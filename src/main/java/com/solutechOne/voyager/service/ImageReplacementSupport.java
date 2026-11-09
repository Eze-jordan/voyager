package com.solutechOne.voyager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Écriture d'images sur disque + nettoyage après validation réelle de la transaction SQL.
 * Les répertoires et les préfixes d'URL sont fournis par les services, jamais par le client.
 */
public final class ImageReplacementSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ImageReplacementSupport.class);
    private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9_-]{1,100}");
    private static final Pattern UUID_WITH_EXTENSION = Pattern.compile(
            "(?i)[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}\\.(?:png|jpg|webp)"
    );

    private ImageReplacementSupport() { }

    /** Valide le type MIME et la signature (magic bytes). Ne compresse pas l'image. */
    public static String extensionValidee(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image manquante");
        }

        String contentType = file.getContentType();
        String extension = switch (contentType == null ? "" : contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "Format non supporté. Utilisez PNG, JPG ou WEBP");
        };

        byte[] signature;
        try (InputStream input = file.getInputStream()) {
            signature = input.readNBytes(12);
        }

        boolean coherent = switch (contentType) {
            case "image/png" -> signature.length >= 8
                    && (signature[0] & 0xff) == 0x89 && signature[1] == 'P'
                    && signature[2] == 'N' && signature[3] == 'G'
                    && (signature[4] & 0xff) == 13 && (signature[5] & 0xff) == 10
                    && (signature[6] & 0xff) == 26 && (signature[7] & 0xff) == 10;
            case "image/jpeg" -> signature.length >= 3
                    && (signature[0] & 0xff) == 0xff
                    && (signature[1] & 0xff) == 0xd8
                    && (signature[2] & 0xff) == 0xff;
            case "image/webp" -> signature.length >= 12
                    && signature[0] == 'R' && signature[1] == 'I'
                    && signature[2] == 'F' && signature[3] == 'F'
                    && signature[8] == 'W' && signature[9] == 'E'
                    && signature[10] == 'B' && signature[11] == 'P';
            default -> false;
        };
        if (!coherent) {
            throw new IllegalArgumentException(
                    "Le contenu du fichier ne correspond pas à une image PNG, JPG ou WEBP valide");
        }
        return extension;
    }

    /** Dossier canonique, avec vérification du sous-dossier de compagnie le cas échéant. */
    public static Path dossier(String configuredRoot, String sousDossier) throws IOException {
        Path base = Path.of(configuredRoot).toAbsolutePath().normalize();
        Files.createDirectories(base);
        Path baseReal = base.toRealPath();

        if (sousDossier == null) {
            return baseReal;
        }
        verifierIdentifiant(sousDossier);
        Path child = baseReal.resolve(sousDossier);
        Files.createDirectories(child);
        Path childReal = child.toRealPath();
        if (!baseReal.equals(childReal.getParent())) {
            throw new SecurityException("Répertoire compagnie hors du dossier autorisé");
        }
        return childReal;
    }

    public static void verifierIdentifiant(String id) {
        if (id == null || !SAFE_ID.matcher(id).matches()) {
            throw new IllegalArgumentException("Identifiant invalide pour un nom de fichier");
        }
    }

    /**
     * IMPORTANT : doit être appelé à l'intérieur d'une transaction Spring active.
     * Après COMMIT : efface uniquement l'ancienne image appartenant à l'entité.
     * Après ROLLBACK : efface la nouvelle image afin d'éviter un fichier orphelin.
     */
    public static String enregistrerEtPlanifierNettoyage(
            MultipartFile file,
            Path dossier,
            String extension,
            String prefixNom,
            String ancienneUrl,
            String prefixUrl
    ) throws IOException {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "Une transaction Spring active est obligatoire pour remplacer une image");
        }
        if (!(".png".equals(extension) || ".jpg".equals(extension)
                || ".webp".equals(extension))) {
            throw new IllegalArgumentException("Extension interdite");
        }
        Path dossierReal = dossier.toRealPath();
        if (!Files.isDirectory(dossierReal)) {
            throw new IllegalArgumentException("Dossier de destination invalide");
        }

        // Ne pas utiliser le nom du fichier transmis par le navigateur.
        String filename = prefixNom + UUID.randomUUID() + extension;
        Path nouveau = dossierReal.resolve(filename);
        Path ancien = ancienFichierAutorise(ancienneUrl, prefixUrl, prefixNom, dossierReal);

        boolean nouveauCree = false;
        try {
            // CREATE_NEW : aucun écrasement de fichier préexistant, même par collision.
            try (OutputStream output = Files.newOutputStream(
                    nouveau, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                nouveauCree = true;
                try (InputStream input = file.getInputStream()) {
                    input.transferTo(output);
                }
            }
        } catch (IOException | RuntimeException ex) {
            if (nouveauCree) {
                try {
                    Files.deleteIfExists(nouveau);
                } catch (IOException suppressed) {
                    ex.addSuppressed(suppressed);
                }
            }
            throw ex;
        }

        try {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    if (ancien != null && !ancien.equals(nouveau)) {
                        supprimerSiFichierRegulier(ancien, "ancienne image");
                    }
                }

                @Override
                public void afterCompletion(int status) {
                    // STATUS_UNKNOWN : ne pas supprimer ; la base a peut-être commité.
                    if (status == STATUS_ROLLED_BACK) {
                        supprimerSiFichierRegulier(nouveau, "nouvelle image (rollback)");
                    }
                }
            });
        } catch (RuntimeException ex) {
            supprimerSiFichierRegulier(nouveau, "nouvelle image (enregistrement transaction impossible)");
            throw ex;
        }

        return prefixUrl + filename;
    }

    private static Path ancienFichierAutorise(
            String url, String prefixUrl, String prefixNom, Path dossierReal) {
        // Ne jamais supprimer des URL externes, chemins absolus ou noms non générés par l'app.
        if (url == null || !url.startsWith(prefixUrl)) {
            return null;
        }
        String name = url.substring(prefixUrl.length());
        if (!name.startsWith(prefixNom)) {
            return null;
        }
        String suffixe = name.substring(prefixNom.length());
        if (!UUID_WITH_EXTENSION.matcher(suffixe).matches()) {
            return null;
        }
        Path candidat = dossierReal.resolve(name).normalize();
        return candidat.getParent().equals(dossierReal) ? candidat : null;
    }

    private static void supprimerSiFichierRegulier(Path chemin, String role) {
        try {
            // NOFOLLOW_LINKS : n'efface jamais la cible d'un lien symbolique.
            if (Files.isRegularFile(chemin, LinkOption.NOFOLLOW_LINKS)) {
                Files.deleteIfExists(chemin);
            }
        } catch (IOException | SecurityException ex) {
            // Évite une réponse HTTP 500 après un COMMIT réussi.
            LOG.warn("Suppression différée impossible pour {} : {}", role, chemin, ex);
        }
    }
}

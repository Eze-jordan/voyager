package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.Invoice;
import com.solutechOne.voyager.model.Manager;
import com.solutechOne.voyager.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Year;

/**
 * Emails Voyager : présentation commune inspirée du modèle monBonDocteur.
 * Les signatures publiques et l'envoi du billet PDF sont conservés.
 */
@Service
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from:noreply@solutech-one.com}")
    private String from;

    // Configurer l'URL réelle du FRONTEND pour afficher le bouton « Se connecter ».
    // Si la propriété est absente, aucun bouton avec un lien fictif n'est ajouté.
    @Value("${app.front.url:}")
    private String frontUrl;

    @Value("${app.support.email:}")
    private String supportEmail;

    private static final String LOGO_ID = "voyageurLogo";
    private static final ClassPathResource LOGO =
            new ClassPathResource("images/logo-voyageur.png");

    // Identité visuelle Voyager : bleu, bleu nuit, fond clair.
    private static final String PRIMARY = "#1769D3";
    private static final String DARK = "#0B2346";
    private static final String TEXT = "#27364B";
    private static final String MUTED = "#64748B";
    private static final String BACKGROUND = "#F3F6FB";
    private static final String BORDER = "#DFE7F1";
    private static final String LIGHT_BLUE = "#EDF5FF";

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // =====================================================
    // MODÈLE UNIQUE POUR TOUS LES EMAILS
    // =====================================================

    private String buildTemplate(String title, String body,
                                 String actionLabel, String actionUrl) {
        String action = (isBlank(actionLabel) || isBlank(actionUrl))
                ? "" : buildActionButton(actionLabel, actionUrl);

        String support = isBlank(supportEmail) ? "" :
                "<br><a href=\"mailto:" + escapeHtml(supportEmail) +
                        "\" style=\"color:" + PRIMARY + ";text-decoration:none;\">" +
                        escapeHtml(supportEmail) + "</a>";

        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Voyageur</title>
            </head>
            <body style="margin:0;padding:0;background-color:%s;font-family:Arial,Helvetica,sans-serif;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                     style="width:100%%;background-color:%s;">
                <tr>
                  <td align="center" style="padding:36px 14px;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                           style="width:100%%;max-width:540px;background-color:#ffffff;
                                  border:1px solid %s;border-radius:10px;overflow:hidden;">
                      <tr>
                        <td align="center" style="background-color:#ffffff;padding:22px 24px 16px;">
                          <img src="cid:%s" alt="Voyageur" width="230"
                               style="display:block;width:230px;max-width:100%%;height:auto;border:0;">
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="background-color:%s;padding:20px 24px;">
                          <h1 style="margin:0;font-size:19px;line-height:1.4;font-weight:700;color:#ffffff;">
                            %s
                          </h1>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding:28px 26px 22px;">%s</td>
                      </tr>
                      %s
                      <tr>
                        <td style="padding:0 26px;">
                          <hr style="border:0;border-top:1px solid %s;margin:0;">
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="padding:19px 26px 23px;">
                          <p style="margin:0 0 6px;color:%s;font-size:13px;font-weight:700;">Voyageur</p>
                          <p style="margin:0;color:%s;font-size:11px;line-height:1.7;">
                            Cet email a été envoyé automatiquement.%s
                          </p>
                        </td>
                      </tr>
                    </table>
                    <p style="margin:15px 0 0;color:%s;font-size:11px;">
                      &copy; %d Voyageur. Tous droits réservés.
                    </p>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(
                BACKGROUND, BACKGROUND, BORDER, LOGO_ID,
                PRIMARY, escapeHtml(title), body, action,
                BORDER, DARK, MUTED, support, MUTED, Year.now().getValue()
        );
    }

    private String buildActionButton(String label, String url) {
        return """
            <tr>
              <td align="center" style="padding:0 26px 28px;">
                <a href="%s" style="display:inline-block;padding:13px 26px;
                  border-radius:7px;background-color:%s;color:#ffffff;
                  font-size:14px;font-weight:700;text-decoration:none;">%s</a>
              </td>
            </tr>
            """.formatted(escapeHtml(url), PRIMARY, escapeHtml(label));
    }

    private String buildCredentialsCard(String name, String role,
                                        String idLabel, String id,
                                        String email, String rawPassword) {
        return """
            <p style="margin:0 0 8px;font-size:15px;color:%s;line-height:1.6;">
              Bonjour <strong style="color:%s;">%s</strong>,
            </p>
            <p style="margin:0 0 22px;font-size:14px;color:%s;line-height:1.7;">
              Votre compte <strong>%s</strong> a été créé avec succès sur
              <strong>Voyageur</strong>. Voici vos informations de connexion.
            </p>
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                   style="border:1px solid %s;border-radius:7px;overflow:hidden;margin-bottom:20px;">
              <tr>
                <td colspan="2" style="padding:11px 16px;background:%s;color:%s;
                    font-size:12px;font-weight:700;letter-spacing:0.4px;">
                  VOS IDENTIFIANTS
                </td>
              </tr>
              <tr>
                <td style="padding:12px 16px 5px;color:%s;font-size:12px;">%s</td>
                <td style="padding:12px 16px 5px;color:%s;font-size:13px;font-weight:700;">%s</td>
              </tr>
              <tr>
                <td style="padding:7px 16px;color:%s;font-size:12px;">Email</td>
                <td style="padding:7px 16px;color:%s;font-size:13px;font-weight:600;word-break:break-word;">%s</td>
              </tr>
              <tr>
                <td style="padding:7px 16px 14px;color:%s;font-size:12px;">Mot de passe temporaire</td>
                <td style="padding:7px 16px 14px;color:%s;font-size:13px;font-weight:700;
                           font-family:'Courier New',monospace;word-break:break-word;">%s</td>
              </tr>
            </table>
            <p style="margin:0 0 12px;font-size:12px;color:%s;line-height:1.7;">
              Pour votre sécurité, modifiez ce mot de passe dès votre première connexion.
            </p>
            <p style="margin:0;font-size:12px;color:%s;line-height:1.7;">
              Si vous n'êtes pas à l'origine de cette création, contactez le support.
            </p>
            """.formatted(
                TEXT, DARK, escapeHtml(name), TEXT, escapeHtml(role),
                BORDER, LIGHT_BLUE, DARK,
                MUTED, escapeHtml(idLabel), DARK, escapeHtml(id),
                MUTED, DARK, escapeHtml(email),
                MUTED, DARK, escapeHtml(rawPassword),
                MUTED, MUTED
        );
    }

    private String buildStatusCard(String name, String id, boolean suspended) {
        String state = suspended ? "suspendu" : "réactivé";
        String color = suspended ? "#C24135" : "#13845B";
        String message = suspended
                ? "Vous n'avez plus accès à votre espace jusqu'à la résolution du problème."
                : "Vous pouvez à nouveau accéder à votre espace et gérer vos opérations.";

        return """
            <p style="margin:0 0 9px;font-size:15px;color:%s;line-height:1.6;">
              Bonjour <strong>%s</strong>,
            </p>
            <p style="margin:0 0 20px;font-size:14px;color:%s;line-height:1.7;">
              Votre compte manager <strong>%s</strong> a été
              <strong style="color:%s;">%s</strong>.
            </p>
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                   style="background:%s;border-radius:7px;margin-bottom:20px;">
              <tr><td style="padding:17px;color:%s;font-size:13px;line-height:1.7;">%s</td></tr>
            </table>
            <p style="margin:0;font-size:12px;color:%s;line-height:1.7;">
              %s
            </p>
            """.formatted(
                TEXT, escapeHtml(name), TEXT, escapeHtml(id), color, state,
                LIGHT_BLUE, DARK, escapeHtml(message), MUTED,
                suspended
                        ? "Contactez l'administrateur ou le support pour plus d'informations."
                        : "Merci de votre collaboration."
        );
    }

    private String buildTicketCard(Invoice invoice) {
        String companyName = invoice.getCompanyName() != null
                ? invoice.getCompanyName() : "Voyageur";
        String amount = invoice.getTotalAmount() != null
                ? invoice.getTotalAmount().toPlainString() : "0";
        String reference = invoice.getPaymentReference() != null
                ? invoice.getPaymentReference() : "-";

        return """
            <p style="margin:0 0 8px;font-size:15px;color:%s;">Bonjour,</p>
            <p style="margin:0 0 20px;font-size:14px;color:%s;line-height:1.7;">
              Votre paiement auprès de <strong>%s</strong> a été confirmé.
              Votre <strong>billet</strong> est disponible en pièce jointe (PDF).
            </p>
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                   style="border:1px solid %s;border-radius:7px;overflow:hidden;margin-bottom:18px;">
              <tr><td colspan="2" style="background:%s;color:%s;padding:11px 16px;
                  font-size:12px;font-weight:700;">DÉTAILS DE VOTRE BILLET</td></tr>
              <tr>
                <td style="padding:12px 16px 7px;color:%s;font-size:12px;">N° de billet</td>
                <td style="padding:12px 16px 7px;color:%s;font-size:13px;font-weight:600;">%s</td>
              </tr>
              <tr>
                <td style="padding:7px 16px;color:%s;font-size:12px;">Montant payé</td>
                <td style="padding:7px 16px;color:%s;font-size:13px;font-weight:600;">%s FCFA</td>
              </tr>
              <tr>
                <td style="padding:7px 16px 14px;color:%s;font-size:12px;">Référence de paiement</td>
                <td style="padding:7px 16px 14px;color:%s;font-size:13px;font-weight:600;">%s</td>
              </tr>
            </table>
            <p style="margin:0;font-size:12px;color:%s;line-height:1.7;">
              Merci d'avoir utilisé Voyageur. Conservez votre billet pour votre voyage.
            </p>
            """.formatted(
                TEXT, TEXT, escapeHtml(companyName), BORDER, LIGHT_BLUE, DARK,
                MUTED, DARK, escapeHtml(invoice.getInvoiceNumber()),
                MUTED, DARK, escapeHtml(amount), MUTED, DARK,
                escapeHtml(reference), MUTED
        );
    }

    // =====================================================
    // ENVOI ET LOGO EMBARQUÉ : NON DÉPENDANT DU VPS
    // =====================================================

    private void sendEmail(String to, String subject, String html,
                           File attachment, String attachmentName) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            if (!LOGO.exists()) {
                throw new IllegalStateException(
                        "Logo manquant : src/main/resources/images/logo-voyageur.png");
            }
            helper.addInline(LOGO_ID, LOGO, "image/png");

            if (attachment != null) {
                helper.addAttachment(attachmentName, new FileSystemResource(attachment));
            }

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Impossible d'envoyer l'email Voyager à " + to, e);
        }
    }

    private void sendEmail(String to, String subject, String html) {
        sendEmail(to, subject, html, null, null);
    }

    private String loginUrl() {
        if (isBlank(frontUrl)) {
            return null;
        }
        String base = frontUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!base.startsWith("https://") && !base.startsWith("http://")) {
            return null;
        }
        return base + "/login";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String escapeHtml(Object value) {
        if (value == null) return "";
        return value.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // =====================================================
    // MÉTHODES PUBLIQUES EXISTANTES : SIGNATURES CONSERVÉES
    // =====================================================

    public void envoyerIdentifiantsUser(User user, String rawPassword) {
        String name = (user.getName() == null ? "" : user.getName()) + " "
                + (user.getFirstname() == null ? "" : user.getFirstname());
        String body = buildCredentialsCard(name.trim(), "utilisateur",
                "ID utilisateur", user.getId(), user.getEmail(), rawPassword);
        String html = buildTemplate("Bienvenue sur Voyageur", body,
                "Se connecter", loginUrl());
        sendEmail(user.getEmail(), "Vos identifiants Voyager", html);
    }

    public void envoyerIdentifiantsCompany(Company company, String rawPassword) {
        String body = buildCredentialsCard(company.getName(), "compagnie",
                "ID compagnie", company.getCompanyId(), company.getEmail(), rawPassword);
        String html = buildTemplate("Bienvenue sur Voyageur", body,
                "Se connecter", loginUrl());
        sendEmail(company.getEmail(), "Vos identifiants Voyager", html);
    }

    public void envoyerIdentifiantsManager(Manager manager, String rawPassword) {
        String name = (manager.getNomManager() == null ? "" : manager.getNomManager()) + " "
                + (manager.getPrenomManager() == null ? "" : manager.getPrenomManager());
        String body = buildCredentialsCard(name.trim(), "manager",
                "ID manager", manager.getIdManager(), manager.getEmail(), rawPassword);
        String html = buildTemplate("Bienvenue sur Voyageur", body,
                "Se connecter", loginUrl());
        sendEmail(manager.getEmail(), "Vos identifiants Voyager", html);
    }

    public void envoyerSuspensionManager(Manager manager) {
        String name = (manager.getNomManager() == null ? "" : manager.getNomManager()) + " "
                + (manager.getPrenomManager() == null ? "" : manager.getPrenomManager());
        String html = buildTemplate("Compte manager suspendu",
                buildStatusCard(name.trim(), manager.getIdManager(), true), null, null);
        sendEmail(manager.getEmail(), "⚠️ Compte Manager suspendu — Voyager", html);
    }

    public void envoyerReactivationManager(Manager manager) {
        String name = (manager.getNomManager() == null ? "" : manager.getNomManager()) + " "
                + (manager.getPrenomManager() == null ? "" : manager.getPrenomManager());
        String html = buildTemplate("Compte manager réactivé",
                buildStatusCard(name.trim(), manager.getIdManager(), false),
                "Accéder à mon espace", loginUrl());
        sendEmail(manager.getEmail(), "✅ Compte Manager réactivé — Voyager", html);
    }

    // Nom « envoyerFacture » conservé pour compatibilité avec les appels existants.
    // Dans les emails et le document joint, l'intitulé est « Billet ».
    public void envoyerFacture(Invoice invoice, String pdfPath) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice is required");
        }
        if (isBlank(invoice.getBuyerEmail())) {
            throw new IllegalStateException(
                    "Aucune adresse email pour le billet " + invoice.getInvoiceNumber());
        }
        if (isBlank(pdfPath)) {
            throw new IllegalArgumentException("PDF path is required");
        }

        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            throw new IllegalStateException("PDF de billet introuvable : " + pdfPath);
        }

        String html = buildTemplate("Confirmation de votre paiement",
                buildTicketCard(invoice), null, null);
        sendEmail(invoice.getBuyerEmail(),
                "Votre billet Voyager - " + invoice.getInvoiceNumber(),
                html, pdfFile, invoice.getInvoiceNumber() + ".pdf");
    }
}


package com.solutechOne.voyager.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class UploadDirectoryConfig {

    @Value("${app.upload.dir.profiles}")
    private String profilesDir;

    @Value("${app.upload.dir.compagnies}")
    private String compagniesDir;

    @Value("${app.upload.dir.documents}")
    private String documentsDir;

    @PostConstruct
    public void init() throws IOException {
        createDirectory(profilesDir);
        createDirectory(compagniesDir);
        createDirectory(documentsDir);
    }

    private void createDirectory(String directory) throws IOException {
        Path path = Path.of(directory).toAbsolutePath().normalize();

        Files.createDirectories(path);

        System.out.println("Répertoire upload prêt : " + path);
    }
}

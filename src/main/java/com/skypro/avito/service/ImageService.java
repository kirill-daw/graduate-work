package com.skypro.avito.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    private final String uploadPath;

    public ImageService(@Value("${app.image.upload-path:./uploads/ads/}") String uploadPath) {
        this.uploadPath = uploadPath;
    }

    @PostConstruct
    public void init() {
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
                log.info("Created upload directory: {}", uploadDir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
            }
        }
        log.info("Upload directory: {}", uploadDir);
    }

    public String saveImage(MultipartFile image) {
        String extension = getExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        Path filePath = Paths.get(uploadPath, filename).toAbsolutePath();
        log.info("Saving image: {}", filename);
        log.info("File path: {}", filePath);
        try {
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File exists after save: {}", Files.exists(filePath));
        } catch (IOException e) {
            log.error("Failed to save image: {}", filePath, e);
            throw new RuntimeException("Failed to save image: " + filename, e);
        }
        return filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}

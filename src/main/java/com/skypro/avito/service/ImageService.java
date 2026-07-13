package com.skypro.avito.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    private final String uploadPath;

    public ImageService(@Value("${app.image.upload-path:./uploads/ads/}") String uploadPath) {
        this.uploadPath = uploadPath;
    }

    @PostConstruct
    public void init() {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
            }
        }
    }

    public String saveImage(MultipartFile image) {
        String extension = getExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        Path path = Paths.get(uploadPath, filename);
        try {
            image.transferTo(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
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

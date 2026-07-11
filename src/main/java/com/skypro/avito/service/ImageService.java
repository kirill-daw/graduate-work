package com.skypro.avito.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageService {

    private final String uploadPath;

    public ImageService(@Value("${app.image.upload-path:./uploads/ads/}") String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String saveImage(MultipartFile image) {
        String extension = getExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        Path path = Path.of(uploadPath, filename);
        try {
            Files.createDirectories(path.getParent());
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

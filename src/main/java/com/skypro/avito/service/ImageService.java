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

    private final String adsDir;
    private final String usersDir;

    public ImageService(@Value("${app.image.upload-path:./uploads/}") String uploadPath) {
        this.uploadPath = uploadPath;
        this.adsDir = uploadPath + "ads/";
        this.usersDir = uploadPath + "users/";
    }

    @PostConstruct
    public void init() {
        createDir(adsDir);
        createDir(usersDir);
    }

    private void createDir(String dir) {
        Path path = Paths.get(dir).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                log.info("Created directory: {}", path);
            } catch (IOException e) {
                throw new RuntimeException("Could not create directory: " + path, e);
            }
        }
        log.info("Directory ready: {}", path);
    }

    public String saveAdImage(MultipartFile image) {
        return saveImage(image, adsDir);
    }

    public String saveAvatar(MultipartFile file) {
        return saveImage(file, usersDir);
    }

    private String saveImage(MultipartFile image, String directory) {
        String extension = getExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        Path filePath = Paths.get(directory, filename).toAbsolutePath().normalize();
        log.info("Saving image: {} to {}", filename, filePath);
        try {
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
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

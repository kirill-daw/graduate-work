package com.skypro.avito.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.image.upload-path:./uploads/}")
    private String uploadPath;

    private String adsDirUrl;
    private String usersDirUrl;

    @PostConstruct
    public void init() {
        adsDirUrl = Paths.get(uploadPath + "ads/").toAbsolutePath().normalize().toUri().toString();
        usersDirUrl = Paths.get(uploadPath + "users/").toAbsolutePath().normalize().toUri().toString();
    }

}

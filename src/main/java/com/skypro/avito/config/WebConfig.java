package com.skypro.avito.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.image.upload-path:./uploads/ads/}")
    private String uploadPath;

    private String absoluteUploadPath;

    @PostConstruct
    public void init() {
        absoluteUploadPath = Paths.get(uploadPath).toAbsolutePath().normalize() + "/";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/ads/**")
                .addResourceLocations("file:" + absoluteUploadPath);
    }
}

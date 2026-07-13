package com.skypro.avito.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer multipartFormCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            if (handlerMethod.getMethod().getName().equals("addAd")) {
                operation.setParameters(null);
                operation.setRequestBody(new RequestBody()
                        .content(new Content()
                                .addMediaType("multipart/form-data",
                                        new MediaType().schema(new Schema<>()
                                                .type("object")
                                                .addProperties("properties",
                                                        new Schema<>()
                                                                .type("string")
                                                                .example("{\"title\":\"Товар\",\"price\":1000,\"description\":\"Описание\"}"))
                                                .addProperties("image",
                                                        new Schema<>()
                                                                .type("string")
                                                                .format("binary"))
                                                .required(List.of("properties", "image"))
                                        ))
                        ));
            }
            return operation;
        };
    }
}

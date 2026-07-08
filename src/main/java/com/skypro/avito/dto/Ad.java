package com.skypro.avito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ad {

    @Schema(description = "id объявления")
    private Integer id;

    @Schema(description = "id автора объявления")
    private Integer author;

    @Schema(description = "цена объявления")
    private Integer price;

    @Schema(description = "заголовок объявления")
    private String title;

    @Schema(description = "описание объявления")
    private String description;

    @Schema(description = "ссылка на картинку объявления")
    private String image;

    @Schema(description = "дата и время создания объявления в миллисекундах с 00:00:00 01.01.1970")
    private Long createdAt;
}

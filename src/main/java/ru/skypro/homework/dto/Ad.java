package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ad {

    @Schema(description = "id объявления")
    private Integer pk;

    @Schema(description = "id автора объявления")
    private Integer author;

    @Schema(description = "цена объявления")
    private Integer price;

    @Schema(description = "заголовок объявления")
    private String title;

    @Schema(description = "ссылка на картинку объявления")
    private String image;
}

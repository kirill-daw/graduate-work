package com.skypro.avito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateComment {

    @Schema(description = "текст комментария", minLength = 8, maxLength = 64)
    private String text;
}

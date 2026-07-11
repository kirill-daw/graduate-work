package com.skypro.avito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Schema(description = "id комментария")
    private Integer id;

    @Schema(description = "id автора комментария")
    private Integer author;

    @Schema(description = "дата и время создания комментария в миллисекундах с 00:00:00 01.01.1970")
    private Long createdAt;

    @Schema(description = "текст комментария")
    private String text;
}

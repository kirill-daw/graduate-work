package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {

    @Schema(description = "общее количество комментариев")
    private Integer count;

    @Schema(description = "список комментариев")
    private List<Comment> results;
}

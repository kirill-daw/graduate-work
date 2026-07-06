package com.skypro.avito.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.skypro.avito.dto.Comment;
import com.skypro.avito.dto.Comments;
import com.skypro.avito.dto.CreateOrUpdateComment;

import java.util.Collections;

@RestController
@RequestMapping("/ads")
public class CommentController {

    @Operation(summary = "Получение комментариев объявления", operationId = "getComments")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comments.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{adId}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable Integer adId) {
        Comments comments = new Comments(0, Collections.emptyList());
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Добавление комментария к объявлению", operationId = "addComment")
    @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PostMapping("/{adId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Integer adId,
                                              @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        Comment comment = new Comment(1, null, null, null, null, createOrUpdateComment.getText());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @Operation(summary = "Удаление комментария", operationId = "deleteComment")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer adId,
                                              @PathVariable Integer commentId) {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновление комментария", operationId = "updateComment")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        Comment comment = new Comment(commentId, null, null, null, null, createOrUpdateComment.getText());
        return ResponseEntity.ok(comment);
    }
}

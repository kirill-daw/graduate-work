package com.skypro.avito.exception;

public class CommentAccessDeniedException extends RuntimeException {

    public CommentAccessDeniedException(Integer id) {
        super("Access denied to comment with id: " + id);
    }
}

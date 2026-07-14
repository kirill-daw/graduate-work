package com.skypro.avito.exception;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(Integer id) {
        super("Comment not found with id: " + id);
    }
}

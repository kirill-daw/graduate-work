package com.skypro.avito.exception;

public class AdAccessDeniedException extends RuntimeException {

    public AdAccessDeniedException(Integer id) {
        super("Access denied to ad with id: " + id);
    }
}

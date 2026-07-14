package com.skypro.avito.exception;

import org.springframework.security.access.AccessDeniedException;

public class AdAccessDeniedException extends AccessDeniedException {

    public AdAccessDeniedException(Integer id) {
        super("Access denied to ad with id: " + id);
    }
}

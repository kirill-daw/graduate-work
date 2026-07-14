package com.skypro.avito.exception;

public class AdNotFoundException extends RuntimeException {

    public AdNotFoundException(Integer id) {
        super("Ad not found with id: " + id);
    }
}

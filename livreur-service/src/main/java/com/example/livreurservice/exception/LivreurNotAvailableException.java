package com.example.livreurservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LivreurNotAvailableException extends RuntimeException {

    public LivreurNotAvailableException(String message) {
        super(message);
    }
}
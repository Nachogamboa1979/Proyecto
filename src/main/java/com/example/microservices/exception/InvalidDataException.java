package com.example.microservices.exception;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}

package com.socialising.services.exceptionHandler;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

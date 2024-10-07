package com.socialising.services.exceptionHandler;

public class PostUpdateException extends RuntimeException {
    public PostUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

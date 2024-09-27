package com.socialising.services.exceptionHandler;

// Custom exception class for User Not Found scenarios
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}


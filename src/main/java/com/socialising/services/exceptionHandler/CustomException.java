package com.socialising.services.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private final Set<String> errorMessages;

    // To use it
    // new throw CustomException( Pass the error messages )
}

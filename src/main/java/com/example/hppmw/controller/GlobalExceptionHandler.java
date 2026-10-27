package com.example.hppmw.controller;

import com.example.hppmw.service.GlobalPayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates service-layer exceptions into HTTP responses, passing through
 * the upstream Global Payments status code and error body unchanged.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GlobalPayException.class)
    public ResponseEntity<String> handleGlobalPayException(GlobalPayException ex) {
        log.error("Global Payments error: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBody());
    }
}

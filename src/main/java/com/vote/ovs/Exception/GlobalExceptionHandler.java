package com.vote.ovs.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("message", ex.getMessage());

        HttpStatus status;

        switch (ex.getMessage()) {
            case "User not found", "Invalid password" -> status = HttpStatus.UNAUTHORIZED;
            case "Username already exists" -> status = HttpStatus.CONFLICT;
            case "User already voted" -> status = HttpStatus.BAD_REQUEST;
            case "Candidate not found" -> status = HttpStatus.NOT_FOUND;
            case "Results not yet released", "Invalid admin secret", "Voting is closed" -> status = HttpStatus.FORBIDDEN;
            case "Candidate name is required", "Party name is required" -> status = HttpStatus.BAD_REQUEST;
            default -> status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());

        return new ResponseEntity<>(body, status);
    }
}

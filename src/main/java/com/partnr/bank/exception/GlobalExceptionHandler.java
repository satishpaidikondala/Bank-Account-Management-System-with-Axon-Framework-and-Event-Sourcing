package com.partnr.bank.exception;

import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientFunds(InsufficientFundsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(AccountClosedException.class)
    public ResponseEntity<Map<String, String>> handleAccountClosed(AccountClosedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFound(AccountNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleIllegalState(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(AggregateNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAggregateNotFound(AggregateNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<Map<String, String>> handleCommandExecution(CommandExecutionException e) {
        if (e.getCause() instanceof InsufficientFundsException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getCause().getMessage()));
        }
        if (e.getCause() instanceof AccountClosedException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getCause().getMessage()));
        }
        if (e.getCause() instanceof IllegalStateException || e.getCause() instanceof IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getCause().getMessage()));
        }
        if (e.getCause() instanceof AggregateNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getCause().getMessage()));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
}

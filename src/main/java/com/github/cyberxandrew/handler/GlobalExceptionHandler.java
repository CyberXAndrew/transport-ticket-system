package com.github.cyberxandrew.handler;

import com.github.cyberxandrew.dto.CustomValidationViolationResponse;
import com.github.cyberxandrew.exception.user.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomValidationViolationResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        CustomValidationViolationResponse customResponse = new CustomValidationViolationResponse(
                LocalDateTime.now().format(formatter),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                errorDetails,
                request.getRequestURI(),
                ex.getClass().getName()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}

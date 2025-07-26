package com.itil.exception;

import com.itil.dto.ApiResponse;
import com.itil.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found: " + ex.getMessage());
        return ResponseEntity.status(404).body(ResponseUtil.error(Collections.singletonList(ex.getMessage()), "Resource not found", 404));
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error("Authentication failed: " + ex.getMessage());
        return ResponseEntity.status(401).body(ResponseUtil.error(Collections.singletonList("Invalid email or password"), "Unauthorized", 401));
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
        logger.error("Bad Request: " + ex.getMessage());
        return ResponseEntity.status(400).body(ResponseUtil.error(Collections.singletonList(ex.getMessage()), "Bad Request", 400));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation Failed: " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> {
                        errors.putIfAbsent(error.getField(), error.getDefaultMessage());});
        return ResponseEntity.status(400).body(ResponseUtil.error(
                new ArrayList<>(errors.values()),
                "Bad Request",
                400));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(error -> error.getPropertyPath() + ": " + error.getMessage())
                .collect(Collectors.toList());

         return ResponseEntity.badRequest().body(ResponseUtil.error(
    errors,
            "Fields are Invalid",
            400));
    }


    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.error( Arrays.asList("Access Denied: " + ex.getMessage()),
                "Forbidden",
                403));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(500).body(ResponseUtil.error(Arrays.asList("An unexpected error occurred: " + ex.getMessage()),
                "Internal Server Error",  500));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex) {
        logger.error("Email Already Exists: " + ex.getMessage());
        return ResponseEntity.status(400)
                .body(ResponseUtil.error(List.of(ex.getMessage()),
                        "Email Already Exists", 400));
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidEnumValueFormatException(
            InvalidFormatException ex) {
        logger.error("Invalid Enum value provided: " + ex.getMessage());
        return ResponseEntity.status(400)
                .body(ResponseUtil.error(List.of(ex.getMessage() + ". Allowed values: [ROLE_TEAMMEMBER, ROLE_USER, ROLE_GATEKEEPER]"),
                        "Bad Request", 400));
    }

    @ExceptionHandler(NoTicketFoundException.class)
    public ResponseEntity<String> handleNoTicketFoundException(NoTicketFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTicketStatusException.class)
    public ResponseEntity<String> handleInvalidTicketStatusException(InvalidTicketStatusException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

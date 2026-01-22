package com.Eqinox.store.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> validation(
            MethodArgumentNotValidException e,
            HttpServletRequest req
    ) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Validation failed");

        ApiError err = ApiError.of("VALIDATION_ERROR", msg, 400, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(err));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> badRequest(
            IllegalArgumentException e,
            HttpServletRequest req
    ) {
        ApiError err = ApiError.of("BAD_REQUEST", e.getMessage(), 400, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(err));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> conflict(
            IllegalStateException e,
            HttpServletRequest req
    ) {
        ApiError err = ApiError.of("CONFLICT", e.getMessage(), 409, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(err));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> runtime(
            RuntimeException e,
            HttpServletRequest req
    ) {
        // keep as 400 for now; later you can split NOT_FOUND vs others
        ApiError err = ApiError.of("RUNTIME_ERROR", e.getMessage(), 400, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(err));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> server(
            Exception e,
            HttpServletRequest req
    ) {
        ApiError err = ApiError.of("INTERNAL_ERROR", "Internal server error", 500, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.fail(err));
    }
}

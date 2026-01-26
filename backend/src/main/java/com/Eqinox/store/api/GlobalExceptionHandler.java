package com.Eqinox.store.api;

import com.Eqinox.store.exceptions.BusinessException;
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(err));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> badRequest(
            IllegalArgumentException e,
            HttpServletRequest req
    ) {
        ApiError err = ApiError.of("BAD_REQUEST", e.getMessage(), 400, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(err));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> conflict(
            IllegalStateException e,
            HttpServletRequest req
    ) {
        ApiError err = ApiError.of("CONFLICT", e.getMessage(), 409, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(err));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> business(
            BusinessException e,
            HttpServletRequest req
    ) {
        ApiError err = ApiError.of(
                "BUSINESS_RULE_VIOLATION",
                e.getMessage(),
                409,
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(err));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> server(
            Exception e,
            HttpServletRequest req
    ) {
        ApiError err = ApiError.of(
                "INTERNAL_ERROR",
                "Internal server error",
                500,
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(err));
    }
}

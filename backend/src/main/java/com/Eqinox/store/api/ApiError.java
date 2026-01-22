package com.Eqinox.store.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String code;       // e.g. "VALIDATION_ERROR", "NOT_FOUND"
    private String message;    // user-friendly message
    private Integer status;    // http status code
    private String path;       // request path
    private Instant timestamp; // error time

    public ApiError() {}

    public ApiError(String code, String message, Integer status, String path, Instant timestamp) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = timestamp;
    }

    public static ApiError of(String code, String message, int status, String path) {
        return new ApiError(code, message, status, path, Instant.now());
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Integer getStatus() { return status; }
    public String getPath() { return path; }
    public Instant getTimestamp() { return timestamp; }

    public void setCode(String code) { this.code = code; }
    public void setMessage(String message) { this.message = message; }
    public void setStatus(Integer status) { this.status = status; }
    public void setPath(String path) { this.path = path; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}

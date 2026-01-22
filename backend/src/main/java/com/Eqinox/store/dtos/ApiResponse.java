package com.Eqinox.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }

    public static ApiResponse failure(String message) {
        return new ApiResponse(false, message);
    }
}

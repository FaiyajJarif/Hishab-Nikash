package com.Eqinox.store.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String email;
    private String password;
    private Boolean rememberMe;
}

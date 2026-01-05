package com.Eqinox.store.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private String dateOfBirth;
    private String timezone;
    private Integer budgetStartDay;
}

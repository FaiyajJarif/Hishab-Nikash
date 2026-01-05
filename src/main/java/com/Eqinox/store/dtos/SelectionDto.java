package com.Eqinox.store.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class SelectionDto {
    private String category;
    private String value;
    private String frequency; // MONTHLY / YEARLY / null
}

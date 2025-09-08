package com.example.pasa_la_pagina.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUser {

    private String email;
    private String firstName;
    private String lastName;
    
}

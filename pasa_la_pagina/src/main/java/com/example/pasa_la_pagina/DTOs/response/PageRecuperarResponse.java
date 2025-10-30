package com.example.pasa_la_pagina.DTOs.response;

import java.util.List;

import lombok.Data;

@Data
public class PageRecuperarResponse {
    private List<?> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

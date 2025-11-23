package com.example.pasa_la_pagina.DTOs.response;

import com.example.pasa_la_pagina.entities.Enum.TipoUpdateNotificacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionUpdateResponse {
    public TipoUpdateNotificacion tipo;
    public Long id;
}

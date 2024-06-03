package com.graffiti.apirestauthservicios.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long idUsuario;
    private String nombre;
    private String apellidos;
    private String username;

}

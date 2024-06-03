package com.graffiti.apirestauthservicios.app.models;

import com.graffiti.apirestauthservicios.auth.models.Usuarios;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "servicios")
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long idServicio;


    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre del servicio no puede estar vacio")
    private String nombre;


    @Column(nullable = false)
    @NotBlank(message = "La descripcion no puede estar vacia")
    private String descripcion;


    private String img;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @NotNull(message = "Asignar un usuario")
    private Usuarios usuario;

}

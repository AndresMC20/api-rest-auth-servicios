package com.graffiti.apirestauthservicios.auth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false)
    @NotBlank(message = "El nombre no puede estar vacio")
    @Pattern(regexp = "^[^0-9]*$", message = "El nombre no debe contener números")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "El apellido no puede estar vacio")
    @Pattern(regexp = "^[^0-9]*$", message = "El apellido no debe contener números")
    private String apellidos;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El username no puede estar vacio")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "El password no puede estar vacio")
    private String password;

    @Transient
    @NotBlank(message = "La confirmacion del password no puede estar vacia")
    private String confirmationPassword;


    //Usamos fetchType en EAGER para que cada vez que se acceda o se extraiga un usuario de la BD, este se traiga todos sus roles
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    /*Con JoinTable estaremos creando una tabla que unirá la tabla de usuario y role, con lo cual tendremos un total de 3 tablas
    relacionadas en la tabla "usuarios_roles", a través de sus columnas usuario_id que apuntara al ID de la tabla usuario
    y role_id que apuntara al Id de la tabla role */
    @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id_usuario")
            ,inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id_role"))
    private List<Roles> roles = new ArrayList<>();
}

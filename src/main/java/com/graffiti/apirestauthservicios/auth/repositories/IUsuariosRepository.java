package com.graffiti.apirestauthservicios.auth.repositories;

import com.graffiti.apirestauthservicios.auth.models.Usuarios;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuariosRepository extends CrudRepository<Usuarios, Long> {
    // Verificar si la persona ya existe en la base de datos
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuarios u WHERE u.nombre = :nombre AND u.apellidos = :apellidos")
    boolean existsByNombreAndApellidos(@Param("nombre") String nombre, @Param("apellidos") String apellidos);

    // Verificar si ya existe el usuario en la base de datos
    boolean existsByUsername(String username);



    // Verificar si la persona ya existe en la base de datos excluyendo el id
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuarios u WHERE u.nombre = :nombre AND u.apellidos = :apellidos AND u.id <> :id")
    boolean existsByFullNameExcludingId(@Param("nombre") String nombre, @Param("apellidos") String apellidos, @Param("id") Long id);

    // Verificar si el username ya existe en la base de datos excluyendo el id
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuarios u WHERE u.username = :username AND u.id <> :id")
    boolean existsByUsernameExcludingId(@Param("username") String username, @Param("id") Long id);



    // Esto es para el security
    Optional<Usuarios> findByUsername(String username);

}

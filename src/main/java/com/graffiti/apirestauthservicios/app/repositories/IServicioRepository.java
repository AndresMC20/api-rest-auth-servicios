package com.graffiti.apirestauthservicios.app.repositories;

import com.graffiti.apirestauthservicios.app.models.Servicio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IServicioRepository extends CrudRepository<Servicio, Long> {

    // Verificar si el nombre existe en la base de datos
    boolean existsByNombre(String nombre);

}

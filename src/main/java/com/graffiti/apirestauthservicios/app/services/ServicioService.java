package com.graffiti.apirestauthservicios.app.services;

import com.graffiti.apirestauthservicios.app.models.Servicio;
import com.graffiti.apirestauthservicios.app.repositories.IServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private IServicioRepository servicioRepository;


    // Obtener toda la lista de servicios
    public List<Servicio> findAll(){
        return (List<Servicio>) servicioRepository.findAll();
    }


    // Obtener UN solo servicio
    public Servicio findById(Long id){
        return servicioRepository.findById(id).orElse(null);
    }


    // Guardar y/o actualizar un servicio
    public Servicio save(Servicio servicio){
        return servicioRepository.save(servicio);
    }


    // Borrar un servicio
    public void delete(Long id){
        servicioRepository.deleteById(id);
    }


    // Verificar si el nombre existe en la base de datos
    public boolean existsByNombre(String nombre){
        return servicioRepository.existsByNombre(nombre);
    }


}

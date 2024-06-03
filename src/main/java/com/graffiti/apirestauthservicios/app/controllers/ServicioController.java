package com.graffiti.apirestauthservicios.app.controllers;

import com.graffiti.apirestauthservicios.app.models.Servicio;
import com.graffiti.apirestauthservicios.app.services.ServicioService;
import com.graffiti.apirestauthservicios.auth.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {
    @Autowired
    private ServicioService servicioService;


    // Obtener toda la lista de servicios
    @GetMapping()
    public List<Servicio> index(){
        return servicioService.findAll();
    }


    // Obtener UN servicio
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){
        Servicio servicio = servicioService.findById(id);

        Map<String, Object> response = new HashMap<>();

        if(servicio == null){
            response.put("error", "El servicio no fue encontrado");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Servicio>(servicio, HttpStatus.OK);

    }

    // Guardar servicio
    @PostMapping("/crear")
    public ResponseEntity<?> create(@Valid @RequestBody Servicio request, BindingResult result){
        Servicio servicio = null;

        Map<String, Object> response = new HashMap<>();

        // Para los errores de los models
        if (result.hasErrors()){
            List<String> errors = new ArrayList<>();
            for (FieldError err : result.getFieldErrors()){
                errors.add(err.getDefaultMessage());
            }
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar si el nombre existe
        if(servicioService.existsByNombre(request.getNombre())){
            response.put("error", "El nombre del servicio ya existe");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        try {
            servicio = servicioService.save(request);
        } catch (DataAccessException e) {
            response.put("error", "No se pudo crear el servicio: " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("error", "Error inesperado: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El servicio fue creado exitosamente");
        response.put("Servicio", servicio);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    // Actualizar servicio
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Servicio request, BindingResult result, @PathVariable Long id){
        Servicio servicio = servicioService.findById(id);

        Map<String, Object> response = new HashMap<>();

        if(servicio == null){
            response.put("error", "El servicio no fue encontrado para actualizarlo");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Para los errores de los models
        if(result.hasErrors()){
            List<String> errors = new ArrayList<>();
            for(FieldError err : result.getFieldErrors()){
                errors.add(err.getDefaultMessage());
            }
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Servicio servicioActualizado = null;

        try{
            servicio.setNombre(request.getNombre());
            servicio.setDescripcion(request.getDescripcion());
            servicio.setImg(request.getImg());
            servicioActualizado = servicioService.save(servicio);
        }catch (DataAccessException e){
            response.put("error", "El servicio no se pudo actualizar");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El servicio fue actualizado exitosamente");
        response.put("Servicio", servicioActualizado);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


    // Borrar un servicio
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Servicio servicio = servicioService.findById(id);

        Map<String, Object> response = new HashMap<>();

        if(servicio == null){
            response.put("error", "E; servicio no fue encontrado para eliminarlo");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            servicioService.delete(id);
        }catch (DataAccessException e){
            response.put("error", "El servicio no se pudo eliminar");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El servicio fue eliminado exitosamente");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}

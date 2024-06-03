package com.graffiti.apirestauthservicios.app.controllers;

import com.graffiti.apirestauthservicios.app.models.Servicio;
import com.graffiti.apirestauthservicios.app.services.ServicioService;
import com.graffiti.apirestauthservicios.auth.models.Usuarios;
import com.graffiti.apirestauthservicios.auth.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {
    @Autowired
    private ServicioService servicioService;

    @Autowired
    private UsuarioService usuarioService;


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
    public ResponseEntity<?> create(@RequestParam("imagen") MultipartFile file,
                                    @RequestParam("nombre") String nombre,
                                    @RequestParam("descripcion") String descripcion,
                                    @RequestParam("usuarioId") Long usuarioId){

        Map<String, Object> response = new HashMap<>();

        // Validar los parámetros
        if (nombre.isEmpty() || descripcion.isEmpty() || usuarioId == null) {
            response.put("error", "Todos los campos son requeridos");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar si el nombre existe
        if (servicioService.existsByNombre(nombre)) {
            response.put("error", "El nombre del servicio ya existe");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar si el usuario existe
        Usuarios usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            response.put("error", "El usuario no existe");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        try {
            // Guardar el archivo de imagen
            String imgPath = servicioService.handleFileUpload(file, null);

            // Crear el servicio
            Servicio servicio = new Servicio();
            servicio.setNombre(nombre);
            servicio.setDescripcion(descripcion);
            servicio.setImg(imgPath);
            servicio.setUsuario(usuario);

            servicioService.save(servicio);

            response.put("mensaje", "El servicio fue creado exitosamente");
            response.put("servicio", servicio);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("error", "No se pudo crear el servicio: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // Actualizar servicio
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestParam("imagen") MultipartFile file,
                                    @RequestParam("nombre") String nombre,
                                    @RequestParam("descripcion") String descripcion,
                                    @RequestParam("usuarioId") Long usuarioId){
        Servicio servicio = servicioService.findById(id);

        Map<String, Object> response = new HashMap<>();

        if(servicio == null){
            response.put("error", "El servicio no fue encontrado para actualizarlo");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Validar los parámetros
        if (nombre.isEmpty() || descripcion.isEmpty() || usuarioId == null) {
            response.put("error", "Todos los campos son requeridos");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar si el nombre ya está en uso por otro servicio
        if (!servicio.getNombre().equals(nombre) && servicioService.existsByNombre(nombre)) {
            response.put("error", "El nombre del servicio ya existe");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar si el usuario existe
        Usuarios usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            response.put("error", "El usuario no existe");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Si se proporciona una nueva imagen, manejar la subida del archivo
            if (file != null && !file.isEmpty()) {
                String existingImgPath = servicio.getImg();
                String newImgPath = servicioService.handleFileUpload(file, existingImgPath);
                servicio.setImg(newImgPath);
            }

            // Actualizar el servicio
            servicio.setNombre(nombre);
            servicio.setDescripcion(descripcion);
            servicio.setUsuario(usuario);

            Servicio servicioActualizado = servicioService.save(servicio);

            response.put("mensaje", "El servicio fue actualizado exitosamente");
            response.put("servicio", servicioActualizado);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "No se pudo actualizar el servicio: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    // Borrar un servicio
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Servicio servicio = servicioService.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (servicio == null) {
            response.put("error", "El servicio no fue encontrado para eliminarlo");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Eliminar el archivo de imagen asociado
            String imgPath = servicio.getImg();
            if (imgPath != null && !imgPath.isEmpty()) {
                File imgFile = new File(imgPath);
                if (imgFile.exists()) {
                    if (!imgFile.delete()) {
                        response.put("error", "No se pudo eliminar el archivo de imagen asociado");
                        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }

            // Eliminar el servicio
            servicioService.delete(id);

        } catch (DataAccessException e) {
            response.put("error", "El servicio no se pudo eliminar");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El servicio fue eliminado exitosamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}

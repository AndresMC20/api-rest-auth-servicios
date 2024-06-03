package com.graffiti.apirestauthservicios.auth.controllers;

import com.graffiti.apirestauthservicios.auth.dtos.UserDTO;
import com.graffiti.apirestauthservicios.auth.models.Roles;
import com.graffiti.apirestauthservicios.auth.models.Usuarios;
import com.graffiti.apirestauthservicios.auth.repositories.IRolesRepository;
import com.graffiti.apirestauthservicios.auth.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private IRolesRepository rolesRepository;



    // Obtener todos los usuarios
    @GetMapping
    public List<UserDTO> index() {
        List<Usuarios> usuarios = usuarioService.findAll();
        return usuarios.stream()
                .map(usuario -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setIdUsuario(usuario.getIdUsuario());
                    userDTO.setNombre(usuario.getNombre());
                    userDTO.setApellidos(usuario.getApellidos());
                    userDTO.setUsername(usuario.getUsername());
                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    // Obtener UN usuario
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){
        Usuarios usuario = usuarioService.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (usuario == null){
            response.put("mensaje", "El usuario no fue encontrado");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<UserDTO>(usuarioService.responseUserDTO(usuario), HttpStatus.OK);
    }


    //Método para poder guardar usuarios de tipo ADMIN
    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody Usuarios request, BindingResult result) {
        Usuarios usuario = null;

        Map<String, Object> response = new HashMap<>();

        // Para los errores de los models
        if (result.hasErrors()){
            List<String> errors = new ArrayList<>();
            for (FieldError err : result.getFieldErrors()){
                errors.add(err.getDefaultMessage());
            }
            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // Para ver si la persona ya existe en la base de datos
        if (usuarioService.existsByFullName(request.getNombre(), request.getApellidos())) {
            response.put("mensaje", "El usuario con ese nombre completo ya existe");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // Para ver si el username existe en la base de datos
        if(usuarioService.existsByUsername(request.getUsername())){
            response.put("mensaje", "El usuario ya existe, intenta con otro username");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // Para la comparación de passwords
        if (!request.getPassword().equals(request.getConfirmationPassword())) {
            response.put("mensaje", "El password y la confirmación no coinciden");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Roles roles = rolesRepository.findByName("ADMIN").get();
            request.setRoles(Collections.singletonList(roles));
            usuario = usuarioService.save(request);
        }catch (DataAccessException e){
            response.put("mensaje", "El usuario no se pudo crear");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido creado exitosamente");
        response.put("Usuario", usuarioService.responseUserDTO(usuario));
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

    }

    // Actualizar un usuario
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Usuarios request, BindingResult result, @PathVariable Long id){
        Usuarios usuario = usuarioService.findById(id);

        Map<String, Object> response = new HashMap<>();

        if(usuario == null){
            response.put("error", "El usuario no fue encontrado");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // Para los errores de los models
        if (result.hasErrors()){
            List<String> errors = new ArrayList<>();
            for (FieldError err : result.getFieldErrors()){
                errors.add(err.getDefaultMessage());
            }
            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // Para ver si la persona ya existe en la base de datos (excluyendo al usuario actual)
        if (usuarioService.existsByFullNameExcludingId(request.getNombre(), request.getApellidos(), id)) {
            response.put("mensaje", "El usuario con ese nombre completo ya existe");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // Para ver si el username ya existe en la base de datos (excluyendo al usuario actual)
        if (usuarioService.existsByUsernameExcludingId(request.getUsername(), id)) {
            response.put("mensaje", "El usuario ya existe, intenta con otro username");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }


        // Para la comparación de passwords
        if (!request.getPassword().equals(request.getConfirmationPassword())) {
            response.put("mensaje", "El password y la confirmación no coinciden");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        Usuarios usuarioActualizado = null;

        try {
            usuario.setNombre(request.getNombre());
            usuario.setApellidos(request.getApellidos());
            usuario.setUsername(request.getUsername());
            usuario.setPassword(request.getPassword());
            usuario.setConfirmationPassword(request.getConfirmationPassword());

            usuarioActualizado = usuarioService.save(usuario);

        }catch (DataAccessException e){
            response.put("error", "El usuario no se pudo actualizar");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido actualizado exitosamente");
        response.put("Usuario Actualizado", usuarioService.responseUserDTO(usuarioActualizado));
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

    }

    // Borrar un usuario
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();

        try {
            usuarioService.delete(id);
        }catch (DataAccessException e){
            response.put("error", "El usuario no se pudo eliminar");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario fue eliminado exitosamente");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

    }

}

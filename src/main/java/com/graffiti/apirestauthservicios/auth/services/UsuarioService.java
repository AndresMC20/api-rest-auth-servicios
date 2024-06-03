package com.graffiti.apirestauthservicios.auth.services;

import com.graffiti.apirestauthservicios.auth.dtos.UserDTO;
import com.graffiti.apirestauthservicios.auth.models.Usuarios;
import com.graffiti.apirestauthservicios.auth.repositories.IRolesRepository;
import com.graffiti.apirestauthservicios.auth.repositories.IUsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private IUsuariosRepository usuariosRepository;
    @Autowired
    private IRolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Devolver toda la lista de usuarios
    public List<Usuarios> findAll() {
        return (List<Usuarios>) usuariosRepository.findAll();
    }

    // Devolver un solo usuario por ID
    public Usuarios findById(Long id) {
        return usuariosRepository.findById(id).orElse(null);
    }

    // Guardar o actualizar un usuario
    public Usuarios save(Usuarios usuario) {
        String encodedPassword = passwordEncoder.encode(usuario.getPassword()); // Encriptar la contraseña antes de guardar
        usuario.setPassword(encodedPassword);

        return usuariosRepository.save(usuario);
    }

    // Borrar un usuario por ID
    public void delete(Long id) {
        usuariosRepository.deleteById(id);
    }

    // Verificar si la persona ya existe en la base de datos
    public boolean existsByFullName(String nombre, String apellidos) {
        return usuariosRepository.existsByNombreAndApellidos(nombre, apellidos);
    }

    //Verificar si el username existe
    public boolean existsByUsername(String username){
        return usuariosRepository.existsByUsername(username);
    }

    // Devolver la respuesta en formato UserDTO
    public UserDTO responseUserDTO(Usuarios usuario){
        UserDTO userDTO = new UserDTO();
        userDTO.setIdUsuario(usuario.getIdUsuario());
        userDTO.setNombre(usuario.getNombre());
        userDTO.setApellidos(usuario.getApellidos());
        userDTO.setUsername(usuario.getUsername());

        return userDTO;

    }


    // Verificar si la persona ya existe en la base de datos pero ignorar el ID
    public boolean existsByFullNameExcludingId(String nombre, String apellidos, Long id) {
        return usuariosRepository.existsByFullNameExcludingId(nombre, apellidos, id);
    }

    // Verificar si el username ya existe en la base de datos pero ignorar el ID
    public boolean existsByUsernameExcludingId(String username, Long id) {
        return usuariosRepository.existsByUsernameExcludingId(username, id);
    }



}

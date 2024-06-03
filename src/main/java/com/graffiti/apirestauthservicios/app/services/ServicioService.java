package com.graffiti.apirestauthservicios.app.services;

import com.graffiti.apirestauthservicios.app.models.Servicio;
import com.graffiti.apirestauthservicios.app.repositories.IServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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


    // Para la img
    public String handleFileUpload(MultipartFile file, String existingFilePath) throws Exception {
        try {
            byte[] bytes = file.getBytes();
            String fileOriginalName = file.getOriginalFilename();

            long fileSize = file.getSize();
            long maxFileSize = 5 * 1024 * 1024;

            if (fileSize > maxFileSize) {
                throw new Exception("File size must be less than or equal to 5MB");
            }

            if (!fileOriginalName.endsWith(".jpg") && !fileOriginalName.endsWith(".jpeg") && !fileOriginalName.endsWith(".png")) {
                throw new Exception("Only JPG, JPEG, PNG files are allowed");
            }

            File folder = new File("src/main/resources/picture");

            if (!folder.exists()) {
                folder.mkdirs();
            }

            // If updating, delete the existing file
            if (existingFilePath != null) {
                File existingFile = new File(existingFilePath);
                if (existingFile.exists()) {
                    existingFile.delete();
                }
            }

            Path path = Paths.get("src/main/resources/picture/" + fileOriginalName);
            Files.write(path, bytes);

            return path.toString();

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }



}

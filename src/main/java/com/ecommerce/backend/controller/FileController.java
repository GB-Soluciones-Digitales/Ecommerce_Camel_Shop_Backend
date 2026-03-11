package com.ecommerce.backend.controller;

import com.ecommerce.backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "*") 
public class FileController {
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Subir a Cloudinary
            String fileUrl = cloudinaryService.uploadFile(file);
            
            // Responder al Frontend
            Map<String, String> response = new HashMap<>();
            
            response.put("filename", fileUrl); 
            
            response.put("url", fileUrl);
            response.put("message", "Imagen subida a Cloudinary");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error subiendo imagen: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
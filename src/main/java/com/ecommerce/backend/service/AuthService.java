package com.ecommerce.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.LoginRequest;
import com.ecommerce.backend.dto.LoginResponse;
import com.ecommerce.backend.model.Usuario;
import com.ecommerce.backend.repository.UsuarioRepository;
import com.ecommerce.backend.security.JwtUtil;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        
        return LoginResponse.builder()
            .token(token)
            .tipo("Bearer")
            .id(usuario.getId())
            .username(usuario.getUsername())
            .email(usuario.getEmail())
            .nombreCompleto(usuario.getNombreCompleto())
            .rol(usuario.getRol().name())
            .build();
    }
    
    // Método para crear usuario inicial
    public Usuario crearUsuarioInicial(String username, String password, String email, String nombreCompleto) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya existe");
        }
        
        Usuario usuario = Usuario.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .email(email)
            .nombreCompleto(nombreCompleto)
            .activo(true)
            .build();
        
        return usuarioRepository.save(usuario);
    }
}
package com.example.enviexpress.config;

import com.example.enviexpress.model.Usuario;
import com.example.enviexpress.repository.UsuarioRepository;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository repo) {
        return args -> {
            if (repo.findByUserName("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUserName("admin");
                admin.setPassword(new BCryptPasswordEncoder().encode("123"));
                admin.setRol("ADMIN");
                admin.setNombre("Administrador");
                admin.setApellido("Principal");
                admin.setCorreo("admin@correo.com");
                admin.setActivo(true);
                admin.setFechaCreacion(LocalDateTime.now());
                admin.setTelefono("3244434432");
                repo.save(admin);
                System.out.println("✅ Usuario admin creado con éxito");
            } else {
                System.out.println("ℹ️ Usuario admin ya existe");
            }
        };
    }
}

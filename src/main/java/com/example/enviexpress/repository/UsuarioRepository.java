package com.example.enviexpress.repository; 

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.enviexpress.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUserName(String username);
}

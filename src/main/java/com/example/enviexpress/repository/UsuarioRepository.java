package com.example.enviexpress.repository;

import com.example.enviexpress.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUserName(String userName);
    List<Usuario> findByRol(String rol);  // ← AGREGAR ESTE MÉTODO
}
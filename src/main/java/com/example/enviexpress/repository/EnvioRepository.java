package com.example.enviexpress.repository;

import com.example.enviexpress.model.Envio;
import com.example.enviexpress.model.EstadoEnvio;
import com.example.enviexpress.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByNumeroGuia(String numeroGuia);
    List<Envio> findByCliente(Usuario cliente);
    List<Envio> findByMensajero(Usuario mensajero);
    List<Envio> findByEstado(EstadoEnvio estado);
}
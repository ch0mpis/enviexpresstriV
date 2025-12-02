package com.example.enviexpress.repository;

import com.example.enviexpress.model.Seguimiento;
import com.example.enviexpress.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {
    List<Seguimiento> findByEnvioOrderByFechaDesc(Envio envio);
}
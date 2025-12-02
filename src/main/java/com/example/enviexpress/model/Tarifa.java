package com.example.enviexpress.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tarifas")
public class Tarifa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 20)
    private String tipo;
    
    @Column(name = "precio_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioKg;
    
    // Relaciones
    @OneToMany(mappedBy = "tarifa")
    private List<Envio> envios;
}
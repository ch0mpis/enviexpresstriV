package com.example.enviexpress.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehiculo")
public class Vehiculo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehiculo_id")
    private Long vehiculoId;
    
    @Column(nullable = false, unique = true, length = 10)
    private String placa;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;  // ← Cambiado aquí
    
    @Column(name = "capacidad_kg", nullable = false)
    private Double capacidadKg;
    
    // Relaciones
    @OneToMany(mappedBy = "vehiculo")
    private List<Envio> envios;

}
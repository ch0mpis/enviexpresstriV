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
@Table(name = "rutas")
public class Ruta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ruta_id")
    private Long rutaId;
    
    @Column(name = "nombre_sector", length = 150)
    private String nombreSector;
    
    @Column(length = 100)
    private String ciudad;
    
    // Relaciones
    @OneToMany(mappedBy = "ruta")
    private List<Envio> envios;
}
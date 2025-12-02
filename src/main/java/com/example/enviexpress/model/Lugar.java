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
@Table(name = "lugares")
public class Lugar {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lugar_id")
    private Long lugarId;
    
    @Column(length = 255)
    private String direccion;
    
    @Column(length = 100)
    private String ciudad;
    
    @Column(length = 255)
    private String referencia;
    
    // Relaciones
    @OneToMany(mappedBy = "lugarRecogida")
    private List<Envio> enviosRecogida;
    
    @OneToMany(mappedBy = "lugarEntrega")
    private List<Envio> enviosEntrega;
}
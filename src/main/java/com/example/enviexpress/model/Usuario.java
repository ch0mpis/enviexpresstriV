package com.example.enviexpress.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @Column(nullable = false, unique = true, length = 120)
    private String correo;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(nullable = false)
    private String rol;
    
    @Column(nullable = false)
    private Boolean activo;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    // Relaciones
    @OneToMany(mappedBy = "cliente")
    private List<Envio> enviosComoCliente;
    
    @OneToMany(mappedBy = "mensajero")
    private List<Envio> enviosComoMensajero;
}
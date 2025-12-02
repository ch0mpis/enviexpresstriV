package com.example.enviexpress.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "envios")
public class Envio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "envio_id")
    private Long envioId;
    
    @Column(name = "numero_guia", length = 20)
    private String numeroGuia;
    
    // Relación con cliente
    @ManyToOne
    @JoinColumn(name = "usuario_cliente_id")
    private Usuario cliente;
    
    // Relación con mensajero
    @ManyToOne
    @JoinColumn(name = "usuario_mensajero_id")
    private Usuario mensajero;
    
    // Relación con lugar de recogida
    @ManyToOne
    @JoinColumn(name = "lugar_recogida_id")
    private Lugar lugarRecogida;
    
    // Relación con lugar de entrega
    @ManyToOne
    @JoinColumn(name = "lugar_entrega_id")
    private Lugar lugarEntrega;
    
    // Relación con ruta
    @ManyToOne
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;
    
    // Relación con vehículo
    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", nullable = false)
    private TipoServicio tipoServicio;
    
    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio;
    
    @Column(columnDefinition = "TEXT")
    private String instrucciones;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnvio estado;
    
    // Relación con tarifa
    @ManyToOne
    @JoinColumn(name = "tarifa_id")
    private Tarifa tarifa;
    
    // Relación con seguimientos
    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL)
    private List<Seguimiento> seguimientos;

}
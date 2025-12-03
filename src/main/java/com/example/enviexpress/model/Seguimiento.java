    package com.example.enviexpress.model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import java.time.LocalDateTime;

    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(name = "seguimientos")
    public class Seguimiento {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "seguimiento_id")
        private Long seguimientoId;
        
        @ManyToOne
        @JoinColumn(name = "envio_id", nullable = false)
        private Envio envio;
        
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EstadoEnvio estado;
        
        @Column(length = 255)
        private String descripcion;
        
        @Column(nullable = false)
        private LocalDateTime fecha;
    }
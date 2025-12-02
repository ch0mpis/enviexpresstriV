package com.example.enviexpress.model;

public enum EstadoEnvio {
    Registrado,
    En_Bodega("En Bodega"),
    En_Ruta("En Ruta"),
    En_Destino("En Destino"),
    Entregado,
    Fallido;
    
    private String displayName;
    
    EstadoEnvio() {
        this.displayName = this.name();
    }
    
    EstadoEnvio(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
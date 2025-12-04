package com.example.enviexpress.controller;

import com.example.enviexpress.model.*;
import com.example.enviexpress.repository.*;
import com.example.enviexpress.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/envios")
public class EnvioController {

    @Autowired
    private EnvioRepository envioRepo;
    
    @Autowired
    private UsuarioRepository usuarioRepo;
    
    @Autowired
    private VehiculoRepository vehiculoRepo;
    
    @Autowired
    private TarifaRepository tarifaRepo;
    
    @Autowired
    private RutaRepository rutaRepo;
    
    @Autowired
    private LugarRepository lugarRepo;

    @Autowired
    private PdfGenerator pdfGenerator;

    // ============= CRUD ENVÍOS =============

    // Listar todos los envíos
    @GetMapping
    public String listar(Model model, Authentication auth) {
        String username = auth.getName();
        Usuario usuario = usuarioRepo.findByUserName(username).orElse(null);
        
        List<Envio> envios;
        
        // Si es admin, muestra todos. Si es cliente, solo sus envíos. Si es mensajero, los asignados a él
        if (usuario != null && usuario.getRol().equals("ROLE_ADMIN")) {
            envios = envioRepo.findAll();
        } else if (usuario != null && usuario.getRol().equals("ROLE_CLIENTE")) {
            envios = envioRepo.findByCliente(usuario);
        } else if (usuario != null && usuario.getRol().equals("ROLE_MENSAJERO")) {
            envios = envioRepo.findByMensajero(usuario);
        } else {
            envios = List.of();
        }
        
        model.addAttribute("envios", envios);
        model.addAttribute("rol", usuario != null ? usuario.getRol() : "");
        return "envios";
    }

    // Formulario para nuevo envío
    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication auth) {
        String username = auth.getName();
        Usuario usuario = usuarioRepo.findByUserName(username).orElse(null);
        
        Envio envio = new Envio();
        
        // Si es cliente, pre-asignar como cliente del envío
        if (usuario != null && usuario.getRol().equals("ROLE_CLIENTE")) {
            envio.setCliente(usuario);
        }
        
        model.addAttribute("envio", envio);
        cargarDatosFormulario(model);
        return "form-envio";
    }

    // Guardar envío
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Envio envio, 
                         @RequestParam(required = false) String nuevaDireccionRecogida,
                         @RequestParam(required = false) String nuevaCiudadRecogida,
                         @RequestParam(required = false) String nuevaReferenciaRecogida,
                         @RequestParam(required = false) String nuevaDireccionEntrega,
                         @RequestParam(required = false) String nuevaCiudadEntrega,
                         @RequestParam(required = false) String nuevaReferenciaEntrega,
                         Authentication auth) {
        
        String username = auth.getName();
        Usuario usuarioActual = usuarioRepo.findByUserName(username).orElse(null);
        boolean esAdmin = usuarioActual != null && usuarioActual.getRol().equals("ROLE_ADMIN");
        
        // Si es edición, cargar el envío existente y preservar campos
        if (envio.getEnvioId() != null) {
            Envio envioExistente = envioRepo.findById(envio.getEnvioId())
                .orElseThrow(() -> new IllegalArgumentException("Envío no encontrado"));
            
            envio.setNumeroGuia(envioExistente.getNumeroGuia());
            envio.setFechaCreacion(envioExistente.getFechaCreacion());
            
            if (!esAdmin) {
                envio.setCliente(envioExistente.getCliente());
                envio.setMensajero(envioExistente.getMensajero());
                envio.setEstado(envioExistente.getEstado());
                envio.setVehiculo(envioExistente.getVehiculo());
                envio.setRuta(envioExistente.getRuta());
            } else {
                if (envio.getEstado() == null) {
                    envio.setEstado(envioExistente.getEstado());
                }
            }
        } else {
            // Si es nuevo envío
            envio.setFechaCreacion(LocalDateTime.now());
            envio.setEstado(EstadoEnvio.Registrado);
            
            String numeroGuia = "ENV" + String.format("%06d", (int)(Math.random() * 999999));
            envio.setNumeroGuia(numeroGuia);
            
            // Si es cliente, asignarlo automáticamente
            if (!esAdmin && usuarioActual != null) {
                envio.setCliente(usuarioActual);
            }
        }
        
        // IMPORTANTE: Cargar el cliente completo desde la BD
        if (envio.getCliente() != null && envio.getCliente().getIdUsuario() != null) {
            Usuario clienteCompleto = usuarioRepo.findById(envio.getCliente().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            envio.setCliente(clienteCompleto);
        }
        
        // IMPORTANTE: Cargar el mensajero completo desde la BD (si existe)
        if (envio.getMensajero() != null && envio.getMensajero().getIdUsuario() != null) {
            Usuario mensajeroCompleto = usuarioRepo.findById(envio.getMensajero().getIdUsuario())
                .orElse(null);
            envio.setMensajero(mensajeroCompleto);
        } else {
            envio.setMensajero(null);
        }
        
        // Manejar lugar de recogida (nuevo o existente)
        if (nuevaDireccionRecogida != null && !nuevaDireccionRecogida.trim().isEmpty()) {
            Lugar nuevoLugarRecogida = new Lugar();
            nuevoLugarRecogida.setDireccion(nuevaDireccionRecogida);
            nuevoLugarRecogida.setCiudad(nuevaCiudadRecogida != null ? nuevaCiudadRecogida : "Bogotá");
            nuevoLugarRecogida.setReferencia(nuevaReferenciaRecogida);
            lugarRepo.save(nuevoLugarRecogida);
            envio.setLugarRecogida(nuevoLugarRecogida);
        } else if (envio.getLugarRecogida() != null && envio.getLugarRecogida().getLugarId() != null) {
            Lugar lugarRecogidaCompleto = lugarRepo.findById(envio.getLugarRecogida().getLugarId())
                .orElseThrow(() -> new IllegalArgumentException("Lugar de recogida no encontrado"));
            envio.setLugarRecogida(lugarRecogidaCompleto);
        }
        
        // Manejar lugar de entrega (nuevo o existente)
        if (nuevaDireccionEntrega != null && !nuevaDireccionEntrega.trim().isEmpty()) {
            Lugar nuevoLugarEntrega = new Lugar();
            nuevoLugarEntrega.setDireccion(nuevaDireccionEntrega);
            nuevoLugarEntrega.setCiudad(nuevaCiudadEntrega != null ? nuevaCiudadEntrega : "Bogotá");
            nuevoLugarEntrega.setReferencia(nuevaReferenciaEntrega);
            lugarRepo.save(nuevoLugarEntrega);
            envio.setLugarEntrega(nuevoLugarEntrega);
        } else if (envio.getLugarEntrega() != null && envio.getLugarEntrega().getLugarId() != null) {
            Lugar lugarEntregaCompleto = lugarRepo.findById(envio.getLugarEntrega().getLugarId())
                .orElseThrow(() -> new IllegalArgumentException("Lugar de entrega no encontrado"));
            envio.setLugarEntrega(lugarEntregaCompleto);
        }
        
        // Cargar tarifa completa
        if (envio.getTarifa() != null && envio.getTarifa().getId() != null) {
            Tarifa tarifaCompleta = tarifaRepo.findById(envio.getTarifa().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));
            envio.setTarifa(tarifaCompleta);
        }
        
        // Cargar vehículo completo (si existe)
        if (envio.getVehiculo() != null && envio.getVehiculo().getVehiculoId() != null) {
            Vehiculo vehiculoCompleto = vehiculoRepo.findById(envio.getVehiculo().getVehiculoId())
                .orElse(null);
            envio.setVehiculo(vehiculoCompleto);
        } else {
            envio.setVehiculo(null);
        }
        
        // Cargar ruta completa (si existe)
        if (envio.getRuta() != null && envio.getRuta().getRutaId() != null) {
            Ruta rutaCompleta = rutaRepo.findById(envio.getRuta().getRutaId())
                .orElse(null);
            envio.setRuta(rutaCompleta);
        } else {
            envio.setRuta(null);
        }
        
        // Calcular costo del envío
        if (envio.getTarifa() != null && envio.getTarifa().getPrecioKg() != null && envio.getPeso() != null) {
            BigDecimal precioKg = envio.getTarifa().getPrecioKg();
            BigDecimal costo = precioKg.multiply(envio.getPeso());
            
            if (envio.getTipoServicio() == TipoServicio.EXPRESS) {
                costo = costo.multiply(BigDecimal.valueOf(1.5));
            }
            
            envio.setCostoEnvio(costo);
        } else {
            envio.setCostoEnvio(BigDecimal.ZERO);
        }
        
        envioRepo.save(envio);
        return "redirect:/envios";
    }

    // Editar envío
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Envio envio = envioRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Envío no encontrado: " + id));
        model.addAttribute("envio", envio);
        cargarDatosFormulario(model);
        return "form-envio";
    }

    // Ver detalle del envío
    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Envio envio = envioRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Envío no encontrado: " + id));
        model.addAttribute("envio", envio);
        return "detalle-envio";
    }

    // Eliminar envío
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        envioRepo.deleteById(id);
        return "redirect:/envios";
    }

    // Método auxiliar para cargar datos del formulario
    private void cargarDatosFormulario(Model model) {
        model.addAttribute("clientes", usuarioRepo.findByRol("ROLE_CLIENTE"));
        model.addAttribute("mensajeros", usuarioRepo.findByRol("ROLE_MENSAJERO"));
        model.addAttribute("vehiculos", vehiculoRepo.findAll());
        model.addAttribute("tarifas", tarifaRepo.findAll());
        model.addAttribute("rutas", rutaRepo.findAll());
        model.addAttribute("lugares", lugarRepo.findAll());
        model.addAttribute("tiposServicio", TipoServicio.values());
        model.addAttribute("estados", EstadoEnvio.values());
    }

    // ============= REPORTES =============

    /**
     * Muestra la vista de reporte de envíos con filtros
     */
    @GetMapping("/reporte")
    public String vistaReporte(
            @RequestParam(required = false) String numeroGuia,
            @RequestParam(required = false) EstadoEnvio estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {
        
        List<Envio> envios = filtrarEnvios(numeroGuia, estado, desde, hasta);
        
        model.addAttribute("envios", envios);
        model.addAttribute("numeroGuia", numeroGuia);
        model.addAttribute("estado", estado);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("estados", EstadoEnvio.values());
        
        return "vista-reporte-envios";
    }

/**
 * Genera el PDF de envíos
 */
@GetMapping("/reporte/pdf")
public void generarReportePdf(
        @RequestParam(required = false) String numeroGuia,
        @RequestParam(required = false) EstadoEnvio estado,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
        HttpServletResponse response) throws Exception {
    
    List<Envio> envios = filtrarEnvios(numeroGuia, estado, desde, hasta);
    
    // Convertir envíos a un formato amigable para FreeMarker
    List<Map<String, Object>> enviosFormateados = envios.stream()
        .map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("numeroGuia", e.getNumeroGuia());
            map.put("clienteNombre", e.getCliente() != null ? e.getCliente().getNombre() + " " + e.getCliente().getApellido() : "N/A");
            map.put("origenCiudad", e.getLugarRecogida() != null ? e.getLugarRecogida().getCiudad() : "N/A");
            map.put("destinoCiudad", e.getLugarEntrega() != null ? e.getLugarEntrega().getCiudad() : "N/A");
            map.put("peso", e.getPeso());
            map.put("costoEnvio", e.getCostoEnvio());
            map.put("estadoNombre", e.getEstado().name());
            map.put("estadoDisplay", e.getEstado().getDisplayName());
            map.put("fechaCreacion", e.getFechaCreacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return map;
        })
        .collect(Collectors.toList());
    
    Map<String, Object> model = new HashMap<>();
    model.put("envios", enviosFormateados);
    model.put("fecha", LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    model.put("desde", desde != null ? desde.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
    model.put("hasta", hasta != null ? hasta.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
    model.put("total", envios.size());
    
    // Calcular estadísticas
    BigDecimal totalCostos = envios.stream()
        .map(Envio::getCostoEnvio)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    model.put("totalCostos", totalCostos);
    
    pdfGenerator.generarPdf("reporte-envios", model, "reporte-envios", response);
}

    /**
     * Método auxiliar para filtrar envíos
     */
    private List<Envio> filtrarEnvios(String numeroGuia, EstadoEnvio estado, LocalDate desde, LocalDate hasta) {
        List<Envio> todos = envioRepo.findAll();
        
        return todos.stream()
                .filter(e -> numeroGuia == null || 
                        e.getNumeroGuia().toLowerCase().contains(numeroGuia.toLowerCase()))
                .filter(e -> estado == null || e.getEstado().equals(estado))
                .filter(e -> {
                    if (desde != null && hasta != null) {
                        return !e.getFechaCreacion().toLocalDate().isBefore(desde) &&
                               !e.getFechaCreacion().toLocalDate().isAfter(hasta);
                    } else if (desde != null) {
                        return !e.getFechaCreacion().toLocalDate().isBefore(desde);
                    } else if (hasta != null) {
                        return !e.getFechaCreacion().toLocalDate().isAfter(hasta);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
package com.example.enviexpress.controller;

import com.example.enviexpress.model.Usuario;
import com.example.enviexpress.repository.UsuarioRepository;
import com.example.enviexpress.util.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PdfGenerator pdfGenerator;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro/guardar")
    public String guardarRegistro(@ModelAttribute Usuario usuario) {
        usuario.setRol("ROLE_CLIENTE");
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        repo.save(usuario);
        return "redirect:/login?registro=exitoso";
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", repo.findAll());
        return "usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form";
    }

    @PostMapping("/usuarios/guardar")
    public String guardar(@ModelAttribute Usuario usuario) {
        if (usuario.getNombre() == null) usuario.setNombre("");
        if (usuario.getApellido() == null) usuario.setApellido("");
        if (usuario.getCorreo() == null) usuario.setCorreo("");
        if (usuario.getRol() == null) usuario.setRol("USER");
        if (usuario.getActivo() == null) usuario.setActivo(true);
        if (usuario.getFechaCreacion() == null) usuario.setFechaCreacion(LocalDateTime.now());

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        repo.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        model.addAttribute("usuario", usuario);
        return "form";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/usuarios";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication auth) {
        String username = auth.getName();
        Usuario usuario = repo.findByUserName(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/perfil/guardar")
    public String guardarPerfil(@ModelAttribute Usuario usuario, Authentication auth) {
        String username = auth.getName();
        Usuario actual = repo.findByUserName(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        actual.setUserName(usuario.getUserName());
        actual.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getNombre() != null) actual.setNombre(usuario.getNombre());
        if (usuario.getApellido() != null) actual.setApellido(usuario.getApellido());
        if (usuario.getCorreo() != null) actual.setCorreo(usuario.getCorreo());

        repo.save(actual);
        return "redirect:/home_cliente?actualizado";
    }

    // ============= REPORTES =============

    /**
     * Muestra la vista de reporte de usuarios con filtros
     */
    @GetMapping("/usuarios/reporte")
    public String vistaReporte(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean activo,
            Model model) {
        
        List<Usuario> usuarios = filtrarUsuarios(nombre, rol, activo);
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("nombre", nombre);
        model.addAttribute("rol", rol);
        model.addAttribute("activo", activo);
        
        return "vista-reporte-usuarios";
    }

    /**
     * Genera el PDF de usuarios
     */
    @GetMapping("/usuarios/reporte/pdf")
    public void generarReportePdf(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean activo,
            HttpServletResponse response) throws Exception {
        
        List<Usuario> usuarios = filtrarUsuarios(nombre, rol, activo);
        
        Map<String, Object> model = new HashMap<>();
        model.put("usuarios", usuarios);
        model.put("fecha", LocalDate.now());
        model.put("total", usuarios.size());
        
        pdfGenerator.generarPdf("reporte-usuarios", model, "reporte-usuarios", response);
    }

    /**
     * Método auxiliar para filtrar usuarios
     */
    private List<Usuario> filtrarUsuarios(String nombre, String rol, Boolean activo) {
        List<Usuario> todos = repo.findAll();
        
        return todos.stream()
                .filter(u -> nombre == null || 
                        u.getNombre().toLowerCase().contains(nombre.toLowerCase()) ||
                        u.getApellido().toLowerCase().contains(nombre.toLowerCase()) ||
                        u.getUserName().toLowerCase().contains(nombre.toLowerCase()))
                .filter(u -> rol == null || u.getRol().equals(rol))
                .filter(u -> activo == null || u.getActivo().equals(activo))
                .collect(Collectors.toList());
    }
}
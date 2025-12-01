package com.example.enviexpress.controller;

import com.example.enviexpress.model.Usuario;
import com.example.enviexpress.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;  // ← ESTE IMPORT
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ← ESTE TIPO

    @GetMapping("/")
    public String redireccionRaiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication auth) {
        model.addAttribute("rol", auth.getAuthorities().toString());
        return "home";
    }

    @GetMapping("/home_cliente")
    public String home_cliente(Model model, Authentication auth) {
        model.addAttribute("rol", auth.getAuthorities().toString());
        return "home_cliente";
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
        return "form";
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
        return "redirect:/home?actualizado";
    }
}
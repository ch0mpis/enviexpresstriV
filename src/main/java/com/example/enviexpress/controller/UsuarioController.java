package com.example.enviexpress.controller;

import com.example.enviexpress.model.Usuario;
import com.example.enviexpress.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

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
        usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
        repo.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", repo.findById(id).orElseThrow());
        return "form";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/usuarios";
    }

    // metodos para que el usuario normal "USER" pueda editar su perfil
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication auth) {
        String username = auth.getName(); // nombre de usuario autenticado
        Usuario usuario = repo.findByUserName(username).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "form";
    }

    @PostMapping("/perfil/guardar")
    public String guardarPerfil(@ModelAttribute Usuario usuario, Authentication auth) {
        String username = auth.getName();
        Usuario actual = repo.findByUserName(username).orElseThrow();

        actual.setUserName(usuario.getUserName());
        actual.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
        repo.save(actual); 
        return "redirect:/home?actualizado";
        
    }

}

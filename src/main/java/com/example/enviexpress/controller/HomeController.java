package com.example.enviexpress.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model, Authentication auth) {
        // Verificar el rol y redirigir a la página correspondiente
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("rol", auth.getAuthorities().toString());
            return "home"; // home.html para admin
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            model.addAttribute("rol", auth.getAuthorities().toString());
            return "home_cliente"; // home_cliente.html para cliente
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MENSAJERO"))) {
            model.addAttribute("rol", auth.getAuthorities().toString());
            return "home_mensajero"; // Si tienes una página para mensajero
        }
        
        // Por defecto, redirigir a home_cliente
        model.addAttribute("rol", auth.getAuthorities().toString());
        return "home_cliente";
    }

    @GetMapping("/home_cliente")
    public String homeCliente(Model model, Authentication auth) {
        model.addAttribute("rol", auth.getAuthorities().toString());
        return "home_cliente";
    }
}
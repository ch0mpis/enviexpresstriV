package com.example.enviexpress.controller;

import com.example.enviexpress.model.Envio;
import com.example.enviexpress.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class SeguimientoController {

    @Autowired
    private EnvioRepository envioRepo;

    @GetMapping("/seguimiento")
    public String seguimiento() {
        return "seguimiento";
    }

    @PostMapping("/seguimiento/buscar")
    public String buscar(@RequestParam String numeroGuia, Model model) {
        Optional<Envio> envioOpt = envioRepo.findByNumeroGuia(numeroGuia.trim().toUpperCase());
        
        if (envioOpt.isPresent()) {
            model.addAttribute("envio", envioOpt.get());
            model.addAttribute("encontrado", true);
        } else {
            model.addAttribute("encontrado", false);
            model.addAttribute("mensaje", "No se encontró ningún envío con el número de guía: " + numeroGuia);
        }
        
        return "seguimiento";
    }
}
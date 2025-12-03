package com.example.enviexpress.controller;

import com.example.enviexpress.model.Tarifa;
import com.example.enviexpress.repository.TarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/tarifas")
public class TarifaController {

    @Autowired
    private TarifaRepository repo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tarifas", repo.findAll());
        return "tarifas";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("tarifa", new Tarifa());
        return "tarifas-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Tarifa tarifa) {
        if (tarifa.getTipo() == null) tarifa.setTipo("");
        if (tarifa.getPrecioKg() == null) tarifa.setPrecioKg(BigDecimal.ZERO);
        
        repo.save(tarifa);
        return "redirect:/tarifas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Tarifa tarifa = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada: " + id));
        model.addAttribute("tarifa", tarifa);
        return "tarifas-form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/tarifas";
    }
}
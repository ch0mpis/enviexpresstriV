package com.example.enviexpress.controller;

import com.example.enviexpress.model.Vehiculo;
import com.example.enviexpress.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoRepository repo;

    // Listar todos los vehículos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vehiculos", repo.findAll());
        return "vehiculos";
    }

    // Formulario para nuevo vehículo
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        return "form-vehiculo";
    }

    // Guardar vehículo
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Vehiculo vehiculo) {
        repo.save(vehiculo);
        return "redirect:/vehiculos";
    }

    // Editar vehículo
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Vehiculo vehiculo = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado: " + id));
        model.addAttribute("vehiculo", vehiculo);
        return "form-vehiculo";
    }

    // Eliminar vehículo
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/vehiculos";
    }
}
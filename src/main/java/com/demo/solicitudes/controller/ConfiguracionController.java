package com.demo.solicitudes.controller;

import com.demo.solicitudes.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConfiguracionController {

    @GetMapping("/configuracion")
    public String configuracion(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuario", usuario);
        return "configuracion";
    }
}
package com.demo.solicitudes.controller;

import com.demo.solicitudes.model.Usuario;
import com.demo.solicitudes.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Usuario usuario = usuarioRepository.findByUsernameAndPasswordAndActivo(username, password, 1);

        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/";
        }

        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "redirect:/login?logout";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/guardar-usuario")
    public String guardarUsuario(@RequestParam String nombre,
                                 @RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam(required = false) String autorizaDatos,
                                 Model model) {

        if (autorizaDatos == null) {
            model.addAttribute("error", "Debe aceptar la política de tratamiento de datos personales.");
            return "registro";
        }

        if (usuarioRepository.existsByUsername(username)) {
            model.addAttribute("error", "El usuario ya existe.");
            return "registro";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setRol("USER");
        usuario.setActivo(1);

        usuarioRepository.save(usuario);

        return "redirect:/login?registroExitoso=" + username;
    }

    @GetMapping("/politica-datos")
    public String politicaDatos() {
        return "politica-datos";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuario", usuario);
        return "perfil";
    }



}
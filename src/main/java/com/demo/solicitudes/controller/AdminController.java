package com.demo.solicitudes.controller;

import com.demo.solicitudes.model.Solicitud;
import com.demo.solicitudes.model.Usuario;
import com.demo.solicitudes.service.SolicitudService;
import com.demo.solicitudes.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SolicitudService solicitudService;
    private final UsuarioRepository usuarioRepository;

    public AdminController(SolicitudService solicitudService, UsuarioRepository usuarioRepository) {
        this.solicitudService = solicitudService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String panel(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/";
        }

        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("solicitudes", solicitudService.listarTodas());

        return "admin";
    }

    @PostMapping("/solicitud/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam String estado,
                                HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/";
        }

        Solicitud solicitud = solicitudService.buscarPorId(id);

        if (solicitud != null) {
            solicitud.setEstado(estado);
            solicitudService.guardar(solicitud);
        }

        return "redirect:/admin";
    }
    @GetMapping("/exportar/solicitudes")
    public void exportarTodas(HttpSession session,
                              HttpServletResponse response) throws IOException {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            response.sendRedirect("/");
            return;
        }

        List<Solicitud> solicitudes = solicitudService.listarTodas();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solicitudes");

        Row header = sheet.createRow(0);
        String[] columnas = {"ID", "Solicitante", "Correo", "Tipo", "Prioridad", "Estado", "Fecha"};

        for (int i = 0; i < columnas.length; i++) {
            header.createCell(i).setCellValue(columnas[i]);
        }

        int fila = 1;

        for (Solicitud s : solicitudes) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(s.getId());
            row.createCell(1).setCellValue(s.getNombre());
            row.createCell(2).setCellValue(s.getCorreo());
            row.createCell(3).setCellValue(s.getTipo());
            row.createCell(4).setCellValue(s.getPrioridad());
            row.createCell(5).setCellValue(s.getEstado());
            row.createCell(6).setCellValue(String.valueOf(s.getFechaCreacion()));
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes_admin.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
package com.demo.solicitudes.controller;

import com.demo.solicitudes.model.Solicitud;
import com.demo.solicitudes.model.Usuario;
import com.demo.solicitudes.service.SolicitudService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;

import java.util.List;

@Controller
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        List<Solicitud> solicitudes = solicitudService.listarPorUsuario(usuario.getId());

        model.addAttribute("solicitudes", solicitudes);

        return "index";
    }

    @GetMapping("/nueva")
    public String nuevaSolicitud(Model model) {
        model.addAttribute("solicitud", new Solicitud());
        return "formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Solicitud solicitud,
                          BindingResult result,
                          HttpSession session,
                          Model model) {

        if (result.hasErrors()) {
            model.addAttribute("solicitud", solicitud);
            return "formulario";
        }

        if (solicitud.getVerificaDatos() == null || solicitud.getVerificaDatos() != 1) {
            model.addAttribute("error", "Debe confirmar la verificación de datos antes de guardar.");
            model.addAttribute("solicitud", solicitud);
            return "formulario";
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        solicitud.setUsuario(usuario);
        solicitud.setEstado("Pendiente");

        solicitudService.guardar(solicitud);

        return "redirect:/";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         HttpSession session,
                         Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        Solicitud solicitud = solicitudService.buscarPorId(id);

        if (solicitud == null || !solicitud.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/";
        }

        model.addAttribute("solicitud", solicitud);

        return "editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Solicitud solicitud,
                             HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        Solicitud solicitudActual = solicitudService.buscarPorId(solicitud.getId());

        if (solicitudActual == null || !solicitudActual.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/";
        }

        solicitud.setUsuario(usuario);

        solicitudService.guardar(solicitud);

        return "redirect:/";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        Solicitud solicitud = solicitudService.buscarPorId(id);

        if (solicitud == null || !solicitud.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/";
        }

        solicitudService.eliminar(id);

        return "redirect:/";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam String criterio,
                         @RequestParam String valor,
                         HttpSession session,
                         Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        List<Solicitud> resultados;

        if ("estado".equalsIgnoreCase(criterio)) {
            resultados = solicitudService.buscarPorEstadoYUsuario(valor, usuario.getId());
        } else {
            resultados = solicitudService.buscarPorNombreYUsuario(valor, usuario.getId());
        }

        model.addAttribute("solicitudes", resultados);

        return "index";
    }

    @GetMapping("/certificado/{id}")
    public String certificado(@PathVariable Long id,
                              HttpSession session,
                              Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        Solicitud solicitud = solicitudService.buscarPorId(id);

        if (solicitud == null || !solicitud.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/";
        }

        if (!"Certificado".equals(solicitud.getTipo()) || !"Aprobada".equals(solicitud.getEstado())) {
            return "redirect:/";
        }

        model.addAttribute("solicitud", solicitud);

        return "certificado";

    }
    @GetMapping("/exportar/solicitudes")
    public void exportarMisSolicitudes(HttpSession session,
                                       HttpServletResponse response) throws IOException {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        List<Solicitud> solicitudes = solicitudService.listarPorUsuario(usuario.getId());

        generarExcel(response, solicitudes, "mis_solicitudes.xlsx");
    }

    private void generarExcel(HttpServletResponse response,
                              List<Solicitud> solicitudes,
                              String nombreArchivo) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solicitudes");

        Row header = sheet.createRow(0);
        String[] columnas = {"ID", "Nombre", "Correo", "Tipo", "Prioridad", "Estado", "Fecha"};

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
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
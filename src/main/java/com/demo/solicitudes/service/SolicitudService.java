package com.demo.solicitudes.service;

import com.demo.solicitudes.model.Solicitud;
import com.demo.solicitudes.repository.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;

    public SolicitudService(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    public List<Solicitud> listarTodas() {
        return solicitudRepository.findAll();
    }

    public Solicitud guardar(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    public Solicitud buscarPorId(Long id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        solicitudRepository.deleteById(id);
    }

    public List<Solicitud> buscarPorNombre(String nombre) {
        return solicitudRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Solicitud> buscarPorEstado(String estado) {
        return solicitudRepository.findByEstadoIgnoreCase(estado);
    }
    public List<Solicitud> listarPorUsuario(Long usuarioId) {
        return solicitudRepository.findByUsuarioId(usuarioId);
    }

    public List<Solicitud> buscarPorEstadoYUsuario(String estado, Long usuarioId) {
        return solicitudRepository.findByEstadoContainingIgnoreCaseAndUsuarioId(estado, usuarioId);
    }

    public List<Solicitud> buscarPorNombreYUsuario(String nombre, Long usuarioId) {
        return solicitudRepository.findByNombreContainingIgnoreCaseAndUsuarioId(nombre, usuarioId);
    }
}
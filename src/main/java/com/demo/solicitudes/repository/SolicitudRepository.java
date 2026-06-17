package com.demo.solicitudes.repository;

import com.demo.solicitudes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByUsuarioId(Long usuarioId);

    List<Solicitud> findByNombreContainingIgnoreCase(String nombre);

    List<Solicitud> findByEstadoIgnoreCase(String estado);

    List<Solicitud> findByEstadoContainingIgnoreCaseAndUsuarioId(String estado, Long usuarioId);

    List<Solicitud> findByNombreContainingIgnoreCaseAndUsuarioId(String nombre, Long usuarioId);
}
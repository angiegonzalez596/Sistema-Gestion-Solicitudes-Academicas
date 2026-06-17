package com.demo.solicitudes.repository;

import com.demo.solicitudes.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsernameAndPasswordAndActivo(String username, String password, Integer activo);

    boolean existsByUsername(String username);
}
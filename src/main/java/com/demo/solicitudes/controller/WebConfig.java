package com.demo.solicitudes.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Aplica cabeceras de seguridad a TODAS las páginas
        registry.addInterceptor(new SecurityHeadersInterceptor())
                .addPathPatterns("/**");

        // Valida sesión solo en páginas privadas
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/registro",
                        "/guardar-usuario",
                        "/politica-datos",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error"
                );
    }
}
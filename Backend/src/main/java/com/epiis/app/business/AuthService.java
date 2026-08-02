package com.epiis.app.business;

// ✅ ARCHIVO NUEVO - Crear aquí: src/main/java/com/epiis/app/business/AuthService.java

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dataaccess.EmpleadoRepository;
import com.epiis.app.dto.LoginRequest;
import com.epiis.app.dto.LoginResponse;
import com.epiis.app.entity.Empleado;
import com.epiis.app.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Autentica un empleado y genera un token JWT
     */
    public LoginResponse login(LoginRequest request) {
        // Validar que nombre y password no sean nulos
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            return crearRespuestaError("El nombre de usuario es requerido");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return crearRespuestaError("La contraseña es requerida");
        }

        // Buscar empleado por nombre
        Empleado empleado = empleadoRepository.findByNombre(request.getNombre().trim());

        // Validar si existe
        if (empleado == null) {
            return crearRespuestaError("Usuario no encontrado");
        }

        // Validar si está activo
        if (!empleado.getEstado()) {
            return crearRespuestaError("Usuario inactivo");
        }

        // Validar contraseña (en producción, usar BCryptPasswordEncoder)
        if (!empleado.getPassword().equals(request.getPassword())) {
            return crearRespuestaError("Contraseña incorrecta");
        }

        // Generar token JWT
        String token = jwtUtil.generateToken(
                empleado.getIdEmpleado(),
                empleado.getNombre(),
                empleado.getRol()
        );

        // Crear respuesta exitosa
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setIdEmpleado(empleado.getIdEmpleado());
        response.setNombre(empleado.getNombre());
        response.setRol(empleado.getRol());
        response.setEstado(empleado.getEstado());
        response.setMensaje("Login exitoso");

        return response;
    }

    /**
     * Valida un token y retorna los datos del empleado
     */
    public LoginResponse validateToken(String token) {
        // Validar que el token no sea nulo
        if (token == null || token.trim().isEmpty()) {
            return crearRespuestaError("Token no proporcionado");
        }

        // Verificar si el token es válido
        if (!jwtUtil.isTokenValid(token)) {
            return crearRespuestaError("Token inválido");
        }

        // Verificar si el token ha expirado
        if (jwtUtil.isTokenExpired(token)) {
            return crearRespuestaError("Token expirado");
        }

        // Extraer idEmpleado del token
        String idEmpleado = jwtUtil.getIdEmpleadoFromToken(token);

        // Buscar empleado en BD
        Empleado empleado = empleadoRepository.findById(idEmpleado).orElse(null);

        if (empleado == null) {
            return crearRespuestaError("Empleado no encontrado");
        }

        // Crear respuesta exitosa
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setIdEmpleado(empleado.getIdEmpleado());
        response.setNombre(empleado.getNombre());
        response.setRol(empleado.getRol());
        response.setEstado(empleado.getEstado());
        response.setMensaje("Token válido");

        return response;
    }

    /**
     * Helper para crear respuestas de error
     */
    private LoginResponse crearRespuestaError(String mensaje) {
        LoginResponse response = new LoginResponse();
        response.setToken(null);
        response.setMensaje(mensaje);
        return response;
    }
}
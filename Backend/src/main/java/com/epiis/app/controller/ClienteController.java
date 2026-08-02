package com.epiis.app.controller;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import com.epiis.app.service.ClienteService;
@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", maxAge = 3600)
public class ClienteController {
    @Autowired
    private ClienteService clienteService;
    /**
     * ✅ Verifica DNI en RENIEC
     * POST /api/cliente/verificar-dni
     */
    @PostMapping("/verificar-dni")
    public ResponseEntity<Map<String, Object>> registroDni(@RequestBody Map<String, String> request) {
        try {
            String dni = request.get("dni");
            System.out.println("📋 [/verificar-dni] DNI: " + dni);
            
            if (dni == null || dni.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "DNI es requerido"));
            }
            
            Map<String, Object> response = clienteService.verificarDni(dni);
            
            if ((boolean) response.get("success")) {
                System.out.println("✅ [/verificar-dni] Éxito");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ [/verificar-dni] " + response.get("error"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/verificar-dni] Exception: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    /**
     * ✅ Envía código OTP por EMAIL
     * POST /api/cliente/enviar-codigo
     */
    @PostMapping("/enviar-codigo")
    public ResponseEntity<Map<String, Object>> enviarCodigoEmail(@RequestBody Map<String, String> request) {
        System.out.println("🚀 [/enviar-codigo] INICIO - Request Body: " + request);
        
        try {
            String dni = request.get("dni");
            String email = request.get("email");
            
            System.out.println("📧 [/enviar-codigo] DNI: " + dni + ", Email: " + email);
            
            if (dni == null || email == null) {
                System.out.println("❌ [/enviar-codigo] Campos nulos");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "DNI y email son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("⏳ [/enviar-codigo] Llamando a clienteService...");
            Map<String, Object> response = clienteService.enviarCodigoVerificacion(dni, email);
            System.out.println("📊 [/enviar-codigo] Respuesta: " + response);
            
            if ((boolean) response.get("success")) {
                System.out.println("✅ [/enviar-codigo] ÉXITO - Enviando respuesta 200");
                return ResponseEntity.ok().body(response);
            } else {
                System.out.println("❌ [/enviar-codigo] Error: " + response.get("error"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/enviar-codigo] EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    /**
     * ✅ Confirma OTP y crea cuenta
     * POST /api/cliente/crear-cuenta
     */
    @PostMapping("/crear-cuenta")
    public ResponseEntity<Map<String, Object>> confirmarOtp(@RequestBody Map<String, String> request) {
        System.out.println("🔐 [/crear-cuenta] ===== INICIO =====");
        System.out.println("🔐 [/crear-cuenta] Request Body: " + request);
        
        try {
            String dni = request.get("dni");
            String nombres = request.get("nombres");
            String apellido_paterno = request.get("apellido_paterno");
            String apellido_materno = request.get("apellido_materno");
            String email = request.get("email");
            String password = request.get("password");
            String codigo = request.get("codigo");
            
            System.out.println("📝 [/crear-cuenta] DNI: " + dni);
            System.out.println("📝 [/crear-cuenta] Nombres: " + nombres);
            System.out.println("📝 [/crear-cuenta] Email: " + email);
            System.out.println("📝 [/crear-cuenta] Código: " + codigo);
            
            if (dni == null || nombres == null || apellido_paterno == null || 
                email == null || password == null || codigo == null) {
                System.err.println("❌ [/crear-cuenta] Faltan campos");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Todos los campos son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("⏳ [/crear-cuenta] Llamando a clienteService.confirmarOtpYCrearCuenta()...");
            Map<String, Object> response = clienteService.confirmarOtpYCrearCuenta(
                    dni, nombres, apellido_paterno, apellido_materno, 
                    email, password, codigo
            );
            System.out.println("📊 [/crear-cuenta] Respuesta recibida: " + response);
            
            if ((boolean) response.get("success")) {
                System.out.println("✅ [/crear-cuenta] ÉXITO - Enviando respuesta 200");
                return ResponseEntity.ok().body(response);
            } else {
                System.out.println("❌ [/crear-cuenta] Error: " + response.get("error"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/crear-cuenta] EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } finally {
            System.out.println("🔐 [/crear-cuenta] ===== FIN =====\n");
        }
    }
    /**
     * ✅ Login cliente con DNI + Contraseña
     * POST /api/cliente/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginCliente(@RequestBody Map<String, String> request) {
        try {
            String dni = request.get("dni");
            String password = request.get("password");
            System.out.println("👤 [/login] DNI: " + dni);
            
            if (dni == null || password == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "DNI y contraseña son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> response = clienteService.loginCliente(dni, password);
            if ((boolean) response.get("success")) {
                System.out.println("✅ [/login] Éxito");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ [/login] " + response.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/login] Exception: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
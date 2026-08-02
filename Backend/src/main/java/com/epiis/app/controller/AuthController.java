package com.epiis.app.controller;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.epiis.app.business.AuthService;
import com.epiis.app.dto.LoginRequest;
import com.epiis.app.dto.LoginResponse;
import com.epiis.app.service.ClienteService;
import com.epiis.app.service.EmailService;
import com.epiis.app.service.OtpService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private OtpService otpService;

    // ==================== EMPLEADOS ====================
    
    /**
     * ✅ LOGIN EMPLEADOS
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            if (response.getToken() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", response.getMensaje(),
                                "success", false
                        ));
            }
            return ResponseEntity.ok(Map.of(
                    "token", response.getToken(),
                    "idEmpleado", response.getIdEmpleado(),
                    "nombre", response.getNombre(),
                    "rol", response.getRol(),
                    "estado", response.getEstado(),
                    "mensaje", response.getMensaje(),
                    "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error en el servidor: " + e.getMessage(),
                            "success", false
                    ));
        }
    }
    
    /**
     * ✅ VALIDAR TOKEN EMPLEADOS
     * POST /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "Header Authorization no proporcionado",
                                "success", false
                        ));
            }
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "Formato de Authorization inválido. Debe ser: Bearer {token}",
                                "success", false
                        ));
            }
            String token = authHeader.substring(7);
            LoginResponse response = authService.validateToken(token);
            if (response.getToken() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", response.getMensaje(),
                                "success", false
                        ));
            }
            return ResponseEntity.ok(Map.of(
                    "token", response.getToken(),
                    "idEmpleado", response.getIdEmpleado(),
                    "nombre", response.getNombre(),
                    "rol", response.getRol(),
                    "estado", response.getEstado(),
                    "mensaje", response.getMensaje(),
                    "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error al validar token: " + e.getMessage(),
                            "success", false
                    ));
        }
    }

    // ==================== CLIENTES ====================
    
    /**
     * ✅ Verifica DNI en RENIEC
     * POST /api/auth/registro-dni
     */
    @PostMapping("/registro-dni")
    public ResponseEntity<Map<String, Object>> registroDni(@RequestBody Map<String, String> request) {
        try {
            String dni = request.get("dni");
            System.out.println("📋 [/registro-dni] DNI: " + dni);
            
            if (dni == null || dni.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "DNI es requerido"));
            }
            
            Map<String, Object> response = clienteService.verificarDni(dni);
            
            if ((boolean) response.get("success")) {
                System.out.println("✅ [/registro-dni] Éxito");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ [/registro-dni] " + response.get("error"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/registro-dni] Exception: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * ✅ Envía código OTP por EMAIL
     * POST /api/auth/enviar-codigo-email
     */
    @PostMapping("/enviar-codigo-email")
    public ResponseEntity<Map<String, Object>> enviarCodigoEmail(@RequestBody Map<String, String> request) {
        System.out.println("🚀 [/enviar-codigo-email] INICIO - Request Body: " + request);
        
        try {
            String dni = request.get("dni");
            String email = request.get("email");
            
            System.out.println("📧 [/enviar-codigo-email] DNI: " + dni + ", Email: " + email);
            
            if (dni == null || email == null) {
                System.out.println("❌ [/enviar-codigo-email] Campos nulos");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "DNI y email son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("⏳ [/enviar-codigo-email] Llamando a clienteService...");
            Map<String, Object> response = clienteService.enviarCodigoVerificacion(dni, email);
            System.out.println("📊 [/enviar-codigo-email] Respuesta: " + response);
            
            if ((boolean) response.get("success")) {
                System.out.println("✅ [/enviar-codigo-email] ÉXITO - Enviando respuesta 200");
                return ResponseEntity.ok().body(response);
            } else {
                System.out.println("❌ [/enviar-codigo-email] Error: " + response.get("error"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/enviar-codigo-email] EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * ✅ Confirma OTP y crea cuenta
     * POST /api/auth/confirmar-otp
     */
    @PostMapping("/confirmar-otp")
    public ResponseEntity<Map<String, Object>> confirmarOtp(@RequestBody Map<String, String> request) {
        System.out.println("🔐 [/confirmar-otp] ===== INICIO =====");
        System.out.println("🔐 [/confirmar-otp] Request Body: " + request);
        
        try {
            String dni = request.get("dni");
            String nombres = request.get("nombres");
            String apellido_paterno = request.get("apellido_paterno");
            String apellido_materno = request.get("apellido_materno");
            String email = request.get("email");
            String password = request.get("password");
            String codigo = request.get("codigo");
            
            System.out.println("📝 [/confirmar-otp] DNI: " + dni);
            System.out.println("📝 [/confirmar-otp] Nombres: " + nombres);
            System.out.println("📝 [/confirmar-otp] Email: " + email);
            System.out.println("📝 [/confirmar-otp] Código: " + codigo);
            
            if (dni == null || nombres == null || apellido_paterno == null || 
                email == null || password == null || codigo == null) {
                System.err.println("❌ [/confirmar-otp] Faltan campos");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Todos los campos son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("⏳ [/confirmar-otp] Llamando a clienteService.confirmarOtpYCrearCuenta()...");
            Map<String, Object> response = clienteService.confirmarOtpYCrearCuenta(
                    dni, nombres, apellido_paterno, apellido_materno, 
                    email, password, codigo
            );
            System.out.println("📊 [/confirmar-otp] Respuesta recibida: " + response);
            
            if ((boolean) response.get("success")) {
                System.out.println("✅ [/confirmar-otp] ÉXITO - Enviando respuesta 200");
                return ResponseEntity.ok().body(response);
            } else {
                System.out.println("❌ [/confirmar-otp] Error: " + response.get("error"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/confirmar-otp] EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } finally {
            System.out.println("🔐 [/confirmar-otp] ===== FIN =====\n");
        }
    }
    
    /**
     * ✅ Login cliente con DNI + Contraseña
     * POST /api/auth/login-cliente
     */
    @PostMapping("/login-cliente")
    public ResponseEntity<Map<String, Object>> loginCliente(@RequestBody Map<String, String> request) {
        try {
            String dni = request.get("dni");
            String password = request.get("password");
            System.out.println("👤 [/login-cliente] DNI: " + dni);
            
            if (dni == null || password == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "DNI y contraseña son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> response = clienteService.loginCliente(dni, password);
            if ((boolean) response.get("success")) {
                System.out.println("✅ [/login-cliente] Éxito");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ [/login-cliente] " + response.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/login-cliente] Exception: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * ✅ Solicita recuperación de contraseña
     * POST /api/auth/solicitud-recuperacion-password
     */
    @PostMapping("/solicitud-recuperacion-password")
    public ResponseEntity<Map<String, Object>> solicitudRecuperacionPassword(@RequestBody Map<String, String> request) {
        System.out.println("🔐 [/solicitud-recuperacion-password] SOLICITUD");
        
        try {
            String dni = request.get("dni");
            String email = request.get("email");
            
            if (dni == null || email == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "DNI y email son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> response = clienteService.solicitudRecuperacionPassword(dni, email);
            if ((boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/solicitud-recuperacion-password] Exception: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * ✅ Confirma OTP y cambia contraseña
     * POST /api/auth/confirmar-recuperacion-password
     */
    @PostMapping("/confirmar-recuperacion-password")
    public ResponseEntity<Map<String, Object>> confirmarRecuperacionPassword(@RequestBody Map<String, String> request) {
        System.out.println("🔐 [/confirmar-recuperacion-password] SOLICITUD");
        
        try {
            String dni = request.get("dni");
            String email = request.get("email");
            String codigo = request.get("codigo");
            String passwordNueva = request.get("passwordNueva");
            String passwordConfirm = request.get("passwordConfirm");
            
            if (dni == null || email == null || codigo == null || 
                passwordNueva == null || passwordConfirm == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Todos los campos son requeridos");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> response = clienteService.confirmarRecuperacionPassword(
                    dni, email, codigo, passwordNueva, passwordConfirm
            );
            if ((boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [/confirmar-recuperacion-password] Exception: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
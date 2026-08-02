package com.epiis.app.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.epiis.app.dataaccess.ClienteRepository;
import com.epiis.app.entity.Cliente;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ReniecService reniecService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    /**
     * ✅ Verifica DNI en RENIEC y retorna datos del cliente
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> verificarDni(String dni) {
        // Validar formato DNI
        if (dni == null || !dni.matches("^[0-9]{8}$")) {
            return Map.of(
                    "success", false,
                    "error", "DNI inválido. Debe tener 8 dígitos"
            );
        }
        // Verificar si ya existe cliente con este DNI
        Optional<Cliente> clienteExistente = clienteRepository.findByDni(dni);
        if (clienteExistente.isPresent()) {
            return Map.of(
                    "success", false,
                    "error", "El DNI ya está registrado en el sistema"
            );
        }
        // Consultar RENIEC
        Map<String, Object> reniecResponse = reniecService.consultarDni(dni);
        if (!(boolean) reniecResponse.get("success")) {
            return Map.of(
                    "success", false,
                    "error", reniecResponse.get("error")
            );
        }
        Map<String, Object> reniecData = (Map<String, Object>) reniecResponse.get("data");
        Map<String, String> datosCliente = reniecService.extraerDatosReniec(reniecData);
        return Map.of(
                "success", true,
                "datos", datosCliente
        );
    }
    /**
     * ✅ Envía código OTP por email para verificación
     */
    public Map<String, Object> enviarCodigoVerificacion(String dni, String email) {
        // Validar DNI
        if (dni == null || !dni.matches("^[0-9]{8}$")) {
            return Map.of("success", false, "error", "DNI inválido");
        }
        // Validar email
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return Map.of("success", false, "error", "Email inválido");
        }
        // Verificar si email ya existe
        if (clienteRepository.existsByEmail(email)) {
            return Map.of("success", false, "error", "El email ya está registrado");
        }
        // Generar código OTP de 6 dígitos
        String codigoOtp = otpService.generarOtp();
        
        // Guardar OTP en Redis/Caché
        otpService.guardarOtp(email, codigoOtp);
        // Enviar email con código OTP
        boolean emailEnviado = emailService.enviarCodigoOtp(email, codigoOtp);
        if (!emailEnviado) {
            return Map.of("success", false, "error", "Error al enviar email");
        }
        System.out.println("📧 Código OTP generado para email " + email + ": " + codigoOtp);
        return Map.of(
                "success", true,
                "mensaje", "Código enviado al email " + email,
                "codigo", codigoOtp  // SOLO PARA DESARROLLO - REMOVER EN PRODUCCIÓN
        );
    }
    /**
     * ✅ Valida código OTP y crea la cuenta
     */
    public Map<String, Object> confirmarOtpYCrearCuenta(
            String dni,
            String nombres,
            String apellido_paterno,
            String apellido_materno,
            String email,
            String password,
            String codigoOtp) {
        // Validaciones básicas
        if (dni == null || nombres == null || apellido_paterno == null || 
            email == null || password == null || codigoOtp == null) {
            return Map.of("success", false, "error", "Faltan campos requeridos");
        }
        // Validar formato DNI
        if (!dni.matches("^[0-9]{8}$")) {
            return Map.of("success", false, "error", "DNI inválido");
        }
        // Validar que el DNI no exista ya
        if (clienteRepository.existsByDni(dni)) {
            return Map.of("success", false, "error", "El DNI ya está registrado");
        }
        // Validar que el email no exista ya
        if (clienteRepository.existsByEmail(email)) {
            return Map.of("success", false, "error", "El email ya está registrado");
        }
        // ✅ Validar código OTP
        if (!otpService.validarOtp(email, codigoOtp)) {
            return Map.of("success", false, "error", "Código OTP inválido o expirado");
        }
        try {
            // Crear nuevo cliente
            Cliente cliente = new Cliente();
            cliente.setIdCliente(java.util.UUID.randomUUID().toString());
            cliente.setDni(dni);
            cliente.setNombres(nombres.toUpperCase());
            cliente.setApellido_paterno(apellido_paterno.toUpperCase());
            cliente.setApellido_materno(apellido_materno != null ? apellido_materno.toUpperCase() : null);
            cliente.setEmail(email);
            cliente.setPassword(passwordEncoder.encode(password)); // Encriptar
            cliente.setEstado_verificacion(true);
            cliente.setFecha_verificacion(new Timestamp(System.currentTimeMillis()));
            cliente.setEstado(true);
            clienteRepository.save(cliente);
            // Enviar email de bienvenida
            emailService.enviarConfirmacionCuenta(email, nombres);
            System.out.println("✅ Cliente creado exitosamente: " + cliente.getNombres());
            return Map.of(
                    "success", true,
                    "mensaje", "Cuenta creada exitosamente",
                    "idCliente", cliente.getIdCliente(),
                    "nombres", cliente.getNombres()
            );
        } catch (Exception e) {
            System.err.println("❌ Error al crear cuenta: " + e.getMessage());
            return Map.of("success", false, "error", "Error al crear cuenta: " + e.getMessage());
        }
    }
    /**
     * ✅ Login cliente con DNI + Contraseña
     */
    public Map<String, Object> loginCliente(String dni, String password) {
        // Validar DNI
        if (dni == null || !dni.matches("^[0-9]{8}$")) {
            return Map.of("success", false, "error", "DNI inválido");
        }
        // Buscar cliente
        Optional<Cliente> clienteOpt = clienteRepository.findByDniAndEstado(dni, true);
        if (clienteOpt.isEmpty()) {
            return Map.of("success", false, "error", "Cliente no encontrado");
        }
        Cliente cliente = clienteOpt.get();
        // Validar contraseña (con BCrypt)
        if (!passwordEncoder.matches(password, cliente.getPassword())) {
            return Map.of("success", false, "error", "Contraseña incorrecta");
        }
        // Validar verificación email
        if (!cliente.getEstado_verificacion()) {
            return Map.of("success", false, "error", "Cuenta no verificada");
        }
        // Login exitoso
        return Map.of(
                "success", true,
                "mensaje", "Login exitoso",
                "idCliente", cliente.getIdCliente(),
                "nombres", cliente.getNombres(),
                "dni", cliente.getDni()
        );
    }
    /**
     * ✅ Solicita recuperación de contraseña
     * ✨ CORREGIDO: Busca por DNI únicamente, no por DNI+Email
     */
    public Map<String, Object> solicitudRecuperacionPassword(String dni, String email) {
        // Validar DNI
        if (dni == null || !dni.matches("^[0-9]{8}$")) {
            return Map.of("success", false, "error", "DNI inválido");
        }
        
        // Validar email
        if (email == null || email.isEmpty()) {
            return Map.of("success", false, "error", "Email requerido");
        }
        
        // ✅ Buscar cliente SOLO por DNI (el DNI es único)
        Optional<Cliente> clienteOpt = clienteRepository.findByDni(dni);
        if (clienteOpt.isEmpty()) {
            System.out.println("❌ Cliente no encontrado con DNI: " + dni);
            return Map.of("success", false, "error", "DNI no encontrado en el sistema");
        }
        
        Cliente cliente = clienteOpt.get();
        
        // ✅ Verificar que el email proporcionado coincida con el del cliente
        if (!cliente.getEmail().equalsIgnoreCase(email)) {
            System.out.println("❌ Email no coincide para DNI: " + dni);
            System.out.println("   Email esperado: " + cliente.getEmail());
            System.out.println("   Email proporcionado: " + email);
            return Map.of("success", false, "error", "El email no coincide con el registrado");
        }
        
        // Generar código OTP
        String codigoOtp = otpService.generarOtp();
        otpService.guardarOtp(email, codigoOtp);
        
        // Enviar email con código
        boolean emailEnviado = emailService.enviarCodigoRecuperacion(email, codigoOtp);
        if (!emailEnviado) {
            return Map.of("success", false, "error", "Error al enviar email");
        }
        
        System.out.println("✅ Código de recuperación enviado a: " + email + " (DNI: " + dni + ")");
        return Map.of(
                "success", true,
                "mensaje", "Código enviado al email",
                "codigo", codigoOtp  // SOLO PARA DESARROLLO
        );
    }
    
    /**
     * ✅ Confirma OTP y cambia contraseña
     * ✨ CORREGIDO: Busca por DNI únicamente, no por DNI+Email
     */
    public Map<String, Object> confirmarRecuperacionPassword(
            String dni,
            String email,
            String codigoOtp,
            String passwordNueva,
            String passwordConfirm) {
        // Validaciones
        if (dni == null || email == null || codigoOtp == null || 
            passwordNueva == null || passwordConfirm == null) {
            return Map.of("success", false, "error", "Faltan campos requeridos");
        }
        
        // Validar formato DNI
        if (!dni.matches("^[0-9]{8}$")) {
            return Map.of("success", false, "error", "DNI inválido");
        }
        
        // Validar que las contraseñas coincidan
        if (!passwordNueva.equals(passwordConfirm)) {
            return Map.of("success", false, "error", "Las contraseñas no coinciden");
        }
        
        // Validar contraseña mínima
        if (passwordNueva.length() < 6) {
            return Map.of("success", false, "error", "La contraseña debe tener al menos 6 caracteres");
        }
        
        // Validar código OTP
        if (!otpService.validarOtp(email, codigoOtp)) {
            return Map.of("success", false, "error", "Código OTP inválido o expirado");
        }
        
        // ✅ Buscar cliente SOLO por DNI
        Optional<Cliente> clienteOpt = clienteRepository.findByDni(dni);
        if (clienteOpt.isEmpty()) {
            return Map.of("success", false, "error", "Cliente no encontrado");
        }
        
        Cliente cliente = clienteOpt.get();
        
        // ✅ Verificar que el email coincida
        if (!cliente.getEmail().equalsIgnoreCase(email)) {
            return Map.of("success", false, "error", "El email no coincide con el registrado");
        }
        
        try {
            cliente.setPassword(passwordEncoder.encode(passwordNueva));
            clienteRepository.save(cliente);
            System.out.println("✅ Contraseña actualizada para: " + email + " (DNI: " + dni + ")");
            return Map.of(
                    "success", true,
                    "mensaje", "Contraseña actualizada exitosamente"
            );
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar contraseña: " + e.getMessage());
            return Map.of("success", false, "error", "Error al actualizar contraseña");
        }
    }
}
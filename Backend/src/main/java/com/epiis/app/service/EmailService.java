package com.epiis.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * ✅ Envía email con código OTP (ASINCRÓNICO)
     * Responde inmediatamente sin esperar el envío
     */
    public boolean enviarCodigoOtp(String email, String codigo) {
        try {
            System.out.println("📧 [SYNC] Iniciando envío de email a: " + email);
            // Enviar email de forma asincrónica
            enviarEmailAsync(email, "Código de Verificación - POLARIS",
                    "Hola,\n\n" +
                    "Tu código de verificación es: " + codigo + "\n\n" +
                    "Este código expira en 10 minutos.\n\n" +
                    "Si no solicitaste este código, ignora este mensaje.\n\n" +
                    "Saludos,\nEquipo POLARIS");
            
            System.out.println("✅ [SYNC] Email enviado a Background para: " + email);
            return true; // Responder inmediatamente
        } catch (Exception e) {
            System.err.println("❌ [SYNC] Error al encolar email: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ Envía email de confirmación de cuenta (ASINCRÓNICO)
     */
    public boolean enviarConfirmacionCuenta(String email, String nombres) {
        try {
            System.out.println("📧 [SYNC] Iniciando email de bienvenida a: " + email);
            enviarEmailAsync(email, "¡Bienvenido a POLARIS! - Cuenta Creada",
                    "Hola " + nombres + ",\n\n" +
                    "¡Tu cuenta en POLARIS ha sido creada exitosamente!\n\n" +
                    "Ahora puedes acceder a todos nuestros servicios de café y waffles.\n\n" +
                    "Saludos,\nEquipo POLARIS");
            
            System.out.println("✅ [SYNC] Email de bienvenida encolado para: " + email);
            return true;
        } catch (Exception e) {
            System.err.println("❌ [SYNC] Error al encolar email de bienvenida: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ Envía email para recuperación de contraseña (ASINCRÓNICO)
     */
    public boolean enviarCodigoRecuperacion(String email, String codigo) {
        try {
            System.out.println("📧 [SYNC] Iniciando email de recuperación a: " + email);
            enviarEmailAsync(email, "Código de Recuperación de Contraseña - POLARIS",
                    "Hola,\n\n" +
                    "Recibimos tu solicitud de recuperación de contraseña.\n\n" +
                    "Tu código de verificación es: " + codigo + "\n\n" +
                    "Este código expira en 10 minutos.\n\n" +
                    "Si no solicitaste este código, ignora este mensaje.\n\n" +
                    "Saludos,\nEquipo POLARIS");
            
            System.out.println("✅ [SYNC] Email de recuperación encolado para: " + email);
            return true;
        } catch (Exception e) {
            System.err.println("❌ [SYNC] Error al encolar email de recuperación: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔄 MÉTODO ASINCRÓNICO - Se ejecuta en background
     * No bloquea la respuesta del API
     */
    @Async
    public void enviarEmailAsync(String email, String asunto, String cuerpo) {
        try {
            System.out.println("⏳ [ASYNC] Enviando email a: " + email);
            
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(email);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            
            mailSender.send(mensaje);
            System.out.println("✅ [ASYNC] Email enviado exitosamente a: " + email);
        } catch (Exception e) {
            System.err.println("❌ [ASYNC] Error al enviar email a " + email + ": " + e.getMessage());
        }
    }
}
package com.epiis.app.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    // Almacenar OTP en memoria (fallback cuando Redis no está disponible)
    private static final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    
    private static final int OTP_LENGTH = 6;
    private static final long EXPIRATION_MINUTES = 10;

    /**
     * ✅ Genera código OTP de 6 dígitos
     */
    public String generarOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * ✅ Guarda OTP en memoria con expiración
     */
    public void guardarOtp(String email, String otp) {
        try {
            long expirationTime = System.currentTimeMillis() + (EXPIRATION_MINUTES * 60 * 1000);
            OtpData otpData = new OtpData(otp, expirationTime);
            otpStorage.put(email, otpData);
            System.out.println("✅ OTP guardado en memoria para: " + email);
        } catch (Exception e) {
            System.err.println("❌ Error al guardar OTP: " + e.getMessage());
        }
    }

    /**
     * ✅ Valida el OTP ingresado
     */
    public boolean validarOtp(String email, String otpIngresado) {
        try {
            OtpData otpData = otpStorage.get(email);

            if (otpData == null) {
                System.out.println("❌ OTP no encontrado para: " + email);
                return false;
            }

            // Verificar si el OTP ha expirado
            if (System.currentTimeMillis() > otpData.expirationTime) {
                System.out.println("❌ OTP expirado para: " + email);
                otpStorage.remove(email);
                return false;
            }

            // Validar el código OTP
            if (otpData.otp.equals(otpIngresado)) {
                otpStorage.remove(email);  // Eliminar después de validar
                System.out.println("✅ OTP validado correctamente para: " + email);
                return true;
            } else {
                System.out.println("❌ OTP incorrecto para: " + email);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error al validar OTP: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ Elimina OTP después de usar
     */
    public void eliminarOtp(String email) {
        try {
            otpStorage.remove(email);
            System.out.println("✅ OTP eliminado para: " + email);
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar OTP: " + e.getMessage());
        }
    }

    /**
     * ✅ Clase interna para almacenar OTP con tiempo de expiración
     */
    private static class OtpData {
        String otp;
        long expirationTime;

        OtpData(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }
    }
}
package com.epiis.app.util;

// ✅ VERSIÓN COMPATIBLE CON JJWT 0.11.5
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:miClaveSecretaMuySeguraDeAlMenos256BitsParaHS256Que1Tenga32Caracteres}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Genera un token JWT con la información del empleado
     */
    public String generateToken(String idEmpleado, String nombre, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        claims.put("nombre", nombre);
        return createToken(claims, idEmpleado);
    }

    /**
     * Crea el token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // idEmpleado
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el idEmpleado del token
     */
    public String getIdEmpleadoFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Extrae el rol del token
     */
    public String getRolFromToken(String token) {
        return (String) getAllClaimsFromToken(token).get("rol");
    }

    /**
     * Extrae el nombre del token
     */
    public String getNombreFromToken(String token) {
        return (String) getAllClaimsFromToken(token).get("nombre");
    }

    /**
     * Verifica si el token es válido
     */
    public Boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el token ha expirado
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getAllClaimsFromToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Obtiene todos los claims del token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
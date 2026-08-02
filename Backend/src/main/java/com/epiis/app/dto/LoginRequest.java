package com.epiis.app.dto;

// ✅ ARCHIVO NUEVO - Crear aquí: src/main/java/com/epiis/app/dto/LoginRequest.java

public class LoginRequest {
    private String nombre;
    private String password;

    // Constructores
    public LoginRequest() {}

    public LoginRequest(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
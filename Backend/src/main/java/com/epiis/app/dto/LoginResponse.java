package com.epiis.app.dto;

// ✅ ARCHIVO NUEVO - Crear aquí: src/main/java/com/epiis/app/dto/LoginResponse.java

public class LoginResponse {
    private String token;
    private String idEmpleado;
    private String nombre;
    private String rol;
    private Boolean estado;
    private String mensaje;

    // Constructores
    public LoginResponse() {}

    public LoginResponse(String token, String idEmpleado, String nombre, String rol, Boolean estado, String mensaje) {
        this.token = token;
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.rol = rol;
        this.estado = estado;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
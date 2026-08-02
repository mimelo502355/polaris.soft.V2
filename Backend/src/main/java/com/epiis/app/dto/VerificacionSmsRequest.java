package com.epiis.app.dto;

import jakarta.validation.constraints.NotBlank;

public class VerificacionSmsRequest {

    @NotBlank(message = "DNI es requerido")
    private String dni;

    @NotBlank(message = "Teléfono es requerido")
    private String telefono;

    // GETTERS Y SETTERS
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
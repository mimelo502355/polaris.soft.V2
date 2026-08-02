package com.epiis.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegistroClienteRequest {

    @NotBlank(message = "DNI es requerido")
    @Pattern(regexp = "^[0-9]{8}$", message = "DNI debe tener 8 dígitos")
    private String dni;

    @NotBlank(message = "Teléfono es requerido")
    @Pattern(regexp = "^\\d{9}$", message = "Teléfono debe tener 9 dígitos")
    private String telefono;

    @NotBlank(message = "Contraseña es requerida")
    private String password;

    // GETTERS Y SETTERS
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
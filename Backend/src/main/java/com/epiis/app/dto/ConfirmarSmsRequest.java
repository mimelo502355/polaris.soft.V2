package com.epiis.app.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfirmarSmsRequest {

    @NotBlank(message = "DNI es requerido")
    private String dni;

    @NotBlank(message = "Código SMS es requerido")
    private String codigo;

    // GETTERS Y SETTERS
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
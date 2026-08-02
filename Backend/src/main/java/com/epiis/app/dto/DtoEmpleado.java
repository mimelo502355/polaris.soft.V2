package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;

public class DtoEmpleado extends DtoGeneric {
    private String idEmpleado;
    private String nombre;
    private String password;
    private String rol;
    private Boolean estado;

    public String getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
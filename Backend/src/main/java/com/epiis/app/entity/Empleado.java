package com.epiis.app.entity;

import java.sql.Timestamp;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleado")
public class Empleado extends EntityGeneric {

    @Id
    @Column(name = "\"idEmpleado\"", length = 36)
    private String idEmpleado;

    @Column(name = "\"nombre\"", nullable = false, length = 100)
    private String nombre;

    @Column(name = "\"password\"", nullable = false, length = 100)
    private String password;

    @Column(name = "\"rol\"", nullable = false)
    private String rol = "EMPLEADO";

    @Column(name = "\"estado\"")
    private Boolean estado = true;

    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        this.createdAt = ahora;
        this.updatedAt = ahora;
        if (this.rol == null || this.rol.trim().isEmpty()) {
            this.rol = "EMPLEADO";
        }
        if (this.estado == null) {
            this.estado = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

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

    @Override
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
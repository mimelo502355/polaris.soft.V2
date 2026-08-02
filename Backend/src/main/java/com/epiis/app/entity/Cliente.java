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
@Table(name = "cliente")
public class Cliente extends EntityGeneric {
    
    @Id
    @Column(name = "\"idCliente\"", length = 36)
    private String idCliente;
    
    @Column(name = "\"dni\"", nullable = false, length = 20, unique = true)
    private String dni;
    
    @Column(name = "\"nombres\"", nullable = false, length = 100)
    private String nombres;
    
    @Column(name = "\"apellido_paterno\"", nullable = false, length = 100)
    private String apellido_paterno;
    
    @Column(name = "\"apellido_materno\"", length = 100)
    private String apellido_materno;
    
    @Column(name = "\"telefono\"", nullable = true, length = 20, unique = true)
    private String telefono;
    
    @Column(name = "\"email\"", nullable = true, length = 100, unique = true)
    private String email;
    
    @Column(name = "\"password\"", nullable = false, length = 255)
    private String password;
    
    @Column(name = "\"estado_verificacion\"")
    private Boolean estado_verificacion = false;
    
    @Column(name = "\"fecha_verificacion\"")
    private Timestamp fecha_verificacion;
    
    @Column(name = "\"codigo_sms\"", length = 6)
    private String codigo_sms;
    
    @Column(name = "\"intentos_sms\"")
    private Integer intentos_sms = 0;
    
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
        if (this.estado == null) {
            this.estado = true;
        }
        if (this.estado_verificacion == null) {
            this.estado_verificacion = false;
        }
        if (this.intentos_sms == null) {
            this.intentos_sms = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    // ===== GETTERS Y SETTERS =====
    
    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellido_paterno() { return apellido_paterno; }
    public void setApellido_paterno(String apellido_paterno) { this.apellido_paterno = apellido_paterno; }
    
    public String getApellido_materno() { return apellido_materno; }
    public void setApellido_materno(String apellido_materno) { this.apellido_materno = apellido_materno; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Boolean getEstado_verificacion() { return estado_verificacion; }
    public void setEstado_verificacion(Boolean estado_verificacion) { this.estado_verificacion = estado_verificacion; }
    
    public Timestamp getFecha_verificacion() { return fecha_verificacion; }
    public void setFecha_verificacion(Timestamp fecha_verificacion) { this.fecha_verificacion = fecha_verificacion; }
    
    public String getCodigo_sms() { return codigo_sms; }
    public void setCodigo_sms(String codigo_sms) { this.codigo_sms = codigo_sms; }
    
    public Integer getIntentos_sms() { return intentos_sms; }
    public void setIntentos_sms(Integer intentos_sms) { this.intentos_sms = intentos_sms; }
    
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    
    @Override
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
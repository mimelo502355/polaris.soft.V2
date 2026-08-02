package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoAcompanamiento extends DtoGeneric {

    private String idAcompanamiento;
    private String nombreAcompanamiento;
    private String tipoAcompanamiento; 
    
    public String getIdAcompanamiento() { return idAcompanamiento; }
    public void setIdAcompanamiento(String idAcompanamiento) { this.idAcompanamiento = idAcompanamiento; }

    public String getNombreAcompanamiento() { return nombreAcompanamiento; }
    public void setNombreAcompanamiento(String nombreAcompanamiento) { this.nombreAcompanamiento = nombreAcompanamiento; }

    public String getTipoAcompanamiento() { return tipoAcompanamiento; }
    public void setTipoAcompanamiento(String tipoAcompanamiento) { this.tipoAcompanamiento = tipoAcompanamiento; }
}

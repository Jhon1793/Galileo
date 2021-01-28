package com.des.galtest.model;

import androidx.annotation.NonNull;

public class SitioWeb {
    private String UID;
    private String nombreSitio;
    private String Encargado;

    public SitioWeb() {
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNombreSitio() {
        return nombreSitio;
    }

    public void setNombreSitio(String nombreSitio) {
        this.nombreSitio = nombreSitio;
    }

    public String getEncargado() {
        return Encargado;
    }

    public void setEncargado(String encargado) {
        Encargado = encargado;
    }

    @NonNull
    @Override
    public String toString() {
        return nombreSitio +" "+Encargado;
    }
}

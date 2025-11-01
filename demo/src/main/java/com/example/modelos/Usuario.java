package com.example.modelos;

public class Usuario {
    private String cedula;
    private String primerNombre;
    private String email;

    public Usuario(String cedula, String primerNombre, String email) {
        this.cedula = cedula;
        this.primerNombre = primerNombre;
        this.email = email;
    }

    // Getters
    public String getCedula() { return cedula; }
    public String getPrimerNombre() { return primerNombre; }
    public String getEmail() { return email; }

    // Setters
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }
    public void setEmail(String email) { this.email = email; }
}

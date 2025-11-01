package com.example.modelos;

import java.time.LocalDate;

public class RutinaFavoritaModelo {

    private int codRutina;
    private String nombreRutina;
    private String descripcionRutina;
    private String duracionRutina;
    private LocalDate fechaFavorito;
    private LocalDate fechaNoFavorito; // Null si sigue siendo favorita
    private String cedulaUsuario;

    public RutinaFavoritaModelo(int codRutina, String nombreRutina, String descripcionRutina, String duracionRutina, LocalDate fechaFavorito, LocalDate fechaNoFavorito, String cedulaUsuario) {
        this.codRutina = codRutina;
        this.nombreRutina = nombreRutina;
        this.descripcionRutina = descripcionRutina;
        this.duracionRutina = duracionRutina;
        this.fechaFavorito = fechaFavorito;
        this.fechaNoFavorito = fechaNoFavorito;
        this.cedulaUsuario = cedulaUsuario;
    }

    // Getters
    public int getCodRutina() { return codRutina; }
    public String getNombreRutina() { return nombreRutina; }
    public String getDescripcionRutina() { return descripcionRutina; }
    public String getDuracionRutina() { return duracionRutina; }
    public LocalDate getFechaFavorito() { return fechaFavorito; }
    public LocalDate getFechaNoFavorito() { return fechaNoFavorito; }
    public String getCedulaUsuario() { return cedulaUsuario; }
    
    // Setter (solo necesario para fechaNoFavorito en este CRUD)
    public void setFechaNoFavorito(LocalDate fechaNoFavorito) { this.fechaNoFavorito = fechaNoFavorito; }
}

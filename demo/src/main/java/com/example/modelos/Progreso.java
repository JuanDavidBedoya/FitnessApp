package com.example.modelos;

import java.time.LocalDate;

public class Progreso {
    private int codProgreso;
    private LocalDate fechaRegistro;
    private double peso;
    private double caloriasQuemadas;
    private String observaciones;
    private String cedulaUsuario; // FK General_Usuario_cedula

    // Constructor para CREATE
    public Progreso(LocalDate fechaRegistro, double peso, double caloriasQuemadas, String observaciones, String cedulaUsuario) {
        this.fechaRegistro = fechaRegistro;
        this.peso = peso;
        this.caloriasQuemadas = caloriasQuemadas;
        this.observaciones = observaciones;
        this.cedulaUsuario = cedulaUsuario;
    }
    
    // Constructor para READ/UPDATE
    public Progreso(int codProgreso, LocalDate fechaRegistro, double peso, double caloriasQuemadas, String observaciones, String cedulaUsuario) {
        this(fechaRegistro, peso, caloriasQuemadas, observaciones, cedulaUsuario);
        this.codProgreso = codProgreso;
    }

    // Getters
    public int getCodProgreso() { return codProgreso; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public double getPeso() { return peso; }
    public double getCaloriasQuemadas() { return caloriasQuemadas; }
    public String getObservaciones() { return observaciones; }
    public String getCedulaUsuario() { return cedulaUsuario; }

    // Setters
    public void setCodProgreso(int codProgreso) { this.codProgreso = codProgreso; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setPeso(double peso) { this.peso = peso; }
    public void setCaloriasQuemadas(double caloriasQuemadas) { this.caloriasQuemadas = caloriasQuemadas; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setCedulaUsuario(String cedulaUsuario) { this.cedulaUsuario = cedulaUsuario; }
}

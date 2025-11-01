package com.example.modelos;

import java.time.LocalDate;

public class ObjetivoAsignado {

    private int codObjetivo;
    private String nombre;
    private String descripcion;
    private LocalDate fechaAsignacion;
    private String cedulaUsuario;

    public ObjetivoAsignado(int codObjetivo, String nombre, String descripcion, LocalDate fechaAsignacion, String cedulaUsuario) {
        this.codObjetivo = codObjetivo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaAsignacion = fechaAsignacion;
        this.cedulaUsuario = cedulaUsuario;
    }

    // Getters
    public int getCodObjetivo() { return codObjetivo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public String getCedulaUsuario() { return cedulaUsuario; }
}

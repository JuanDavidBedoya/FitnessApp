package com.example.modelos;

import java.time.LocalDate;
import java.time.LocalTime;

public class Recordatorio {
    private int codRecordatorio;
    private String mensaje;
    private LocalDate fecha;
    private LocalTime hora;
    private String cedulaUsuario; // FK General_Usuario_cedula

    // Constructor para CREATE
    public Recordatorio(String mensaje, LocalDate fecha, LocalTime hora, String cedulaUsuario) {
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.hora = hora;
        this.cedulaUsuario = cedulaUsuario;
    }

    // Constructor para READ/UPDATE
    public Recordatorio(int codRecordatorio, String mensaje, LocalDate fecha, LocalTime hora, String cedulaUsuario) {
        this(mensaje, fecha, hora, cedulaUsuario);
        this.codRecordatorio = codRecordatorio;
    }

    // Getters
    public int getCodRecordatorio() { return codRecordatorio; }
    public String getMensaje() { return mensaje; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getCedulaUsuario() { return cedulaUsuario; }

    // Setters
    public void setCodRecordatorio(int codRecordatorio) { this.codRecordatorio = codRecordatorio; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public void setCedulaUsuario(String cedulaUsuario) { this.cedulaUsuario = cedulaUsuario; }
}

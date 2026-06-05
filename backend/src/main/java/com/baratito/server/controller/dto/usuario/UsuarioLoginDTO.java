package com.baratito.server.controller.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioLoginDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    // Constructor vacío (lo necesita Jackson para parsear el JSON)
    public UsuarioLoginDTO() {
    }

    public UsuarioLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
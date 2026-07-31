package com.baratito.server.service;

import com.baratito.server.controller.dto.usuario.CambiarPasswordDTO;
import com.baratito.server.controller.dto.usuario.UsuarioActualizarDTO;
import com.baratito.server.controller.dto.usuario.UsuarioRegistroDTO;
import com.baratito.server.model.Usuario;
import com.baratito.server.persistence.interfaces.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    public Usuario registrar(UsuarioRegistroDTO dto) {
        //chequea si ese email ya es usado por otro
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        //cifra la password para que no sea un texto plano
        usuario.setPassword(encoder.encode(dto.getPassword()));

        //guarda y retorna
        return usuarioRepository.save(usuario);

    }

    public Usuario login(String email, String password) {
        //busca al usuario por email (no hay emails repetidos)
        Usuario usuario = usuarioRepository.findByEmail(email);

        //si no se encontro un usuario tira excepcion
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        //si encontro un usuario pero la contraseña no matchea, tira error
        if (!encoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Datos incorrectos");
        }

        //y si no retorna el usuario
        return usuario;
    }


    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public Usuario actualizarPerfil(Long usuarioId, UsuarioActualizarDTO dto) {
        Usuario usuario = obtenerPorId(usuarioId);

        // confirma identidad antes de permitir el cambio
        if (!encoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        // si cambia el email, hay que chequear que no choque con otro usuario
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());

        return usuarioRepository.save(usuario);
    }

    public void cambiarPassword(Long usuarioId, CambiarPasswordDTO dto) {
        Usuario usuario = obtenerPorId(usuarioId);

        if (!encoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        usuario.setPassword(encoder.encode(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }
}

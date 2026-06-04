package com.baratito.server.service;

import com.baratito.server.model.Usuario;
import com.baratito.server.persistence.interfaces.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    public Usuario registrar(Usuario usuario) {
        //chequea si ese email ya es usado por otro
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }
        //cifra la password para que no sea un texto plano
        usuario.setPassword(encoder.encode(usuario.getPassword()));

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
}

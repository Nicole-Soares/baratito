package com.baratito.server.controller;
import com.baratito.server.controller.dto.usuario.UsuarioDTO;
import com.baratito.server.controller.dto.usuario.UsuarioLoginDTO;
import com.baratito.server.model.Usuario;
import com.baratito.server.security.TokenService;
import com.baratito.server.service.CarritoService;
import com.baratito.server.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
    Frontend hace GET /api/user/register con Authorization: Bearer <token>.

    Spring Security recibe la request → ejecuta JwtAuthenticationFilter.

    El filtro valida el token → si es correcto, el SecurityContext queda con el userId.

    El endpoint /api/user/register se ejecuta con el usuario autenticado.

    Si el token es inválido o falta → Spring devuelve 401 Unauthorized.

     */

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", exposedHeaders = "Authorization")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TokenService tokenService;
    private final CarritoService carritoService;

    public UsuarioController(UsuarioService usuarioService, TokenService tokenService, CarritoService carritoService) {
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
        this.carritoService = carritoService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody Usuario usuario) {

        Usuario nuevoUsuario = usuarioService.registrar(usuario);

        //genero token
        String token = tokenService.generateToken(nuevoUsuario.getId());

        //transformo al usuario para retornarlo con los datos que yo quiero devolver
        UsuarioDTO respuesta = new UsuarioDTO(nuevoUsuario.getId(), nuevoUsuario.getNombre(), nuevoUsuario.getEmail());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(respuesta);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioLoginDTO usuario) {

        Usuario usuarioEncontrado = usuarioService.login(usuario.getEmail(), usuario.getPassword());

        String token = tokenService.generateToken(usuarioEncontrado.getId());

        UsuarioDTO respuesta = new UsuarioDTO(usuarioEncontrado.getId(), usuarioEncontrado.getNombre(), usuarioEncontrado.getEmail());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(respuesta);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        carritoService.guardarHistorial();

        return ResponseEntity.ok().body("Logout exitoso");
    }

}


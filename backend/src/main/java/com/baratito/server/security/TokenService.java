package com.baratito.server.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

/*
el token JWT se construye combinando tres cosas:

Header → define el algoritmo de firma (ej. HS256).

Payload (claims) → la información que se quiera meter en el token (ej. el userId, roles, email).

Firma (signature) → se genera aplicando el algoritmo (HS256) sobre el header + payload usando la clave secreta.

por ende para chequear que sea válido tiene que tener la

*/


@Service
public class TokenService {

    // Clave secreta para firmar los tokens
    @Value("${jwt.secret}")
    private String secret;

    // Tiempo que duran los tokens: 1 hora
    @Value("${jwt.expiration}")
    private long expirationTime;

    // Generar token con el userId + clave secreta
    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    // Validar token y devolver el userId si existe el token
    public Long validateToken(String token) {

        //solo valida por firma y tiempo de exp en este caso
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(secret.getBytes()) // clave secreta
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }
}

package com.baratito.server.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.baratito.server.security.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

//Intercepta antes de que llegue al controller y chequea si hay token, si es el adecuado
//si no hay token, fue alterado o expiero, tira un 401 Unauthorized (preguntandole a TokenService)
//si hay token y es válido, crea un UsernamePasswordAuthenticationToken que es como una tarjeta para identificar al usuario
// que esta haciendo la petición
//lo guarda en SecurityContextHolder que es donde se guardan las autorizaciones
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        //chequea si viene con header / token para validar el token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);


            try {
                Long userId = tokenService.validateToken(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}


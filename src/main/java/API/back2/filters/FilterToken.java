package API.back2.filters;

import API.back2.repositories.UsuarioRepository;
import API.back2.services.TokenService;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FilterToken extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token;

        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            response.sendError(
                    HttpStatus.SC_UNAUTHORIZED,
                    "Token de acesso inválido ou ausente"
            );
            return;
        }

        token = authorizationHeader.replace("Bearer ", "");
        try {
            var subject = this.tokenService.getSubject(token);
            var usuario = this.usuarioRepository.findByLogin(subject);

            var authentication = new UsernamePasswordAuthenticationToken(usuario,
                    null, usuario.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (TokenExpiredException exception) {
            response.sendError(
                    HttpStatus.SC_UNAUTHORIZED,
                    "O token de acesso expirou"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}

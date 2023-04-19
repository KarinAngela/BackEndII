package API.back2.services;

import API.back2.models.Usuario;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import com.auth0.jwt.JWT;


@Service
public class TokenService {
    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer("Produtos")
                .withSubject(usuario.getUsername())
                .withClaim("id", usuario.getId())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(10)
                        .toInstant(ZoneOffset.of("-03:00"))
                )
                .sign(Algorithm.HMAC256("secret"));
    }

    public String getSubject(String token) throws TokenExpiredException {
        return JWT.require(Algorithm.HMAC256("secret"))
                .withIssuer("Produtos")
                .build()
                .verify(token)
                .getSubject();
    }
}

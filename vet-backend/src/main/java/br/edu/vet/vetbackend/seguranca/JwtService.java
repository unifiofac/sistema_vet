package br.edu.vet.vetbackend.seguranca;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long horas;

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.expiracao-horas}") long horas) {
        this.encoder = encoder;
        this.horas = horas;
    }

    public String gerar(Long id, String tipo) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(id))
                .issuedAt(agora)
                .expiresAt(agora.plus(horas, ChronoUnit.HOURS))
                .claim("tipo", tipo.toUpperCase())
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
    }
}

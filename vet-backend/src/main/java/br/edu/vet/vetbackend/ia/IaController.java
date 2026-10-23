package br.edu.vet.vetbackend.ia;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ia")
public class IaController {

    private static final Logger log = LoggerFactory.getLogger(IaController.class);

    private final IaService service;

    public IaController(IaService service) {
        this.service = service;
    }

    @PostMapping
    public IaResponse perguntar(@Valid @RequestBody IaRequest req) {
        try {
            return new IaResponse(service.responder(req));
        } catch (RestClientException e) {
            // não loga o corpo da requisição: contém dados de pacientes
            log.error("Falha ao consultar o Gemini: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha ao consultar a IA.");
        }
    }
}

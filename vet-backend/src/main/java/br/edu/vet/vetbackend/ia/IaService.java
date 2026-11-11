package br.edu.vet.vetbackend.ia;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class IaService {

    private static final Logger log = LoggerFactory.getLogger(IaService.class);

    private static final int TENTATIVAS_POR_MODELO = 2;

    private static final String INSTRUCAO = """
            Você é um assistente de apoio clínico para médicos-veterinários, integrado a um sistema de clínica.
            Responda sempre em português do Brasil, de forma objetiva e organizada.
            Use texto simples, sem markdown: nada de asteriscos, # ou tabelas. Para listas, use hífen no início da linha.
            Considere os dados do paciente informados abaixo. Se faltar informação importante, diga o que falta e peça.
            Você é uma ferramenta de apoio: a decisão clínica final é sempre do veterinário.
            Em sinais de emergência (convulsão, dificuldade respiratória, sangramento, intoxicação), destaque a urgência no início da resposta.
            """;

    private final RestClient restClient;
    private final List<String> modelos = new ArrayList<>();

    public IaService(@Value("${gemini.api-key}") String apiKey,
                     @Value("${gemini.model}") String model,
                     @Value("${gemini.model-reserva:}") String modeloReserva) {
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(http);
        fabrica.setReadTimeout(Duration.ofSeconds(20));

        this.restClient = RestClient.builder()
                .requestFactory(fabrica)
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();

        modelos.add(model);
        if (modeloReserva != null && !modeloReserva.isBlank()) {
            modelos.add(modeloReserva);
        }
    }

    public String responder(IaRequest req) {
        Map<String, Object> body = montarCorpo(req);
        RestClientException ultimaFalha = null;

        for (String modelo : modelos) {
            for (int tentativa = 1; tentativa <= TENTATIVAS_POR_MODELO; tentativa++) {
                try {
                    return extrairTexto(chamar(modelo, body));
                } catch (RestClientException e) {
                    ultimaFalha = e;
                    log.warn("Gemini ({}) tentativa {} falhou: {}", modelo, tentativa, resumo(e));
                    if (!retentavel(e)) break; // erro permanente: passa para o próximo modelo
                    if (tentativa < TENTATIVAS_POR_MODELO) pausar();
                }
            }
        }
        throw ultimaFalha;
    }

    private Map<?, ?> chamar(String modelo, Map<String, Object> body) {
        return restClient.post()
                .uri("/models/{model}:generateContent", modelo)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    private Map<String, Object> montarCorpo(IaRequest req) {
        List<Map<String, Object>> contents = new ArrayList<>();

        if (req.historico() != null) {
            for (IaRequest.Mensagem m : req.historico()) {
                String papel = "model".equals(m.papel()) ? "model" : "user";
                contents.add(Map.of("role", papel, "parts", List.of(Map.of("text", m.texto()))));
            }
        }
        contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", req.pergunta()))));

        String instrucao = INSTRUCAO + "\nDADOS DO PACIENTE:\n" + descrever(req.paciente());

        return Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", instrucao))),
                "contents", contents);
    }

    // 429 (limite), 5xx (Google instável) e timeout/falha de rede valem nova tentativa
    private boolean retentavel(RestClientException e) {
        if (e instanceof ResourceAccessException) return true;
        if (e instanceof RestClientResponseException r) {
            int status = r.getStatusCode().value();
            return status == 429 || status >= 500;
        }
        return false;
    }

    private String resumo(RestClientException e) {
        String msg = String.valueOf(e.getMessage());
        return msg.length() > 200 ? msg.substring(0, 200) + "..." : msg;
    }

    private void pausar() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private String descrever(IaRequest.PacienteContexto p) {
        StringBuilder sb = new StringBuilder();
        sb.append("- Nome: ").append(p.nome()).append('\n');
        sb.append("- Espécie: ").append(p.especie()).append('\n');
        if (p.raca() != null && !p.raca().isBlank()) sb.append("- Raça: ").append(p.raca()).append('\n');
        if (p.idade() != null) sb.append("- Idade: ").append(p.idade()).append(" anos\n");
        if (p.peso() != null) sb.append("- Peso: ").append(p.peso()).append(" kg\n");
        if (p.historicoMedico() != null && !p.historicoMedico().isBlank())
            sb.append("- Histórico médico: ").append(p.historicoMedico()).append('\n');
        if (p.alergias() != null && !p.alergias().isBlank())
            sb.append("- Alergias: ").append(p.alergias()).append('\n');
        if (p.medicamentosAtuais() != null && !p.medicamentosAtuais().isBlank())
            sb.append("- Medicamentos atuais: ").append(p.medicamentosAtuais()).append('\n');
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String extrairTexto(Map<?, ?> resposta) {
        try {
            List<Map<String, Object>> candidatos = (List<Map<String, Object>>) resposta.get("candidates");
            Map<String, Object> conteudo = (Map<String, Object>) candidatos.get(0).get("content");
            List<Map<String, Object>> partes = (List<Map<String, Object>>) conteudo.get("parts");

            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> parte : partes) {
                if (parte.get("text") != null) sb.append(parte.get("text"));
            }
            String texto = sb.toString().trim();
            if (!texto.isEmpty()) return texto;
        } catch (RuntimeException ignorada) {
            // cai no texto padrão abaixo (ex.: resposta bloqueada pelos filtros de segurança)
        }
        return "A IA não retornou uma resposta para esta pergunta. Tente reformulá-la.";
    }
}

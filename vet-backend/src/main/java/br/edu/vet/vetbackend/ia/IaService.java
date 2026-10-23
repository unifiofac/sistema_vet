package br.edu.vet.vetbackend.ia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IaService {

    private static final String INSTRUCAO = """
            Você é um assistente de apoio clínico para médicos-veterinários, integrado a um sistema de clínica.
            Responda sempre em português do Brasil, de forma objetiva e organizada.
            Use texto simples, sem markdown: nada de asteriscos, # ou tabelas. Para listas, use hífen no início da linha.
            Considere os dados do paciente informados abaixo. Se faltar informação importante, diga o que falta e peça.
            Você é uma ferramenta de apoio: a decisão clínica final é sempre do veterinário.
            Em sinais de emergência (convulsão, dificuldade respiratória, sangramento, intoxicação), destaque a urgência no início da resposta.
            """;

    private final RestClient restClient;
    private final String model;

    public IaService(@Value("${gemini.api-key}") String apiKey,
                     @Value("${gemini.model}") String model) {
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
        this.model = model;
    }

    public String responder(IaRequest req) {
        List<Map<String, Object>> contents = new ArrayList<>();

        if (req.historico() != null) {
            for (IaRequest.Mensagem m : req.historico()) {
                String papel = "model".equals(m.papel()) ? "model" : "user";
                contents.add(Map.of("role", papel, "parts", List.of(Map.of("text", m.texto()))));
            }
        }
        contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", req.pergunta()))));

        String instrucao = INSTRUCAO + "\nDADOS DO PACIENTE:\n" + descrever(req.paciente());

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", instrucao))),
                "contents", contents);

        Map<?, ?> resposta = restClient.post()
                .uri("/models/{model}:generateContent", model)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        return extrairTexto(resposta);
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

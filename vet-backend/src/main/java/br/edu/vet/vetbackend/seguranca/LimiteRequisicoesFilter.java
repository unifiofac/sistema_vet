package br.edu.vet.vetbackend.seguranca;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LimiteRequisicoesFilter extends OncePerRequestFilter {

    private static final long JANELA_MS = 60_000L;
    private static final int MAX_CHAVES = 10_000;

    private final int limiteLogin;
    private final int limiteIa;
    private final Map<String, Deque<Long>> acessos = new ConcurrentHashMap<>();

    public LimiteRequisicoesFilter(@Value("${app.limite.login-por-minuto}") int limiteLogin,
                                   @Value("${app.limite.ia-por-minuto}") int limiteIa) {
        this.limiteLogin = limiteLogin;
        this.limiteIa = limiteIa;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return limiteDe(request) == 0;
    }

    private int limiteDe(HttpServletRequest request) {
        if (!"POST".equals(request.getMethod())) return 0;
        String caminho = request.getRequestURI();
        if (caminho.equals("/api/auth/login")) return limiteLogin;
        if (caminho.equals("/api/ia")) return limiteIa;
        return 0;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        int limite = limiteDe(request);
        String chave = request.getRequestURI() + "|" + request.getRemoteAddr();
        long agora = System.currentTimeMillis();

        if (acessos.size() > MAX_CHAVES) {
            acessos.values().removeIf(d -> synchronizedVazia(d, agora));
        }

        Deque<Long> fila = acessos.computeIfAbsent(chave, k -> new ArrayDeque<>());
        boolean excedeu;
        synchronized (fila) {
            while (!fila.isEmpty() && agora - fila.peekFirst() > JANELA_MS) fila.pollFirst();
            excedeu = fila.size() >= limite;
            if (!excedeu) fila.addLast(agora);
        }

        if (excedeu) {
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json;charset=UTF-8");
            response.getOutputStream().write(
                    "{\"erro\":\"Muitas requisições. Aguarde um minuto e tente novamente.\"}"
                            .getBytes(StandardCharsets.UTF_8));
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean synchronizedVazia(Deque<Long> fila, long agora) {
        synchronized (fila) {
            while (!fila.isEmpty() && agora - fila.peekFirst() > JANELA_MS) fila.pollFirst();
            return fila.isEmpty();
        }
    }
}

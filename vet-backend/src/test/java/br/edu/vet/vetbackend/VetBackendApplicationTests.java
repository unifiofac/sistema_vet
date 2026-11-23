package br.edu.vet.vetbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// perfil "test": usa application-test.properties (chaves falsas e banco em memória),
// então roda sem variáveis de ambiente, inclusive no build da hospedagem
@SpringBootTest
@ActiveProfiles("test")
class VetBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}

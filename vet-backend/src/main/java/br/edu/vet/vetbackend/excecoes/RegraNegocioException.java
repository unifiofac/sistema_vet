package br.edu.vet.vetbackend.excecoes;

// Ex.: e-mail já cadastrado, cliente com pets não pode ser excluído
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}

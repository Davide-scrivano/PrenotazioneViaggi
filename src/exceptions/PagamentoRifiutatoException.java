package exceptions;

public class PagamentoRifiutatoException extends ApplicazioneException {

    private static final long serialVersionUID = 1L;

    public PagamentoRifiutatoException(String messaggio) {
        super(messaggio);
    }
}

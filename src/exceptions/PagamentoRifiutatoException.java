package exceptions;

public class PagamentoRifiutatoException extends Exception {

    private static final long serialVersionUID = 1L;

    public PagamentoRifiutatoException(String messaggio) {
        super(messaggio);
    }
}

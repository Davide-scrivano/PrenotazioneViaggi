package exceptions;

public class PacchettoNonDisponibileException extends Exception {

    private static final long serialVersionUID = 1L;

    public PacchettoNonDisponibileException(String messaggio) {
        super(messaggio);
    }
}

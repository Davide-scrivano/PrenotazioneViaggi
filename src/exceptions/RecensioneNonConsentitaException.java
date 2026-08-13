package exceptions;

public class RecensioneNonConsentitaException extends Exception {

    private static final long serialVersionUID = 1L;

    public RecensioneNonConsentitaException(String messaggio) {
        super(messaggio);
    }
}

package exceptions;

public class OperazioneNonConsentitaException extends Exception {

    private static final long serialVersionUID = 1L;

    public OperazioneNonConsentitaException(String messaggio) {
        super(messaggio);
    }
}

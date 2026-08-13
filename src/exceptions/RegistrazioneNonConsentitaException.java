package exceptions;

public class RegistrazioneNonConsentitaException extends Exception {

    private static final long serialVersionUID = 1L;

    public RegistrazioneNonConsentitaException(String messaggio) {
        super(messaggio);
    }
}

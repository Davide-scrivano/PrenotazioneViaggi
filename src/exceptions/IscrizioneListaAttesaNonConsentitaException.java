package exceptions;

public class IscrizioneListaAttesaNonConsentitaException extends Exception {

    private static final long serialVersionUID = 1L;

    public IscrizioneListaAttesaNonConsentitaException(String messaggio) {
        super(messaggio);
    }
}

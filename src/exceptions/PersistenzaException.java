package exceptions;

public class PersistenzaException extends ApplicazioneException {

    private static final long serialVersionUID = 1L;

    public PersistenzaException(String messaggio) {
        super(messaggio);
    }

    public PersistenzaException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}

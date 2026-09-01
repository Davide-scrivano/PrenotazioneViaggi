package exceptions;

public abstract class ApplicazioneException extends Exception {

    private static final long serialVersionUID = 1L;

    protected ApplicazioneException(String messaggio) {
        super(messaggio);
    }

    protected ApplicazioneException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}

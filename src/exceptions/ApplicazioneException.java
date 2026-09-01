package exceptions;

public abstract class ApplicazioneException extends Exception {

    private static final long serialVersionUID = 1L;

    public ApplicazioneException(String messaggio) {
        super(messaggio);
    }

    public ApplicazioneException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}

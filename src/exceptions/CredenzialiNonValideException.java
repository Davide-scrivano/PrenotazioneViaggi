package exceptions;

public class CredenzialiNonValideException extends ApplicazioneException {

    private static final long serialVersionUID = 1L;

    public CredenzialiNonValideException(String messaggio) {
        super(messaggio);
    }
}

package exceptions;

public class PacchettoNonDisponibileException extends ApplicazioneException {

    private static final long serialVersionUID = 1L;

    public PacchettoNonDisponibileException(String messaggio) {
        super(messaggio);
    }
}

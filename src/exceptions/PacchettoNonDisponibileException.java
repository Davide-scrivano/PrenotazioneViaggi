package exceptions;

public class PacchettoNonDisponibileException extends ApplicazioneException {

    private static final long serialVersionUID = 1L;

    public enum Motivo {
        NON_A_CATALOGO,
        POSTI_INSUFFICIENTI,
        FUORI_PERIODO
    }

    private final Motivo motivo;

    public PacchettoNonDisponibileException(String messaggio) {
        this(messaggio, null);
    }

    public PacchettoNonDisponibileException(String messaggio, Motivo motivo) {
        super(messaggio);
        this.motivo = motivo;
    }

    public Motivo getMotivo() {
        return motivo;
    }
}

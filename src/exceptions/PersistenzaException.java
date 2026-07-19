package exceptions;

/**
 * Eccezione lanciata quando il salvataggio o il caricamento dei dati
 * su un livello di persistenza (file o database) fallisce.
 * Viene gestita da chi la riceve continuando a far funzionare
 * l'applicazione solo in memoria, invece di bloccarla.
 */
public class PersistenzaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenzaException(String messaggio) {
        super(messaggio);
    }
}

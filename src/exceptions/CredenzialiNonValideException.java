package exceptions;

/**
 * Eccezione lanciata quando un tentativo di login fallisce
 * (nickname inesistente o password errata).
 * Viene gestita a livello di GestoreUtenti/UI mostrando un messaggio
 * chiaro all'utente e tenendo traccia dei tentativi falliti,
 * invece di limitarsi a propagare l'errore.
 */
public class CredenzialiNonValideException extends Exception {

    private static final long serialVersionUID = 1L;

    public CredenzialiNonValideException(String messaggio) {
        super(messaggio);
    }
}

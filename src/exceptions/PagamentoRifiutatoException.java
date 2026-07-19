package exceptions;

/**
 * Eccezione lanciata quando il gateway di pagamento rifiuta
 * l'operazione (importo non valido, autorizzazione negata, ecc.).
 * Viene gestita da GestorePrenotazioni: la prenotazione NON viene
 * creata e il pacchetto resta disponibile, invece di lasciare
 * il sistema in uno stato incoerente.
 */
public class PagamentoRifiutatoException extends Exception {

    private static final long serialVersionUID = 1L;

    public PagamentoRifiutatoException(String messaggio) {
        super(messaggio);
    }
}

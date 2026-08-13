package payment;

/**
 * Rappresenta un pagamento gia' avvenuto e ricostruito dal DAO. I dati
 * sensibili (numero carta, cvv, credenziali PayPal) non vengono mai
 * persistiti: qui restano solo descrizione e importo, sufficienti a
 * mostrare la prenotazione. elaboraPagamento() torna sempre true perche'
 * l'addebito e' gia' stato effettuato al momento della prenotazione.
 */
public class PagamentoRegistrato implements Pagamento {

    private final String descrizione;
    private final float costo;

    public PagamentoRegistrato(String descrizione, float costo) {
        this.descrizione = descrizione;
        this.costo = costo;
    }

    @Override
    public boolean elaboraPagamento() {
        return true;
    }

    @Override
    public float costo() {
        return costo;
    }

    @Override
    public String descrizione() {
        return descrizione;
    }
}

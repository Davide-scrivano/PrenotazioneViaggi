package payment;

public class CartaDiCreditoPagamento implements Pagamento {

    private static final int LUNGHEZZA_CVV = 3;

    private final String numeroCarta;
    private final String titolare;
    private final String scadenza;
    private final String cvv;
    private final float importo;

    CartaDiCreditoPagamento(String numeroCarta, String titolare, String scadenza, String cvv, float importo) {
        this.numeroCarta = numeroCarta;
        this.titolare = titolare;
        this.scadenza = scadenza;
        this.cvv = cvv;
        this.importo = importo;
    }

    @Override
    public boolean elaboraPagamento() {
        return numeroCarta != null && !numeroCarta.isEmpty()
                && titolare != null && !titolare.isEmpty()
                && scadenza != null && !scadenza.isEmpty()
                && cvv != null && cvv.length() == LUNGHEZZA_CVV;
    }

    @Override
    public float costo() {
        return importo;
    }

    @Override
    public String descrizione() {
        return "Carta di credito";
    }
}

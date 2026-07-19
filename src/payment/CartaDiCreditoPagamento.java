package payment;

public class CartaDiCreditoPagamento implements Pagamento {

    private String numeroCarta;
    private String titolare;
    private String scadenza;
    private String cvv;
    private float importo;

    public CartaDiCreditoPagamento(String numeroCarta, String titolare, String scadenza, String cvv, float importo) {
        this.numeroCarta = numeroCarta;
        this.titolare = titolare;
        this.scadenza = scadenza;
        this.cvv = cvv;
        this.importo = importo;
    }

    @Override
    public boolean metodoPagamento() {
        // verifica base: numero carta e cvv non vuoti
        return numeroCarta != null && !numeroCarta.isEmpty()
                && cvv != null && cvv.length() == 3;
    }

    @Override
    public float costo() {
        return importo;
    }
}
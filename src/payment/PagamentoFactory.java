package payment;

public class PagamentoFactory {

    public Pagamento creaCartaDiCreditoPagamento(String numeroCarta, String titolare, String scadenza, String cvv, float importo) {
        return new CartaDiCreditoPagamento(numeroCarta, titolare, scadenza, cvv, importo);
    }

    public Pagamento creaPaypalPagamento(String email, String password, float importo) {
        return new PayPalPagamento(email, password, importo);
    }
}
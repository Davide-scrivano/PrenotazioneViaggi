package payment;

class FactoryStrumentoPagamento {

    public StrumentoPagamento creaPagamentoConCarta(String numeroCarta, String titolare,
            String scadenza, String cvv) {
        return new StrumentoCartaDiCredito(numeroCarta, titolare, scadenza, cvv);
    }

    public StrumentoPagamento creaPagamentoConPayPal(String email, String password) {
        return new StrumentoPayPal(email, password);
    }
}

package payment;

public class PagamentoFactory {

    public Pagamento crea(MetodoPagamento metodo, DatiPagamento dati, float importo) {
        if (metodo == null) {
            throw new IllegalArgumentException("Metodo di pagamento non specificato.");
        }

        switch (metodo) {
            case PAYPAL:
                return new PayPalPagamento(dati.getEmailPaypal(), dati.getPasswordPaypal(), importo);
            case CARTA_DI_CREDITO:
            default:
                return new CartaDiCreditoPagamento(dati.getNumeroCarta(), dati.getTitolare(),
                        dati.getScadenza(), dati.getCvv(), importo);
        }
    }
}

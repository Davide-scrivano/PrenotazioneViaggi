package bean;

import payment.MetodoPagamento;

public class DatiPagamentoBean {

    private static final int LUNGHEZZA_CVV = 3;

    public static final String PAGAMENTO_CARTA = MetodoPagamento.CARTA_DI_CREDITO.name();
    public static final String PAGAMENTO_PAYPAL = MetodoPagamento.PAYPAL.name();

    private String metodoPagamento;
    private String numeroCarta;
    private String titolare;
    private String scadenza;
    private String cvv;
    private String emailPaypal;
    private String passwordPaypal;

    public String validaSintassi() {
        MetodoPagamento metodo = MetodoPagamento.daCodice(metodoPagamento);
        if (metodo == null) {
            return "Seleziona un metodo di pagamento.";
        }
        if (metodo == MetodoPagamento.PAYPAL) {
            return validaSintassiPaypal();
        }
        return validaSintassiCarta();
    }

    private String validaSintassiPaypal() {
        if (vuoto(emailPaypal)) {
            return "Inserisci l'email PayPal.";
        }
        if (!emailPaypal.contains("@")) {
            return "L'email PayPal non e' valida.";
        }
        if (vuoto(passwordPaypal)) {
            return "Inserisci la password PayPal.";
        }
        return null;
    }

    private String validaSintassiCarta() {
        if (vuoto(numeroCarta)) {
            return "Inserisci il numero della carta.";
        }
        if (vuoto(titolare)) {
            return "Inserisci il titolare della carta.";
        }
        if (vuoto(scadenza)) {
            return "Inserisci la scadenza della carta.";
        }
        if (vuoto(cvv)) {
            return "Inserisci il CVV.";
        }
        if (cvv.trim().length() != LUNGHEZZA_CVV) {
            return "Il CVV deve essere di " + LUNGHEZZA_CVV + " cifre.";
        }
        return null;
    }

    private boolean vuoto(String valore) {
        return valore == null || valore.isBlank();
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }

    public String getTitolare() {
        return titolare;
    }

    public void setTitolare(String titolare) {
        this.titolare = titolare;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getEmailPaypal() {
        return emailPaypal;
    }

    public void setEmailPaypal(String emailPaypal) {
        this.emailPaypal = emailPaypal;
    }

    public String getPasswordPaypal() {
        return passwordPaypal;
    }

    public void setPasswordPaypal(String passwordPaypal) {
        this.passwordPaypal = passwordPaypal;
    }
}

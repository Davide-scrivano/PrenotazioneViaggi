package payment;

public class DatiPagamento {

    private final String numeroCarta;
    private final String titolare;
    private final String scadenza;
    private final String cvv;
    private final String emailPaypal;
    private final String passwordPaypal;

    private DatiPagamento(String numeroCarta, String titolare, String scadenza, String cvv,
            String emailPaypal, String passwordPaypal) {
        this.numeroCarta = numeroCarta;
        this.titolare = titolare;
        this.scadenza = scadenza;
        this.cvv = cvv;
        this.emailPaypal = emailPaypal;
        this.passwordPaypal = passwordPaypal;
    }

    public static DatiPagamento perCarta(String numeroCarta, String titolare, String scadenza, String cvv) {
        return new DatiPagamento(numeroCarta, titolare, scadenza, cvv, null, null);
    }

    public static DatiPagamento perPaypal(String email, String password) {
        return new DatiPagamento(null, null, null, null, email, password);
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public String getTitolare() {
        return titolare;
    }

    public String getScadenza() {
        return scadenza;
    }

    public String getCvv() {
        return cvv;
    }

    public String getEmailPaypal() {
        return emailPaypal;
    }

    public String getPasswordPaypal() {
        return passwordPaypal;
    }
}

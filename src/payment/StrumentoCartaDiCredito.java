package payment;

class StrumentoCartaDiCredito implements StrumentoPagamento {

    private static final int CIFRE_VISIBILI = 4;

    private final String numeroCarta;
    private final String titolare;
    private final String scadenza;
    private final String cvv;

    StrumentoCartaDiCredito(String numeroCarta, String titolare, String scadenza, String cvv) {
        this.numeroCarta = numeroCarta;
        this.titolare = titolare;
        this.scadenza = scadenza;
        this.cvv = cvv;
    }

    @Override
    public String descrizione() {
        return MetodoPagamento.CARTA_DI_CREDITO.getDescrizione() + " " + numeroMascherato()
                + " - " + titolare + ", scad. " + scadenza;
    }

    @Override
    public String riferimento() {
        return numeroCarta;
    }

    @Override
    public String codiceSicurezza() {
        return cvv;
    }

    private String numeroMascherato() {
        if (numeroCarta == null || numeroCarta.length() <= CIFRE_VISIBILI) {
            return "****";
        }
        return "****" + numeroCarta.substring(numeroCarta.length() - CIFRE_VISIBILI);
    }
}

package payment;

class StrumentoPayPal implements StrumentoPagamento {

    private final String email;
    private final String password;

    StrumentoPayPal(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String descrizione() {
        return MetodoPagamento.PAYPAL.getDescrizione() + " - " + email;
    }

    @Override
    public String riferimento() {
        return email;
    }

    @Override
    public String codiceSicurezza() {
        return password;
    }
}

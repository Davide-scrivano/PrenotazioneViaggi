package payment;

public enum MetodoPagamento {

    CARTA_DI_CREDITO("Carta di credito"),
    PAYPAL("PayPal");

    private final String descrizione;

    MetodoPagamento(String descrizione) {
        this.descrizione = descrizione;
    }

    public static MetodoPagamento daCodice(String codice) {
        if (codice == null) {
            return null;
        }
        for (MetodoPagamento metodo : values()) {
            if (metodo.name().equalsIgnoreCase(codice.trim())) {
                return metodo;
            }
        }
        return null;
    }

    public String getDescrizione() {
        return descrizione;
    }
}

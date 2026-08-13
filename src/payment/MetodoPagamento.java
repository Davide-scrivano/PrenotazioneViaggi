package payment;

public enum MetodoPagamento {

    CARTA_DI_CREDITO,
    PAYPAL;

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
}

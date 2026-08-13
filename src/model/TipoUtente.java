package model;

public enum TipoUtente {

    CONSUMER,
    AGENZIA;

    public static TipoUtente daCodice(String codice) {
        if (codice == null) {
            return CONSUMER;
        }
        for (TipoUtente tipo : values()) {
            if (tipo.name().equalsIgnoreCase(codice.trim())) {
                return tipo;
            }
        }
        return CONSUMER;
    }
}

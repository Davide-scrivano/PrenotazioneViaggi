package model.valori;

public enum TipoUtente {

    CONSUMER,
    AGENZIA;

    public static TipoUtente daCodice(String codice) {
        if (codice != null && AGENZIA.name().equalsIgnoreCase(codice.trim())) {
            return AGENZIA;
        }
        return CONSUMER;
    }
}

package model.valori;

public enum TipoVolo {

    DIRETTO("diretto"),
    CON_SCALO("con scalo");

    private final String descrizione;

    TipoVolo(String descrizione) {
        this.descrizione = descrizione;
    }

    public static TipoVolo daCodice(String codice) {
        if (codice == null) {
            return null;
        }
        for (TipoVolo tipo : values()) {
            if (tipo.name().equalsIgnoreCase(codice.trim())) {
                return tipo;
            }
        }
        return null;
    }

    public String getDescrizione() {
        return descrizione;
    }
}

package model.valori;

public enum DurataViaggio {

    UNA_SETTIMANA(1),
    DUE_SETTIMANE(2);

    private static final int GIORNI_PER_SETTIMANA = 7;
    private static final long MILLISECONDI_GIORNO = 24L * 60 * 60 * 1000;

    private final int settimane;

    DurataViaggio(int settimane) {
        this.settimane = settimane;
    }

    public static DurataViaggio daSettimane(int settimane) {
        for (DurataViaggio durata : values()) {
            if (durata.settimane == settimane) {
                return durata;
            }
        }
        return null;
    }

    public static DurataViaggio daSettimaneOPredefinita(int settimane) {
        DurataViaggio durata = daSettimane(settimane);
        return durata != null ? durata : UNA_SETTIMANA;
    }

    public static long giorniInMillisecondi(int giorni) {
        return giorni * MILLISECONDI_GIORNO;
    }

    public int getSettimane() {
        return settimane;
    }

    public long getDurataInMillisecondi() {
        return giorniInMillisecondi(settimane * GIORNI_PER_SETTIMANA);
    }
}

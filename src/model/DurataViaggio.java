package model;

public enum DurataViaggio {

    UNA_SETTIMANA(1, 7, 1),
    DUE_SETTIMANE(2, 14, 2);

    private final int settimane;
    private final int giorni;
    private final int moltiplicatorePrezzo;

    DurataViaggio(int settimane, int giorni, int moltiplicatorePrezzo) {
        this.settimane = settimane;
        this.giorni = giorni;
        this.moltiplicatorePrezzo = moltiplicatorePrezzo;
    }

    public int getSettimane() {
        return settimane;
    }

    public static DurataViaggio daSettimane(int settimane) {
        for (DurataViaggio durata : values()) {
            if (durata.settimane == settimane) {
                return durata;
            }
        }
        return null;
    }

    public int getGiorni() {
        return giorni;
    }

    public int getMoltiplicatorePrezzo() {
        return moltiplicatorePrezzo;
    }
}

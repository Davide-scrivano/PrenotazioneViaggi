package model;

public class Pacchetto {

    private static final int POSTI_DEFAULT = 10;
    private static final int STELLE_HOTEL_DEFAULT = 3;
    private static final TipoVolo TIPO_VOLO_DEFAULT = TipoVolo.DIRETTO;
    private static final int STELLE_HOTEL_MINIMO = 1;
    private static final int STELLE_HOTEL_MASSIMO = 5;

    private int id;
    private String destinazione;
    private long dataPartenza;
    private long dataRientro;
    private float prezzo;
    private int postiDisponibili;
    private int stelleHotel;
    private TipoVolo tipoVolo;

    public Pacchetto(int id, String destinazione, long dataPartenza, long dataRientro, float prezzo) {
        this(id, destinazione, dataPartenza, dataRientro, prezzo, POSTI_DEFAULT);
    }

    public Pacchetto(int id, String destinazione, long dataPartenza, long dataRientro, float prezzo,
            int postiDisponibili) {
        this(id, destinazione, dataPartenza, dataRientro, prezzo, postiDisponibili,
                new DettagliOfferta(STELLE_HOTEL_DEFAULT, TIPO_VOLO_DEFAULT));
    }

    public Pacchetto(int id, String destinazione, long dataPartenza, long dataRientro, float prezzo,
            int postiDisponibili, DettagliOfferta dettagliOfferta) {
        this.id = id;
        this.destinazione = destinazione;
        this.dataPartenza = dataPartenza;
        this.dataRientro = dataRientro;
        this.prezzo = prezzo;
        this.postiDisponibili = postiDisponibili;
        this.stelleHotel = normalizzaStelle(dettagliOfferta.getStelleHotel());
        this.tipoVolo = dettagliOfferta.getTipoVolo();
    }

    public void aggiorna(String destinazione, long dataPartenza, long dataRientro, float prezzo,
            int postiDisponibili, DettagliOfferta dettagliOfferta) {
        this.destinazione = destinazione;
        this.dataPartenza = dataPartenza;
        this.dataRientro = dataRientro;
        this.prezzo = prezzo;
        this.postiDisponibili = postiDisponibili;
        this.stelleHotel = normalizzaStelle(dettagliOfferta.getStelleHotel());
        this.tipoVolo = dettagliOfferta.getTipoVolo();
    }

    private int normalizzaStelle(int stelleRichieste) {
        return Math.max(STELLE_HOTEL_MINIMO, Math.min(STELLE_HOTEL_MASSIMO, stelleRichieste));
    }

    public int getId() {
        return id;
    }

    public String getDestinazione() {
        return destinazione;
    }

    public long getDataPartenza() {
        return dataPartenza;
    }

    public long getDataRientro() {
        return dataRientro;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public int getStelleHotel() {
        return stelleHotel;
    }

    public TipoVolo getTipoVolo() {
        return tipoVolo;
    }

    public float calcolaPrezzoTotale(int numeroPartecipanti, DurataViaggio durata) {
        return prezzo * durata.getMoltiplicatorePrezzo() * numeroPartecipanti;
    }

    public boolean isDisponibile() {
        return isDisponibile(1);
    }

    public boolean isDisponibile(int postiRichiesti) {
        return postiRichiesti > 0 && postiDisponibili >= postiRichiesti;
    }

    public void occupaPosti(int quantita) {
        if (!isDisponibile(quantita)) {
            throw new IllegalStateException("Posti insufficienti sul pacchetto \"" + destinazione
                    + "\": richiesti " + quantita + ", disponibili " + postiDisponibili
                    + ". Va chiamato isDisponibile(int) prima di occupaPosti(int).");
        }
        postiDisponibili -= quantita;
    }

    public void liberaPosti(int quantita) {
        if (quantita > 0) {
            postiDisponibili += quantita;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pacchetto)) {
            return false;
        }
        Pacchetto pacchetto = (Pacchetto) o;
        return id == pacchetto.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
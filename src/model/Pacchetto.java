package model;

public class Pacchetto {

    private int id;
    private String destinazione;
    private long dataPartenza;
    private long dataRientro;
    private float prezzo;

    public Pacchetto(int id, String destinazione, long dataPartenza, long dataRientro, float prezzo) {
        this.id = id;
        this.destinazione = destinazione;
        this.dataPartenza = dataPartenza;
        this.dataRientro = dataRientro;
        this.prezzo = prezzo;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pacchetto)) return false;
        Pacchetto pacchetto = (Pacchetto) o;
        return id == pacchetto.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
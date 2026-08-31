package bean;

public class PacchettoVistaBean {

    private int id;
    private String destinazione;
    private long dataPartenza;
    private long dataRientro;
    private float prezzoSettimanale;
    private int postiDisponibili;
    private int stelleHotel;
    private String descrizioneVolo;
    private boolean esaurito;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDestinazione() {
        return destinazione;
    }

    public void setDestinazione(String destinazione) {
        this.destinazione = destinazione;
    }

    public long getDataPartenza() {
        return dataPartenza;
    }

    public void setDataPartenza(long dataPartenza) {
        this.dataPartenza = dataPartenza;
    }

    public long getDataRientro() {
        return dataRientro;
    }

    public void setDataRientro(long dataRientro) {
        this.dataRientro = dataRientro;
    }

    public float getPrezzoSettimanale() {
        return prezzoSettimanale;
    }

    public void setPrezzoSettimanale(float prezzoSettimanale) {
        this.prezzoSettimanale = prezzoSettimanale;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

    public int getStelleHotel() {
        return stelleHotel;
    }

    public void setStelleHotel(int stelleHotel) {
        this.stelleHotel = stelleHotel;
    }

    public String getDescrizioneVolo() {
        return descrizioneVolo;
    }

    public void setDescrizioneVolo(String descrizioneVolo) {
        this.descrizioneVolo = descrizioneVolo;
    }

    public boolean isEsaurito() {
        return esaurito;
    }

    public void setEsaurito(boolean esaurito) {
        this.esaurito = esaurito;
    }

    public boolean postiInsufficientiPer(int numeroPartecipanti) {
        return numeroPartecipanti > postiDisponibili;
    }
}

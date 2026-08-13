package bean;

public class PacchettoVistaBean {

    private int id;
    private String destinazione;
    private long dataPartenza;
    private long dataRientro;
    private float prezzoPerPersonaSettimana;
    private int postiDisponibili;
    private int stelleHotel;
    private String descrizioneVolo;
    private String codiceVolo;
    private boolean esaurito;
    private double votoMedio;
    private int numeroRecensioni;

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

    public float getPrezzoPerPersonaSettimana() {
        return prezzoPerPersonaSettimana;
    }

    public void setPrezzoPerPersonaSettimana(float prezzoPerPersonaSettimana) {
        this.prezzoPerPersonaSettimana = prezzoPerPersonaSettimana;
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

    public String getCodiceVolo() {
        return codiceVolo;
    }

    public void setCodiceVolo(String codiceVolo) {
        this.codiceVolo = codiceVolo;
    }

    public boolean isEsaurito() {
        return esaurito;
    }

    public void setEsaurito(boolean esaurito) {
        this.esaurito = esaurito;
    }

    public double getVotoMedio() {
        return votoMedio;
    }

    public void setVotoMedio(double votoMedio) {
        this.votoMedio = votoMedio;
    }

    public int getNumeroRecensioni() {
        return numeroRecensioni;
    }

    public void setNumeroRecensioni(int numeroRecensioni) {
        this.numeroRecensioni = numeroRecensioni;
    }
}

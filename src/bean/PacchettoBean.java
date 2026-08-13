package bean;

import model.TipoVolo;

public class PacchettoBean {

    private static final int STELLE_MINIME = 1;
    private static final int STELLE_MASSIME = 5;

    public static final String VOLO_DIRETTO = TipoVolo.DIRETTO.name();
    public static final String VOLO_CON_SCALO = TipoVolo.CON_SCALO.name();

    private String destinazione;
    private long dataPartenza;
    private long dataRientro;
    private float prezzo;
    private int posti;
    private int stelleHotel;
    private String tipoVolo;

    public String validaSintassi() {
        if (destinazione == null || destinazione.isBlank()) {
            return "Inserisci la destinazione.";
        }
        if (dataPartenza <= 0 || dataRientro <= 0) {
            return "Inserisci date valide.";
        }
        if (dataRientro < dataPartenza) {
            return "La data di rientro non puo' precedere quella di partenza.";
        }
        if (prezzo < 0 || posti < 0) {
            return "Prezzo e posti non possono essere negativi.";
        }
        if (stelleHotel < STELLE_MINIME || stelleHotel > STELLE_MASSIME) {
            return "Le stelle dell'hotel devono essere un numero da " + STELLE_MINIME + " a " + STELLE_MASSIME + ".";
        }
        if (TipoVolo.daCodice(tipoVolo) == null) {
            return "Seleziona il tipo di volo.";
        }
        return null;
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

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public int getPosti() {
        return posti;
    }

    public void setPosti(int posti) {
        this.posti = posti;
    }

    public int getStelleHotel() {
        return stelleHotel;
    }

    public void setStelleHotel(int stelleHotel) {
        this.stelleHotel = stelleHotel;
    }

    public String getTipoVolo() {
        return tipoVolo;
    }

    public void setTipoVolo(String tipoVolo) {
        this.tipoVolo = tipoVolo;
    }
}

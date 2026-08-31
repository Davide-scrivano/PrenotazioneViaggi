package model.valori;

public class DatiAnagrafici {

    private final long dataNascita;
    private final String codiceFiscale;

    public DatiAnagrafici(long dataNascita, String codiceFiscale) {
        this.dataNascita = dataNascita;
        this.codiceFiscale = codiceFiscale != null ? codiceFiscale : "";
    }

    public long getDataNascita() {
        return dataNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }
}

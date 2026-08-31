package bean;

public class PartecipanteVistaBean {

    private String nominativo;
    private long dataNascita;
    private String codiceFiscale;

    public String getNominativo() {
        return nominativo;
    }

    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    public long getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(long dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }
}

package bean;

public class PartecipanteVistaBean {

    private String nome;
    private String cognome;
    private long dataNascita;
    private String codiceFiscale;

    public boolean hasDataNascita() {
        return dataNascita > 0;
    }

    public boolean hasCodiceFiscale() {
        return codiceFiscale != null && !codiceFiscale.isBlank();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
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

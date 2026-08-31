package bean;

public class EsitoPacchettoBean {

    private boolean successo;
    private String messaggio;
    private PacchettoVistaBean pacchetto;

    public boolean isSuccesso() {
        return successo;
    }

    public void setSuccesso(boolean successo) {
        this.successo = successo;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public PacchettoVistaBean getPacchetto() {
        return pacchetto;
    }

    public void setPacchetto(PacchettoVistaBean pacchetto) {
        this.pacchetto = pacchetto;
    }
}

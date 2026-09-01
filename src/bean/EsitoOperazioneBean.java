package bean;

public class EsitoOperazioneBean {

    private boolean successo;
    private String messaggio;
    private boolean postiInsufficienti;

    public boolean isSuccesso() {
        return successo;
    }

    public void setSuccesso(boolean successo) {
        this.successo = successo;
    }

    public boolean isPostiInsufficienti() {
        return postiInsufficienti;
    }

    public void setPostiInsufficienti(boolean postiInsufficienti) {
        this.postiInsufficienti = postiInsufficienti;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}

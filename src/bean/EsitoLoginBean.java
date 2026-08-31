package bean;

public class EsitoLoginBean {

    private boolean successo;
    private String messaggio;
    private UtenteVistaBean utente;

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

    public UtenteVistaBean getUtente() {
        return utente;
    }

    public void setUtente(UtenteVistaBean utente) {
        this.utente = utente;
    }
}

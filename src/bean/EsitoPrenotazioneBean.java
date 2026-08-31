package bean;

public class EsitoPrenotazioneBean {

    private boolean successo;
    private String messaggio;
    private PrenotazioneVistaBean prenotazione;

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

    public PrenotazioneVistaBean getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(PrenotazioneVistaBean prenotazione) {
        this.prenotazione = prenotazione;
    }
}

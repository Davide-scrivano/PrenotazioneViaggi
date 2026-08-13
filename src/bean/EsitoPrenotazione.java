package bean;

public class EsitoPrenotazione {

    private final PrenotazioneVistaBean prenotazione;
    private final String messaggioErrore;

    private EsitoPrenotazione(PrenotazioneVistaBean prenotazione, String messaggioErrore) {
        this.prenotazione = prenotazione;
        this.messaggioErrore = messaggioErrore;
    }

    public static EsitoPrenotazione successo(PrenotazioneVistaBean prenotazione) {
        return new EsitoPrenotazione(prenotazione, null);
    }

    public static EsitoPrenotazione errore(String messaggioErrore) {
        return new EsitoPrenotazione(null, messaggioErrore);
    }

    public boolean isSuccesso() {
        return prenotazione != null;
    }

    public PrenotazioneVistaBean getPrenotazione() {
        return prenotazione;
    }

    public String getMessaggioErrore() {
        return messaggioErrore;
    }
}

package bean;

public class EsitoRegistrazione {

    private final UtenteVistaBean utente;
    private final String messaggioErrore;

    private EsitoRegistrazione(UtenteVistaBean utente, String messaggioErrore) {
        this.utente = utente;
        this.messaggioErrore = messaggioErrore;
    }

    public static EsitoRegistrazione successo(UtenteVistaBean utente) {
        return new EsitoRegistrazione(utente, null);
    }

    public static EsitoRegistrazione errore(String messaggioErrore) {
        return new EsitoRegistrazione(null, messaggioErrore);
    }

    public boolean isSuccesso() {
        return utente != null;
    }

    public UtenteVistaBean getUtente() {
        return utente;
    }

    public String getMessaggioErrore() {
        return messaggioErrore;
    }
}

package bean;

public class EsitoLogin {

    private final UtenteVistaBean utente;
    private final String messaggioErrore;

    private EsitoLogin(UtenteVistaBean utente, String messaggioErrore) {
        this.utente = utente;
        this.messaggioErrore = messaggioErrore;
    }

    public static EsitoLogin successo(UtenteVistaBean utente) {
        return new EsitoLogin(utente, null);
    }

    public static EsitoLogin errore(String messaggioErrore) {
        return new EsitoLogin(null, messaggioErrore);
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

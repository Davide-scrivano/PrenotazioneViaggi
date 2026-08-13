package bean;

public class EsitoOperazione {

    private final boolean successo;
    private final String messaggio;

    private EsitoOperazione(boolean successo, String messaggio) {
        this.successo = successo;
        this.messaggio = messaggio;
    }

    public static EsitoOperazione successo(String messaggio) {
        return new EsitoOperazione(true, messaggio);
    }

    public static EsitoOperazione errore(String messaggio) {
        return new EsitoOperazione(false, messaggio);
    }

    public boolean isSuccesso() {
        return successo;
    }

    public String getMessaggio() {
        return messaggio;
    }
}

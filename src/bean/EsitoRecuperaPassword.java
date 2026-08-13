package bean;

public class EsitoRecuperaPassword {

    private final String password;
    private final String messaggioErrore;

    private EsitoRecuperaPassword(String password, String messaggioErrore) {
        this.password = password;
        this.messaggioErrore = messaggioErrore;
    }

    public static EsitoRecuperaPassword successo(String password) {
        return new EsitoRecuperaPassword(password, null);
    }

    public static EsitoRecuperaPassword errore(String messaggioErrore) {
        return new EsitoRecuperaPassword(null, messaggioErrore);
    }

    public boolean isSuccesso() {
        return password != null;
    }

    public String getPassword() {
        return password;
    }

    public String getMessaggioErrore() {
        return messaggioErrore;
    }
}

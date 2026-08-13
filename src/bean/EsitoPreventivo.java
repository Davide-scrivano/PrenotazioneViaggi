package bean;

public class EsitoPreventivo {

    private final Float importoTotale;
    private final String messaggioErrore;

    private EsitoPreventivo(Float importoTotale, String messaggioErrore) {
        this.importoTotale = importoTotale;
        this.messaggioErrore = messaggioErrore;
    }

    public static EsitoPreventivo successo(float importoTotale) {
        return new EsitoPreventivo(importoTotale, null);
    }

    public static EsitoPreventivo errore(String messaggioErrore) {
        return new EsitoPreventivo(null, messaggioErrore);
    }

    public boolean isSuccesso() {
        return importoTotale != null;
    }

    public float getImportoTotale() {
        return importoTotale != null ? importoTotale : 0f;
    }

    public String getMessaggioErrore() {
        return messaggioErrore;
    }
}

package bean;

public class EsitoPreventivoBean {

    private boolean successo;
    private String messaggio;
    private float importoTotale;

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

    public float getImportoTotale() {
        return importoTotale;
    }

    public void setImportoTotale(float importoTotale) {
        this.importoTotale = importoTotale;
    }
}

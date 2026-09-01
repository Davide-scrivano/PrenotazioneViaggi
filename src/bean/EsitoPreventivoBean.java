package bean;

public class EsitoPreventivoBean {

    private boolean successo;
    private String messaggio;
    private float importoTotale;
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

    public float getImportoTotale() {
        return importoTotale;
    }

    public void setImportoTotale(float importoTotale) {
        this.importoTotale = importoTotale;
    }
}

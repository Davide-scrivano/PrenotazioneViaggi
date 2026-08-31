package bean;

public class EsitoCatalogoBean {

    private boolean successo;
    private String messaggio;
    private CatalogoVistaBean catalogo;

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

    public CatalogoVistaBean getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(CatalogoVistaBean catalogo) {
        this.catalogo = catalogo;
    }
}

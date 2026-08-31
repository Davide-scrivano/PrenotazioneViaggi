package model;

public class Pagamento {

    private final int id;
    private final String metodo;
    private final float importo;
    private final String codiceAutorizzazione;
    private final long dataEsecuzione;

    public Pagamento(int id, String metodo, float importo, String codiceAutorizzazione) {
        this(id, metodo, importo, codiceAutorizzazione, System.currentTimeMillis());
    }

    public Pagamento(int id, String metodo, float importo, String codiceAutorizzazione, long dataEsecuzione) {
        this.id = id;
        this.metodo = metodo;
        this.importo = importo;
        this.codiceAutorizzazione = codiceAutorizzazione;
        this.dataEsecuzione = dataEsecuzione;
    }

    public String descrizione() {
        return metodo + " (" + importo + " euro)";
    }

    public int getId() {
        return id;
    }

    public String getMetodo() {
        return metodo;
    }

    public float getImporto() {
        return importo;
    }

    public String getCodiceAutorizzazione() {
        return codiceAutorizzazione;
    }

    public long getDataEsecuzione() {
        return dataEsecuzione;
    }
}

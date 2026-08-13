package bean;

public class ListaAttesaBean {

    private int idPacchetto;
    private int numeroPosti;

    public String validaSintassi() {
        if (numeroPosti <= 0) {
            return "Inserisci un numero di posti valido.";
        }
        return null;
    }

    public int getIdPacchetto() {
        return idPacchetto;
    }

    public void setIdPacchetto(int idPacchetto) {
        this.idPacchetto = idPacchetto;
    }

    public int getNumeroPosti() {
        return numeroPosti;
    }

    public void setNumeroPosti(int numeroPosti) {
        this.numeroPosti = numeroPosti;
    }
}

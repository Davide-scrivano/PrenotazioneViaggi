package bean;

public class RecensioneBean {

    private int idPacchetto;
    private int voto;
    private String commento;

    public String validaSintassi() {
        if (voto < 1 || voto > 5) {
            return "Il voto deve essere un numero da 1 a 5.";
        }
        return null;
    }

    public int getIdPacchetto() {
        return idPacchetto;
    }

    public void setIdPacchetto(int idPacchetto) {
        this.idPacchetto = idPacchetto;
    }

    public int getVoto() {
        return voto;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }
}

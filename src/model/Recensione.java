package model;

public class Recensione {

    private static final int VOTO_MINIMO = 1;
    private static final int VOTO_MASSIMO = 5;

    private int id;
    private Utente utente;
    private Pacchetto pacchetto;
    private int voto;
    private String commento;
    private long dataRecensione;

    public Recensione(int id, Utente utente, Pacchetto pacchetto, int voto, String commento) {
        this.id = id;
        this.utente = utente;
        this.pacchetto = pacchetto;
        this.voto = normalizzaVoto(voto);
        this.commento = commento;
        this.dataRecensione = System.currentTimeMillis();
    }

    private int normalizzaVoto(int votoRichiesto) {
        return Math.max(VOTO_MINIMO, Math.min(VOTO_MASSIMO, votoRichiesto));
    }

    public int getId() {
        return id;
    }

    public Utente getAutore() {
        return utente;
    }

    public Pacchetto getPacchetto() {
        return pacchetto;
    }

    public int getVoto() {
        return voto;
    }

    public String getCommento() {
        return commento;
    }

    public long getDataRecensione() {
        return dataRecensione;
    }
}

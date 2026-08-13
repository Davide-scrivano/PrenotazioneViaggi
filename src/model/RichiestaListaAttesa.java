package model;

public class RichiestaListaAttesa {

    private final int id;
    private final Utente utente;
    private final Pacchetto pacchetto;
    private final int numeroPosti;
    private boolean notificata;

    public RichiestaListaAttesa(int id, Utente utente, Pacchetto pacchetto, int numeroPosti) {
        this.id = id;
        this.utente = utente;
        this.pacchetto = pacchetto;
        this.numeroPosti = numeroPosti;
        this.notificata = false;
    }

    public int getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }

    public Pacchetto getPacchetto() {
        return pacchetto;
    }

    public int getNumeroPosti() {
        return numeroPosti;
    }

    public boolean isNotificata() {
        return notificata;
    }

    public void segnaNotificata() {
        this.notificata = true;
    }
}

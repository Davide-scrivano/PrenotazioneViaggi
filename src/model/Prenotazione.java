package model;

import payment.Pagamento;

public class Prenotazione {

    private int id;
    private Utente utente;
    private Pacchetto pacchetto;
    private Pagamento pagamento;
    private long dataViaggio;
    private long dataPrenotazione;
    private StatoPrenotazione stato;

    public Prenotazione(int id, Utente utente, Pacchetto pacchetto, Pagamento pagamento, long dataViaggio) {
        this.id = id;
        this.utente = utente;
        this.pacchetto = pacchetto;
        this.pagamento = pagamento;
        this.dataViaggio = dataViaggio;
        this.dataPrenotazione = System.currentTimeMillis();
        this.stato = StatoPrenotazione.CONFERMATA;
    }

    public int getId() {
        return id;
    }

    public Utente getDettagliUtente() {
        return utente;
    }

    public Pacchetto getDettagliPacchetto() {
        return pacchetto;
    }

    public Pagamento getDettagliPagamento() {
        return pagamento;
    }

    public long getDateViaggio() {
        return dataViaggio;
    }

    public long getDataPrenotazione() {
        return dataPrenotazione;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }

    public void annulla() {
        this.stato = StatoPrenotazione.ANNULLATA;
    }

    public void modificaPacchetto(Pacchetto nuovoPacchetto) {
        this.pacchetto = nuovoPacchetto;
    }
}
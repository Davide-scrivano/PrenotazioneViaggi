package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import payment.Pagamento;

public class Prenotazione {

    private static final int GIORNI_PREAVVISO_MINIMO = 10;
    private static final long MILLISECONDI_GIORNO = 24L * 60 * 60 * 1000;

    private int id;
    private Utente utente;
    private Pacchetto pacchetto;
    private Pagamento pagamento;
    private long dataPartenzaViaggio;
    private long dataRientroViaggio;
    private long dataPrenotazione;
    private StatoPrenotazione stato;
    private List<Utente> partecipanti;

    public Prenotazione(int id, Utente utente, Pacchetto pacchetto, Pagamento pagamento, long dataPartenzaViaggio,
            long dataRientroViaggio, List<Utente> partecipanti) {
        this.id = id;
        this.utente = utente;
        this.pacchetto = pacchetto;
        this.pagamento = pagamento;
        this.dataPartenzaViaggio = dataPartenzaViaggio;
        this.dataRientroViaggio = dataRientroViaggio;
        this.dataPrenotazione = System.currentTimeMillis();
        this.stato = StatoPrenotazione.CONFERMATA;
        this.partecipanti = new ArrayList<>(partecipanti);
    }

    // Usato dal DAO per ricostruire una prenotazione gia' esistente: a
    // differenza del costruttore principale, data e stato non vanno
    // ricalcolati ma riletti cosi' come erano stati salvati.
    public Prenotazione(int id, Utente utente, Pacchetto pacchetto, Pagamento pagamento,
            DettagliRicostruzionePrenotazione dettagli, List<Utente> partecipanti) {
        this(id, utente, pacchetto, pagamento, dettagli.getDataPartenzaViaggio(), dettagli.getDataRientroViaggio(),
                partecipanti);
        this.dataPrenotazione = dettagli.getDataPrenotazione();
        this.stato = dettagli.getStato();
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

    public long getDataPartenzaViaggio() {
        return dataPartenzaViaggio;
    }

    public long getDataRientroViaggio() {
        return dataRientroViaggio;
    }

    public long getDataPrenotazione() {
        return dataPrenotazione;
    }

    public StatoPrenotazione getStato() {
        return stato;
    }

    public boolean isAnnullata() {
        return stato == StatoPrenotazione.ANNULLATA;
    }

    public boolean isModificabile() {
        if (isAnnullata()) {
            return false;
        }
        long limite = dataPartenzaViaggio - (GIORNI_PREAVVISO_MINIMO * MILLISECONDI_GIORNO);
        return System.currentTimeMillis() <= limite;
    }

    public static int getGiorniPreavvisoMinimo() {
        return GIORNI_PREAVVISO_MINIMO;
    }

    public List<Utente> getDettagliPartecipanti() {
        return Collections.unmodifiableList(partecipanti);
    }

    public int getNumeroPartecipanti() {
        return partecipanti.size();
    }

    public void annulla() {
        this.stato = StatoPrenotazione.ANNULLATA;
    }

    public void modificaPacchetto(Pacchetto nuovoPacchetto) {
        this.pacchetto = nuovoPacchetto;
    }
}
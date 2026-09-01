package model;

import model.valori.PeriodoViaggio;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prenotazione {

    private final int id;
    private final Utente cliente;
    private final Pacchetto pacchetto;
    private final Pagamento pagamento;
    private final PeriodoViaggio periodo;
    private final List<Partecipante> partecipanti;
    private final long dataPrenotazione;

    static Prenotazione nuova(int id, Utente cliente, Pacchetto pacchetto, Pagamento pagamento,
            PeriodoViaggio periodo, List<Partecipante> partecipanti) {
        return new Prenotazione(id, cliente, pacchetto, pagamento, periodo, partecipanti,
                System.currentTimeMillis());
    }

    private Prenotazione(int id, Utente cliente, Pacchetto pacchetto, Pagamento pagamento, PeriodoViaggio periodo,
            List<Partecipante> partecipanti, long dataPrenotazione) {
        this.id = id;
        this.cliente = cliente;
        this.pacchetto = pacchetto;
        this.pagamento = pagamento;
        this.periodo = periodo;
        this.partecipanti = new ArrayList<>(partecipanti);
        this.dataPrenotazione = dataPrenotazione;
    }

    public static Prenotazione ricostruisci(int id, Utente cliente, Pacchetto pacchetto, Pagamento pagamento,
            PeriodoViaggio periodo, List<Partecipante> partecipanti, long dataPrenotazione) {
        return new Prenotazione(id, cliente, pacchetto, pagamento, periodo, partecipanti, dataPrenotazione);
    }

    public int getNumeroPartecipanti() {
        return partecipanti.size();
    }

    public String getDestinazione() {
        return pacchetto.getDestinazione();
    }

    public String getEmailCliente() {
        return cliente.getEmail();
    }

    public String getNominativoCliente() {
        return cliente.nominativo();
    }

    public float getImportoTotale() {
        return pagamento.getImporto();
    }

    public String getDescrizionePagamento() {
        return pagamento.descrizione();
    }

    public int getId() {
        return id;
    }

    public Utente getCliente() {
        return cliente;
    }

    public int getIdCliente() {
        return cliente.getId();
    }

    public Pacchetto getPacchetto() {
        return pacchetto;
    }

    public int getIdPacchetto() {
        return pacchetto.getId();
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public int getIdPagamento() {
        return pagamento.getId();
    }

    public PeriodoViaggio getPeriodo() {
        return periodo;
    }

    public long getDataPartenzaViaggio() {
        return periodo.getDataPartenza();
    }

    public long getDataRientroViaggio() {
        return periodo.getDataRientro();
    }

    public long getDataPrenotazione() {
        return dataPrenotazione;
    }

    public List<Partecipante> getPartecipanti() {
        return Collections.unmodifiableList(partecipanti);
    }
}

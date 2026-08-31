package bean;

import java.util.ArrayList;
import java.util.List;

import model.valori.DurataViaggio;

public class PrenotazioneBean {

    private int idUtente;
    private int idPacchetto;
    private long dataPartenzaViaggio;
    private int settimaneSoggiorno;
    private List<PartecipanteBean> partecipanti = new ArrayList<>();
    private DatiPagamentoBean datiPagamento = new DatiPagamentoBean();

    public String validaSintassi() {
        String errore = validaSintassiPartecipanti();
        return errore != null ? errore : datiPagamento.validaSintassi();
    }

    public String validaSintassiPartecipanti() {
        String errore = validaSintassiViaggio();
        if (errore != null) {
            return errore;
        }
        for (int posizione = 0; posizione < partecipanti.size(); posizione++) {
            errore = partecipanti.get(posizione).validaSintassi(posizione + 1);
            if (errore != null) {
                return errore;
            }
        }
        return null;
    }

    public String validaSintassiViaggio() {
        if (dataPartenzaViaggio <= 0) {
            return "Seleziona la data di partenza.";
        }
        if (DurataViaggio.daSettimane(settimaneSoggiorno) == null) {
            return "Seleziona la durata del soggiorno: 1 o 2 settimane.";
        }
        if (partecipanti.isEmpty()) {
            return "Inserisci i dati di almeno un partecipante.";
        }
        return null;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public int getIdPacchetto() {
        return idPacchetto;
    }

    public void setIdPacchetto(int idPacchetto) {
        this.idPacchetto = idPacchetto;
    }

    public long getDataPartenzaViaggio() {
        return dataPartenzaViaggio;
    }

    public void setDataPartenzaViaggio(long dataPartenzaViaggio) {
        this.dataPartenzaViaggio = dataPartenzaViaggio;
    }

    public int getSettimaneSoggiorno() {
        return settimaneSoggiorno;
    }

    public void setSettimaneSoggiorno(int settimaneSoggiorno) {
        this.settimaneSoggiorno = settimaneSoggiorno;
    }

    public List<PartecipanteBean> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(List<PartecipanteBean> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public DatiPagamentoBean getDatiPagamento() {
        return datiPagamento;
    }

    public void setDatiPagamento(DatiPagamentoBean datiPagamento) {
        this.datiPagamento = datiPagamento;
    }
}

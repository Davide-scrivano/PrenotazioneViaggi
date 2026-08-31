package bean;

import java.util.ArrayList;
import java.util.List;

public class PrenotazioneVistaBean {

    private int id;
    private String destinazione;
    private long dataPartenzaViaggio;
    private long dataRientroViaggio;
    private long dataPrenotazione;
    private String descrizionePagamento;
    private float importoTotale;
    private List<PartecipanteVistaBean> partecipanti = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDestinazione() {
        return destinazione;
    }

    public void setDestinazione(String destinazione) {
        this.destinazione = destinazione;
    }

    public long getDataPartenzaViaggio() {
        return dataPartenzaViaggio;
    }

    public void setDataPartenzaViaggio(long dataPartenzaViaggio) {
        this.dataPartenzaViaggio = dataPartenzaViaggio;
    }

    public long getDataRientroViaggio() {
        return dataRientroViaggio;
    }

    public void setDataRientroViaggio(long dataRientroViaggio) {
        this.dataRientroViaggio = dataRientroViaggio;
    }

    public long getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(long dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione;
    }

    public String getDescrizionePagamento() {
        return descrizionePagamento;
    }

    public void setDescrizionePagamento(String descrizionePagamento) {
        this.descrizionePagamento = descrizionePagamento;
    }

    public float getImportoTotale() {
        return importoTotale;
    }

    public void setImportoTotale(float importoTotale) {
        this.importoTotale = importoTotale;
    }

    public List<PartecipanteVistaBean> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(List<PartecipanteVistaBean> partecipanti) {
        this.partecipanti = partecipanti;
    }
}

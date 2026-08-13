package bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrenotazioneVistaBean {

    private int id;
    private int idPacchetto;
    private String destinazione;
    private long dataPartenzaViaggio;
    private long dataRientroViaggio;
    private long dataPrenotazione;
    private String stato;
    private boolean modificabile;
    private String descrizionePagamento;
    private float importoPagato;
    private List<PartecipanteVistaBean> partecipanti = new ArrayList<>();

    private String nomeCliente;
    private String cognomeCliente;
    private String emailCliente;

    public int getNumeroPartecipanti() {
        return partecipanti.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPacchetto() {
        return idPacchetto;
    }

    public void setIdPacchetto(int idPacchetto) {
        this.idPacchetto = idPacchetto;
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

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public boolean isModificabile() {
        return modificabile;
    }

    public void setModificabile(boolean modificabile) {
        this.modificabile = modificabile;
    }

    public String getDescrizionePagamento() {
        return descrizionePagamento;
    }

    public void setDescrizionePagamento(String descrizionePagamento) {
        this.descrizionePagamento = descrizionePagamento;
    }

    public float getImportoPagato() {
        return importoPagato;
    }

    public void setImportoPagato(float importoPagato) {
        this.importoPagato = importoPagato;
    }

    public List<PartecipanteVistaBean> getPartecipanti() {
        return Collections.unmodifiableList(partecipanti);
    }

    public void setPartecipanti(List<PartecipanteVistaBean> partecipanti) {
        this.partecipanti = new ArrayList<>(partecipanti);
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCognomeCliente() {
        return cognomeCliente;
    }

    public void setCognomeCliente(String cognomeCliente) {
        this.cognomeCliente = cognomeCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }
}

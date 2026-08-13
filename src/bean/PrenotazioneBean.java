package bean;

import java.util.ArrayList;
import java.util.List;

import model.DurataViaggio;
import payment.MetodoPagamento;

public class PrenotazioneBean {

    public static final String PAGAMENTO_CARTA = MetodoPagamento.CARTA_DI_CREDITO.name();
    public static final String PAGAMENTO_PAYPAL = MetodoPagamento.PAYPAL.name();

    private int idPacchetto;
    private long dataPartenzaViaggio;
    private int settimaneSoggiorno;
    private List<PartecipanteBean> partecipanti = new ArrayList<>();

    private String metodoPagamento;
    private String numeroCarta;
    private String titolare;
    private String scadenza;
    private String cvv;
    private String emailPaypal;
    private String passwordPaypal;

    public String validaSintassi() {
        String erroreViaggio = validaSintassiPartecipanti();
        if (erroreViaggio != null) {
            return erroreViaggio;
        }
        return validaSintassiPagamento();
    }

    /**
     * Valida i dati del viaggio e dei partecipanti, senza richiedere quelli
     * di pagamento: serve per bloccare l'utente sulla pagina dei
     * partecipanti prima che proceda a quella di pagamento.
     */
    public String validaSintassiPartecipanti() {
        String erroreViaggio = validaSintassiPreventivo();
        if (erroreViaggio != null) {
            return erroreViaggio;
        }
        for (int i = 0; i < partecipanti.size(); i++) {
            String errore = partecipanti.get(i).validaSintassi(i + 1);
            if (errore != null) {
                return errore;
            }
        }
        return null;
    }

    public String validaSintassiPreventivo() {
        if (dataPartenzaViaggio <= 0) {
            return "Seleziona la data di partenza.";
        }
        if (DurataViaggio.daSettimane(settimaneSoggiorno) == null) {
            return "Seleziona la durata del soggiorno.";
        }
        if (partecipanti.isEmpty()) {
            return "Inserisci i dati di almeno un partecipante.";
        }
        return null;
    }

    private String validaSintassiPagamento() {
        if (MetodoPagamento.daCodice(metodoPagamento) == null) {
            return "Seleziona un metodo di pagamento.";
        }
        if (PAGAMENTO_PAYPAL.equals(metodoPagamento)) {
            return validaSintassiPaypal();
        }
        return validaSintassiCarta();
    }

    private String validaSintassiPaypal() {
        String errore = obbligatorio(emailPaypal, "Inserisci l'email PayPal.");
        return errore != null ? errore : obbligatorio(passwordPaypal, "Inserisci la password PayPal.");
    }

    private String validaSintassiCarta() {
        String errore = obbligatorio(numeroCarta, "Inserisci il numero della carta.");
        if (errore == null) {
            errore = obbligatorio(titolare, "Inserisci il titolare della carta.");
        }
        if (errore == null) {
            errore = obbligatorio(scadenza, "Inserisci la scadenza della carta.");
        }
        if (errore == null) {
            errore = obbligatorio(cvv, "Inserisci il CVV.");
        }
        return errore;
    }

    private static String obbligatorio(String valore, String messaggio) {
        return valore == null || valore.isBlank() ? messaggio : null;
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

    public void aggiungiPartecipante(PartecipanteBean partecipante) {
        this.partecipanti.add(partecipante);
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }

    public String getTitolare() {
        return titolare;
    }

    public void setTitolare(String titolare) {
        this.titolare = titolare;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getEmailPaypal() {
        return emailPaypal;
    }

    public void setEmailPaypal(String emailPaypal) {
        this.emailPaypal = emailPaypal;
    }

    public String getPasswordPaypal() {
        return passwordPaypal;
    }

    public void setPasswordPaypal(String passwordPaypal) {
        this.passwordPaypal = passwordPaypal;
    }
}

package control;

import java.time.LocalDate;
import java.time.ZoneId;

import bean.DatiPagamentoBean;
import bean.PartecipanteBean;
import bean.PrenotazioneBean;
import model.valori.DurataViaggio;
import payment.MetodoPagamento;

public final class DatiDiProva {

    public static final int ID_UTENTE_MARIO = 1;
    public static final int ID_PACCHETTO_ROMA = 1;

    private DatiDiProva() {
    }

    public static long fraGiorni(int giorni) {
        long oggi = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
        return oggi + DurataViaggio.giorniInMillisecondi(giorni);
    }

    public static PrenotazioneBean prenotazioneValida(int numeroPartecipanti) {
        PrenotazioneBean dati = new PrenotazioneBean();
        dati.setIdUtente(ID_UTENTE_MARIO);
        dati.setIdPacchetto(ID_PACCHETTO_ROMA);
        dati.setDataPartenzaViaggio(fraGiorni(11));
        dati.setSettimaneSoggiorno(1);
        for (int posizione = 1; posizione <= numeroPartecipanti; posizione++) {
            dati.getPartecipanti().add(partecipante("Nome" + posizione, "Cognome" + posizione));
        }
        dati.setDatiPagamento(cartaValida());
        return dati;
    }

    public static PartecipanteBean partecipante(String nome, String cognome) {
        PartecipanteBean partecipante = new PartecipanteBean();
        partecipante.setNome(nome);
        partecipante.setCognome(cognome);
        partecipante.setDataNascita("01/01/1990");
        partecipante.setCodiceFiscale("");
        return partecipante;
    }

    public static DatiPagamentoBean cartaValida() {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        dati.setMetodoPagamento(MetodoPagamento.CARTA_DI_CREDITO.name());
        dati.setNumeroCarta("4111111111111111");
        dati.setTitolare("Mario Rossi");
        dati.setScadenza("12/30");
        dati.setCvv("123");
        return dati;
    }

    public static DatiPagamentoBean cartaRifiutataDalCircuito() {
        DatiPagamentoBean dati = cartaValida();
        dati.setNumeroCarta("4111111111110000");
        return dati;
    }
}

package controller.grafico;

import java.util.ArrayList;
import java.util.List;

import bean.PacchettoVistaBean;
import bean.PartecipanteVistaBean;
import bean.PrenotazioneVistaBean;
import bean.RecensioneVistaBean;
import bean.UtenteVistaBean;
import control.GestoreRecensioni;
import model.Pacchetto;
import model.Prenotazione;
import model.Recensione;
import model.Utente;

final class CostruttoreBeanVista {

    private CostruttoreBeanVista() {
    }

    static UtenteVistaBean daUtente(Utente utente) {
        UtenteVistaBean bean = new UtenteVistaBean();
        bean.setId(utente.getId());
        bean.setNickname(utente.getNickname());
        bean.setNome(utente.getName());
        bean.setCognome(utente.getSurname());
        bean.setEmail(utente.getEmail());
        bean.setAgenzia(utente.isAgenzia());
        return bean;
    }

    static PacchettoVistaBean daPacchetto(Pacchetto pacchetto, GestoreRecensioni gestoreRecensioni) {
        PacchettoVistaBean bean = new PacchettoVistaBean();
        bean.setId(pacchetto.getId());
        bean.setDestinazione(pacchetto.getDestinazione());
        bean.setDataPartenza(pacchetto.getDataPartenza());
        bean.setDataRientro(pacchetto.getDataRientro());
        bean.setPrezzoPerPersonaSettimana(pacchetto.getPrezzo());
        bean.setPostiDisponibili(pacchetto.getPostiDisponibili());
        bean.setStelleHotel(pacchetto.getStelleHotel());
        bean.setDescrizioneVolo(pacchetto.getTipoVolo().getDescrizione());
        bean.setCodiceVolo(pacchetto.getTipoVolo().name());
        bean.setEsaurito(!pacchetto.isDisponibile());
        bean.setVotoMedio(gestoreRecensioni.getMediaVoti(pacchetto.getId()));
        bean.setNumeroRecensioni(gestoreRecensioni.getRecensioniPacchetto(pacchetto.getId()).size());
        return bean;
    }

    static List<PacchettoVistaBean> daPacchetti(List<Pacchetto> pacchetti, GestoreRecensioni gestoreRecensioni) {
        List<PacchettoVistaBean> risultato = new ArrayList<>();
        for (Pacchetto pacchetto : pacchetti) {
            risultato.add(daPacchetto(pacchetto, gestoreRecensioni));
        }
        return risultato;
    }

    static RecensioneVistaBean daRecensione(Recensione recensione) {
        RecensioneVistaBean bean = new RecensioneVistaBean();
        bean.setNomeAutore(recensione.getAutore().getName());
        bean.setVoto(recensione.getVoto());
        bean.setCommento(recensione.getCommento());
        return bean;
    }

    static List<RecensioneVistaBean> daRecensioni(List<Recensione> recensioni) {
        List<RecensioneVistaBean> risultato = new ArrayList<>();
        for (Recensione recensione : recensioni) {
            risultato.add(daRecensione(recensione));
        }
        return risultato;
    }

    static PartecipanteVistaBean daPartecipante(Utente partecipante) {
        PartecipanteVistaBean bean = new PartecipanteVistaBean();
        bean.setNome(partecipante.getName());
        bean.setCognome(partecipante.getSurname());
        bean.setDataNascita(partecipante.getDataNascita());
        bean.setCodiceFiscale(partecipante.getCodiceFiscale());
        return bean;
    }

    static PrenotazioneVistaBean daPrenotazione(Prenotazione prenotazione, boolean conDatiCliente) {
        PrenotazioneVistaBean bean = new PrenotazioneVistaBean();
        bean.setId(prenotazione.getId());
        bean.setIdPacchetto(prenotazione.getDettagliPacchetto().getId());
        bean.setDestinazione(prenotazione.getDettagliPacchetto().getDestinazione());
        bean.setDataPartenzaViaggio(prenotazione.getDataPartenzaViaggio());
        bean.setDataRientroViaggio(prenotazione.getDataRientroViaggio());
        bean.setDataPrenotazione(prenotazione.getDataPrenotazione());
        bean.setStato(prenotazione.getStato().name());
        bean.setModificabile(prenotazione.isModificabile());
        bean.setDescrizionePagamento(prenotazione.getDettagliPagamento().descrizione());
        bean.setImportoPagato(prenotazione.getDettagliPagamento().costo());

        List<PartecipanteVistaBean> partecipanti = new ArrayList<>();
        for (Utente partecipante : prenotazione.getDettagliPartecipanti()) {
            partecipanti.add(daPartecipante(partecipante));
        }
        bean.setPartecipanti(partecipanti);

        if (conDatiCliente) {
            Utente cliente = prenotazione.getDettagliUtente();
            bean.setNomeCliente(cliente.getName());
            bean.setCognomeCliente(cliente.getSurname());
            bean.setEmailCliente(cliente.getEmail());
        }
        return bean;
    }

    static List<PrenotazioneVistaBean> daPrenotazioni(List<Prenotazione> prenotazioni, boolean conDatiCliente) {
        List<PrenotazioneVistaBean> risultato = new ArrayList<>();
        for (Prenotazione prenotazione : prenotazioni) {
            risultato.add(daPrenotazione(prenotazione, conDatiCliente));
        }
        return risultato;
    }
}

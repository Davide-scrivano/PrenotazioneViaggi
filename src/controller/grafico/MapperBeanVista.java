package controller.grafico;

import java.util.ArrayList;
import java.util.List;

import bean.CatalogoVistaBean;
import bean.PacchettoVistaBean;
import bean.PartecipanteVistaBean;
import bean.PrenotazioneVistaBean;
import bean.UtenteVistaBean;
import model.Catalogo;
import model.Pacchetto;
import model.Partecipante;
import model.Prenotazione;
import model.Utente;

final class MapperBeanVista {

    private MapperBeanVista() {
    }

    static UtenteVistaBean daUtente(Utente utente) {
        UtenteVistaBean bean = new UtenteVistaBean();
        bean.setId(utente.getId());
        bean.setNickname(utente.getNickname());
        bean.setNome(utente.getNome());
        bean.setCognome(utente.getCognome());
        bean.setEmail(utente.getEmail());
        bean.setAgenzia(utente.isAgenzia());
        return bean;
    }

    static PacchettoVistaBean daPacchetto(Pacchetto pacchetto) {
        PacchettoVistaBean bean = new PacchettoVistaBean();
        bean.setId(pacchetto.getId());
        bean.setDestinazione(pacchetto.getDestinazione());
        bean.setDataPartenza(pacchetto.getDataPartenzaDisponibilita());
        bean.setDataRientro(pacchetto.getDataRientroDisponibilita());
        bean.setEsaurito(pacchetto.isEsaurito());
        bean.setPrezzoSettimanale(pacchetto.getPrezzoSettimanale());
        bean.setPostiDisponibili(pacchetto.getPostiDisponibili());
        bean.setStelleHotel(pacchetto.getStelleHotel());
        bean.setDescrizioneVolo(pacchetto.getTipoVolo().getDescrizione());
        return bean;
    }

    static CatalogoVistaBean daCatalogo(Catalogo catalogo, String destinazioneCercata) {
        CatalogoVistaBean bean = new CatalogoVistaBean();
        bean.setTitolo(catalogo.getTitolo());
        bean.setPacchetti(daPacchetti(catalogo.cercaPerDestinazione(destinazioneCercata)));
        return bean;
    }

    static List<PacchettoVistaBean> daPacchetti(List<Pacchetto> pacchetti) {
        List<PacchettoVistaBean> risultato = new ArrayList<>();
        for (Pacchetto pacchetto : pacchetti) {
            risultato.add(daPacchetto(pacchetto));
        }
        return risultato;
    }

    static PartecipanteVistaBean daPartecipante(Partecipante partecipante) {
        PartecipanteVistaBean bean = new PartecipanteVistaBean();
        bean.setNominativo(partecipante.nominativo());
        bean.setDataNascita(partecipante.haDataNascita() ? partecipante.getDataNascita() : 0L);
        bean.setCodiceFiscale(partecipante.haCodiceFiscale() ? partecipante.getCodiceFiscale() : "");
        return bean;
    }

    static PrenotazioneVistaBean daPrenotazione(Prenotazione prenotazione) {
        PrenotazioneVistaBean bean = new PrenotazioneVistaBean();
        bean.setId(prenotazione.getId());
        bean.setDestinazione(prenotazione.getDestinazione());
        bean.setDataPartenzaViaggio(prenotazione.getDataPartenzaViaggio());
        bean.setDataRientroViaggio(prenotazione.getDataRientroViaggio());
        bean.setDataPrenotazione(prenotazione.getDataPrenotazione());
        bean.setDescrizionePagamento(prenotazione.getDescrizionePagamento());
        bean.setImportoTotale(prenotazione.getImportoTotale());

        List<PartecipanteVistaBean> partecipanti = new ArrayList<>();
        for (Partecipante partecipante : prenotazione.getPartecipanti()) {
            partecipanti.add(daPartecipante(partecipante));
        }
        bean.setPartecipanti(partecipanti);
        return bean;
    }
}

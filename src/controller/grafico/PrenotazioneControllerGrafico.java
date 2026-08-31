package controller.grafico;

import bean.EsitoCatalogoBean;
import bean.EsitoOperazioneBean;
import bean.EsitoPacchettoBean;
import bean.EsitoPrenotazioneBean;
import bean.EsitoPreventivoBean;
import bean.PrenotazioneBean;
import control.GestoreCatalogo;
import control.GestorePrenotazioni;
import dao.DAOFactory;
import exceptions.ApplicazioneException;
import model.Catalogo;
import model.Pacchetto;
import model.Prenotazione;
import notifica.NotificatorePrenotazioni;
import payment.FacadePagamento;

public class PrenotazioneControllerGrafico {

    private final DAOFactory daoFactory;
    private final FacadePagamento facadePagamento;
    private final NotificatorePrenotazioni notificatore;

    public PrenotazioneControllerGrafico(DAOFactory daoFactory, FacadePagamento facadePagamento,
            NotificatorePrenotazioni notificatore) {
        this.daoFactory = daoFactory;
        this.facadePagamento = facadePagamento;
        this.notificatore = notificatore;
    }

    private GestorePrenotazioni nuovaAttivazione() {
        return new GestorePrenotazioni(daoFactory, facadePagamento, notificatore);
    }

    private GestoreCatalogo nuovaAttivazioneCatalogo() {
        return new GestoreCatalogo(daoFactory);
    }

    public EsitoCatalogoBean cercaNelCatalogo(String destinazione) {
        EsitoCatalogoBean esito = new EsitoCatalogoBean();
        try {
            Catalogo catalogo = nuovaAttivazioneCatalogo().consultaCatalogo();
            esito.setCatalogo(MapperBeanVista.daCatalogo(catalogo, destinazione));
            esito.setSuccesso(true);
        } catch (ApplicazioneException e) {
            esito.setMessaggio(TraduttoreErrori.perUtente(e));
        }
        return esito;
    }

    public EsitoPacchettoBean dettaglioPacchetto(int idPacchetto) {
        EsitoPacchettoBean esito = new EsitoPacchettoBean();
        try {
            Pacchetto pacchetto = nuovaAttivazione().selezionaPacchetto(idPacchetto);
            esito.setPacchetto(MapperBeanVista.daPacchetto(pacchetto));
            esito.setSuccesso(true);
        } catch (ApplicazioneException e) {
            esito.setMessaggio(TraduttoreErrori.perUtente(e));
        }
        return esito;
    }

    public EsitoPreventivoBean calcolaPreventivo(PrenotazioneBean dati) {
        EsitoPreventivoBean esito = new EsitoPreventivoBean();

        String erroreSintassi = dati.validaSintassiViaggio();
        if (erroreSintassi != null) {
            esito.setMessaggio(erroreSintassi);
            return esito;
        }

        try {
            esito.setImportoTotale(nuovaAttivazione().calcolaPreventivo(dati));
            esito.setSuccesso(true);
        } catch (ApplicazioneException e) {
            esito.setMessaggio(TraduttoreErrori.perUtente(e));
        }
        return esito;
    }

    public EsitoOperazioneBean verificaDatiViaggio(PrenotazioneBean dati) {
        EsitoOperazioneBean esito = new EsitoOperazioneBean();

        String erroreSintassi = dati.validaSintassiPartecipanti();
        if (erroreSintassi != null) {
            esito.setMessaggio(erroreSintassi);
            return esito;
        }

        try {
            nuovaAttivazione().verificaDisponibilita(dati);
            esito.setSuccesso(true);
        } catch (ApplicazioneException e) {
            esito.setMessaggio(TraduttoreErrori.perUtente(e));
        }
        return esito;
    }

    public EsitoPrenotazioneBean compilaPrenotazione(PrenotazioneBean dati) {
        EsitoPrenotazioneBean esito = new EsitoPrenotazioneBean();

        String erroreSintassi = dati.validaSintassi();
        if (erroreSintassi != null) {
            esito.setMessaggio(erroreSintassi);
            return esito;
        }

        try {
            Prenotazione prenotazione = nuovaAttivazione().compilaPrenotazione(dati);
            esito.setPrenotazione(MapperBeanVista.daPrenotazione(prenotazione));
            esito.setSuccesso(true);
        } catch (ApplicazioneException e) {
            esito.setMessaggio(TraduttoreErrori.perUtente(e));
        }
        return esito;
    }
}

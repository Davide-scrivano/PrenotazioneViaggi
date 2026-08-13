package controller.grafico;

import java.util.List;

import bean.EsitoOperazione;
import bean.EsitoPrenotazione;
import bean.EsitoPreventivo;
import bean.PrenotazioneBean;
import bean.PrenotazioneVistaBean;
import control.GestorePrenotazioni;
import control.GestoreUtenti;
import exceptions.OperazioneNonConsentitaException;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PagamentoRifiutatoException;
import model.Prenotazione;
import model.Utente;

public class PrenotazioneControllerGraficoCLI {

    private static final String SESSIONE_SCADUTA = "Devi effettuare il login per gestire le prenotazioni.";

    private final GestorePrenotazioni gestorePrenotazioni;
    private final GestoreUtenti gestoreUtenti;

    public PrenotazioneControllerGraficoCLI(GestorePrenotazioni gestorePrenotazioni, GestoreUtenti gestoreUtenti) {
        this.gestorePrenotazioni = gestorePrenotazioni;
        this.gestoreUtenti = gestoreUtenti;
    }

    public EsitoPreventivo calcolaPreventivo(PrenotazioneBean datiPrenotazione) {
        String erroreSintassi = datiPrenotazione.validaSintassiPreventivo();
        if (erroreSintassi != null) {
            return EsitoPreventivo.errore(erroreSintassi);
        }
        try {
            return EsitoPreventivo.successo(gestorePrenotazioni.calcolaPreventivo(datiPrenotazione));
        } catch (PacchettoNonDisponibileException e) {
            return EsitoPreventivo.errore(e.getMessage());
        }
    }

    public EsitoPrenotazione creaPrenotazione(PrenotazioneBean datiPrenotazione) {
        Utente utente = gestoreUtenti.getUtenteLoggato();
        if (utente == null) {
            return EsitoPrenotazione.errore(SESSIONE_SCADUTA);
        }

        String erroreSintassi = datiPrenotazione.validaSintassi();
        if (erroreSintassi != null) {
            return EsitoPrenotazione.errore(erroreSintassi);
        }

        try {
            Prenotazione prenotazione = gestorePrenotazioni.compilaPrenotazione(utente, datiPrenotazione);
            return EsitoPrenotazione.successo(CostruttoreBeanVista.daPrenotazione(prenotazione, false));
        } catch (PagamentoRifiutatoException | PacchettoNonDisponibileException e) {
            return EsitoPrenotazione.errore(e.getMessage());
        }
    }

    public List<PrenotazioneVistaBean> miePrenotazioni() {
        Utente utente = gestoreUtenti.getUtenteLoggato();
        if (utente == null) {
            return List.of();
        }
        return CostruttoreBeanVista.daPrenotazioni(gestorePrenotazioni.getPrenotazioniUtente(utente), false);
    }

    public EsitoOperazione modificaPacchetto(int idPrenotazione, int idNuovoPacchetto) {
        Utente utente = gestoreUtenti.getUtenteLoggato();
        if (utente == null) {
            return EsitoOperazione.errore(SESSIONE_SCADUTA);
        }
        try {
            gestorePrenotazioni.modificaPrenotazione(utente, idPrenotazione, idNuovoPacchetto);
            return EsitoOperazione.successo("Pacchetto aggiornato con successo.");
        } catch (PacchettoNonDisponibileException | OperazioneNonConsentitaException e) {
            return EsitoOperazione.errore(e.getMessage());
        }
    }

    public EsitoOperazione annullaPrenotazione(int idPrenotazione) {
        Utente utente = gestoreUtenti.getUtenteLoggato();
        if (utente == null) {
            return EsitoOperazione.errore(SESSIONE_SCADUTA);
        }
        try {
            boolean annullata = gestorePrenotazioni.annullaPrenotazione(utente, idPrenotazione);
            return annullata
                    ? EsitoOperazione.successo("Prenotazione annullata.")
                    : EsitoOperazione.errore("Prenotazione non trovata o gia' annullata.");
        } catch (OperazioneNonConsentitaException e) {
            return EsitoOperazione.errore(e.getMessage());
        }
    }
}

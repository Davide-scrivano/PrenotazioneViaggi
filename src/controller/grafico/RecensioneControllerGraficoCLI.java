package controller.grafico;

import bean.EsitoOperazione;
import bean.RecensioneBean;
import control.GestoreRecensioni;
import control.GestoreUtenti;
import exceptions.RecensioneNonConsentitaException;
import model.Utente;

public class RecensioneControllerGraficoCLI {

    private final GestoreRecensioni gestoreRecensioni;
    private final GestoreUtenti gestoreUtenti;

    public RecensioneControllerGraficoCLI(GestoreRecensioni gestoreRecensioni, GestoreUtenti gestoreUtenti) {
        this.gestoreRecensioni = gestoreRecensioni;
        this.gestoreUtenti = gestoreUtenti;
    }

    public EsitoOperazione gestisciRecensione(int idPacchetto, int voto, String commento) {
        Utente utente = gestoreUtenti.getUtenteLoggato();
        if (utente == null) {
            return EsitoOperazione.errore("Devi effettuare il login per lasciare una recensione.");
        }

        RecensioneBean dati = new RecensioneBean();
        dati.setIdPacchetto(idPacchetto);
        dati.setVoto(voto);
        dati.setCommento(commento);

        String erroreSintassi = dati.validaSintassi();
        if (erroreSintassi != null) {
            return EsitoOperazione.errore(erroreSintassi);
        }

        try {
            gestoreRecensioni.aggiungiRecensione(utente, dati);
            return EsitoOperazione.successo("Recensione salvata, grazie!");
        } catch (RecensioneNonConsentitaException e) {
            return EsitoOperazione.errore(e.getMessage());
        }
    }
}

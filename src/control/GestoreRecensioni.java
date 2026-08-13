package control;

import java.util.ArrayList;
import java.util.List;

import bean.RecensioneBean;
import exceptions.RecensioneNonConsentitaException;
import model.Pacchetto;
import model.Prenotazione;
import model.Recensione;
import model.Utente;

public class GestoreRecensioni {

    private final List<Recensione> recensioni = new ArrayList<>();
    private int prossimoId = 1;

    public Recensione aggiungiRecensione(Utente utente, RecensioneBean dati)
            throws RecensioneNonConsentitaException {
        Pacchetto pacchetto = Catalogo.getInstance().getPacchettoById(dati.getIdPacchetto());
        if (pacchetto == null) {
            throw new RecensioneNonConsentitaException("Il pacchetto da recensire non e' piu' disponibile a catalogo.");
        }
        if (!haViaggiatoSuPacchetto(utente, pacchetto)) {
            throw new RecensioneNonConsentitaException(
                    "Puoi recensire solo un pacchetto che hai gia' prenotato e non annullato.");
        }

        Recensione recensione = new Recensione(prossimoId++, utente, pacchetto, dati.getVoto(), dati.getCommento());
        recensioni.add(recensione);
        return recensione;
    }

    private boolean haViaggiatoSuPacchetto(Utente utente, Pacchetto pacchetto) {
        for (Prenotazione prenotazione : utente.getPrenotazioniEffettuate()) {
            if (!prenotazione.isAnnullata() && prenotazione.getDettagliPacchetto().equals(pacchetto)) {
                return true;
            }
        }
        return false;
    }

    public List<Recensione> getRecensioniPacchetto(int idPacchetto) {
        List<Recensione> risultato = new ArrayList<>();
        for (Recensione recensione : recensioni) {
            if (recensione.getPacchetto().getId() == idPacchetto) {
                risultato.add(recensione);
            }
        }
        return risultato;
    }

    public double getMediaVoti(int idPacchetto) {
        List<Recensione> recensioniPacchetto = getRecensioniPacchetto(idPacchetto);
        if (recensioniPacchetto.isEmpty()) {
            return 0.0;
        }

        int somma = 0;
        for (Recensione recensione : recensioniPacchetto) {
            somma += recensione.getVoto();
        }
        return (double) somma / recensioniPacchetto.size();
    }
}

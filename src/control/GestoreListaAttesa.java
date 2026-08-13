package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import bean.ListaAttesaBean;
import exceptions.IscrizioneListaAttesaNonConsentitaException;
import model.Pacchetto;
import model.RichiestaListaAttesa;
import model.Utente;

public class GestoreListaAttesa implements OsservatorePosti {

    private static final Logger LOGGER = Logger.getLogger(GestoreListaAttesa.class.getName());

    private final List<RichiestaListaAttesa> richieste = new ArrayList<>();
    private int prossimoId = 1;

    public RichiestaListaAttesa iscrivi(Utente utente, ListaAttesaBean dati)
            throws IscrizioneListaAttesaNonConsentitaException {
        Pacchetto pacchetto = Catalogo.getInstance().getPacchettoById(dati.getIdPacchetto());
        if (pacchetto == null) {
            throw new IscrizioneListaAttesaNonConsentitaException("Il pacchetto scelto non e' piu' disponibile a catalogo.");
        }
        if (pacchetto.isDisponibile(dati.getNumeroPosti())) {
            throw new IscrizioneListaAttesaNonConsentitaException("Il pacchetto \"" + pacchetto.getDestinazione()
                    + "\" ha gia' abbastanza posti disponibili: puoi prenotare direttamente.");
        }
        if (esisteIscrizione(utente, pacchetto)) {
            throw new IscrizioneListaAttesaNonConsentitaException("Sei gia' iscritto alla lista d'attesa di \""
                    + pacchetto.getDestinazione() + "\".");
        }

        RichiestaListaAttesa richiesta = new RichiestaListaAttesa(prossimoId++, utente, pacchetto, dati.getNumeroPosti());
        richieste.add(richiesta);
        return richiesta;
    }

    private boolean esisteIscrizione(Utente utente, Pacchetto pacchetto) {
        for (RichiestaListaAttesa richiesta : richieste) {
            if (!richiesta.isNotificata() && richiesta.getUtente().equals(utente)
                    && richiesta.getPacchetto().equals(pacchetto)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postiLiberati(Pacchetto pacchetto, int numeroPostiLiberati) {
        for (RichiestaListaAttesa richiesta : richieste) {
            if (!richiesta.isNotificata() && richiesta.getPacchetto().equals(pacchetto)
                    && pacchetto.isDisponibile(richiesta.getNumeroPosti())) {
                richiesta.segnaNotificata();
                LOGGER.log(Level.INFO,
                        "Notifica lista d''attesa inviata a {0}: si sono liberati posti su \"{1}\" per {2} partecipanti.",
                        new Object[] { richiesta.getUtente().getEmail(), pacchetto.getDestinazione(),
                                richiesta.getNumeroPosti() });
            }
        }
    }

    public List<RichiestaListaAttesa> getRichiesteUtente(Utente utente) {
        List<RichiestaListaAttesa> risultato = new ArrayList<>();
        for (RichiestaListaAttesa richiesta : richieste) {
            if (richiesta.getUtente().equals(utente)) {
                risultato.add(richiesta);
            }
        }
        return risultato;
    }

    public List<RichiestaListaAttesa> getTutteLeRichieste() {
        return Collections.unmodifiableList(richieste);
    }
}

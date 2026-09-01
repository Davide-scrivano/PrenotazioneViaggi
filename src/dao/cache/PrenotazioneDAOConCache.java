package dao.cache;

import java.util.HashMap;
import java.util.Map;

import dao.PrenotazioneDAO;
import exceptions.PersistenzaException;
import model.Prenotazione;

public final class PrenotazioneDAOConCache implements PrenotazioneDAO, OsservatoreCache {

    private final PrenotazioneDAO componente;
    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private final Map<Integer, Prenotazione> prenotazioni = new HashMap<>();

    public PrenotazioneDAOConCache(PrenotazioneDAO componente) {
        this.componente = componente;
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        prenotazioni.clear();
    }

    @Override
    public int prossimoId() throws PersistenzaException {
        return componente.prossimoId();
    }

    @Override
    public void inserisci(Prenotazione prenotazione) throws PersistenzaException {
        componente.inserisci(prenotazione);
        soggetto.datiModificati();
        prenotazioni.put(prenotazione.getId(), prenotazione);
    }

    @Override
    public Prenotazione trovaPerId(int id) throws PersistenzaException {
        Prenotazione prenotazione = prenotazioni.get(id);
        if (prenotazione == null) {
            prenotazione = componente.trovaPerId(id);
            if (prenotazione != null) {
                prenotazioni.put(prenotazione.getId(), prenotazione);
            }
        }
        return prenotazione;
    }
}

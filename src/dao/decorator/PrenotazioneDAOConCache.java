package dao.decorator;

import java.util.HashMap;
import java.util.Map;

import dao.PrenotazioneDAO;
import dao.cache.MemoriaCentrale;
import dao.cache.OsservatoreCache;
import exceptions.PersistenzaException;
import model.Prenotazione;

public final class PrenotazioneDAOConCache extends PrenotazioneDAODecorator implements OsservatoreCache {

    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private final Map<Integer, Prenotazione> prenotazioni = new HashMap<>();

    public PrenotazioneDAOConCache(PrenotazioneDAO componente) {
        super(componente);
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        prenotazioni.clear();
    }

    @Override
    public void inserisci(Prenotazione prenotazione) throws PersistenzaException {
        super.inserisci(prenotazione);
        soggetto.datiModificati();
        prenotazioni.put(prenotazione.getId(), prenotazione);
    }

    @Override
    public Prenotazione trovaPerId(int id) throws PersistenzaException {
        Prenotazione prenotazione = prenotazioni.get(id);
        if (prenotazione == null) {
            prenotazione = super.trovaPerId(id);
            if (prenotazione != null) {
                prenotazioni.put(prenotazione.getId(), prenotazione);
            }
        }
        return prenotazione;
    }
}

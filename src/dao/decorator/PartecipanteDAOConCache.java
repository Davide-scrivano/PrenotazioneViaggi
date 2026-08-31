package dao.decorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.PartecipanteDAO;
import dao.cache.MemoriaCentrale;
import dao.cache.OsservatoreCache;
import exceptions.PersistenzaException;
import model.Partecipante;

public final class PartecipanteDAOConCache extends PartecipanteDAODecorator implements OsservatoreCache {

    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private final Map<Integer, List<Partecipante>> partecipanti = new HashMap<>();

    public PartecipanteDAOConCache(PartecipanteDAO componente) {
        super(componente);
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        partecipanti.clear();
    }

    @Override
    public void inserisci(Partecipante partecipante, int idPrenotazione) throws PersistenzaException {
        super.inserisci(partecipante, idPrenotazione);
        soggetto.datiModificati();
    }

    @Override
    public List<Partecipante> trovaPerPrenotazione(int idPrenotazione) throws PersistenzaException {
        List<Partecipante> memorizzati = partecipanti.get(idPrenotazione);
        if (memorizzati != null) {
            return memorizzati;
        }
        List<Partecipante> letti = new ArrayList<>(super.trovaPerPrenotazione(idPrenotazione));
        partecipanti.put(idPrenotazione, letti);
        return letti;
    }
}

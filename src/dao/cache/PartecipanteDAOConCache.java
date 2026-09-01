package dao.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.PartecipanteDAO;
import exceptions.PersistenzaException;
import model.Partecipante;

public final class PartecipanteDAOConCache implements PartecipanteDAO, OsservatoreCache {

    private final PartecipanteDAO componente;
    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private final Map<Integer, List<Partecipante>> partecipanti = new HashMap<>();

    public PartecipanteDAOConCache(PartecipanteDAO componente) {
        this.componente = componente;
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        partecipanti.clear();
    }

    @Override
    public int prossimoId() throws PersistenzaException {
        return componente.prossimoId();
    }

    @Override
    public void inserisci(Partecipante partecipante, int idPrenotazione) throws PersistenzaException {
        componente.inserisci(partecipante, idPrenotazione);
        soggetto.datiModificati();
    }

    @Override
    public List<Partecipante> trovaPerPrenotazione(int idPrenotazione) throws PersistenzaException {
        List<Partecipante> memorizzati = partecipanti.get(idPrenotazione);
        if (memorizzati != null) {
            return memorizzati;
        }
        List<Partecipante> letti = new ArrayList<>(componente.trovaPerPrenotazione(idPrenotazione));
        partecipanti.put(idPrenotazione, letti);
        return letti;
    }
}

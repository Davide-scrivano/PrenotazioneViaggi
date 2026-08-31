package dao.decorator;

import java.util.ArrayList;
import java.util.List;

import dao.PacchettoDAO;
import dao.cache.MemoriaCentrale;
import dao.cache.OsservatoreCache;
import exceptions.PersistenzaException;
import model.Pacchetto;

public final class PacchettoDAOConCache extends PacchettoDAODecorator implements OsservatoreCache {

    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private List<Pacchetto> pacchetti = null;

    public PacchettoDAOConCache(PacchettoDAO componente) {
        super(componente);
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        pacchetti = null;
    }

    @Override
    public List<Pacchetto> trovaTutti() throws PersistenzaException {
        if (pacchetti == null) {
            pacchetti = new ArrayList<>(super.trovaTutti());
        }
        return pacchetti;
    }

    @Override
    public Pacchetto trovaPerId(int id) throws PersistenzaException {
        for (Pacchetto pacchetto : trovaTutti()) {
            if (pacchetto.getId() == id) {
                return pacchetto;
            }
        }
        return null;
    }

    @Override
    public void aggiorna(Pacchetto pacchetto) throws PersistenzaException {
        super.aggiorna(pacchetto);
        soggetto.datiModificati();
    }
}

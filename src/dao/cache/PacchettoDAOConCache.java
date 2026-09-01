package dao.cache;

import java.util.ArrayList;
import java.util.List;

import dao.PacchettoDAO;
import exceptions.PersistenzaException;
import model.Pacchetto;

public final class PacchettoDAOConCache implements PacchettoDAO, OsservatoreCache {

    private final PacchettoDAO componente;
    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private List<Pacchetto> pacchetti = null;

    public PacchettoDAOConCache(PacchettoDAO componente) {
        this.componente = componente;
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        pacchetti = null;
    }

    @Override
    public List<Pacchetto> trovaTutti() throws PersistenzaException {
        if (pacchetti == null) {
            pacchetti = new ArrayList<>(componente.trovaTutti());
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
        componente.aggiorna(pacchetto);
        soggetto.datiModificati();
    }
}

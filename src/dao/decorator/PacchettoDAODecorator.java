package dao.decorator;

import java.util.List;

import dao.PacchettoDAO;
import exceptions.PersistenzaException;
import model.Pacchetto;

public abstract class PacchettoDAODecorator implements PacchettoDAO {

    private final PacchettoDAO componente;

    protected PacchettoDAODecorator(PacchettoDAO componente) {
        this.componente = componente;
    }

    @Override
    public List<Pacchetto> trovaTutti() throws PersistenzaException {
        return componente.trovaTutti();
    }

    @Override
    public Pacchetto trovaPerId(int id) throws PersistenzaException {
        return componente.trovaPerId(id);
    }

    @Override
    public void aggiorna(Pacchetto pacchetto) throws PersistenzaException {
        componente.aggiorna(pacchetto);
    }
}

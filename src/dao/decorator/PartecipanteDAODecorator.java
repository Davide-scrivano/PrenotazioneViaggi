package dao.decorator;

import java.util.List;

import dao.PartecipanteDAO;
import exceptions.PersistenzaException;
import model.Partecipante;

public abstract class PartecipanteDAODecorator implements PartecipanteDAO {

    private final PartecipanteDAO componente;

    protected PartecipanteDAODecorator(PartecipanteDAO componente) {
        this.componente = componente;
    }

    @Override
    public int prossimoId() throws PersistenzaException {
        return componente.prossimoId();
    }

    @Override
    public void inserisci(Partecipante partecipante, int idPrenotazione) throws PersistenzaException {
        componente.inserisci(partecipante, idPrenotazione);
    }

    @Override
    public List<Partecipante> trovaPerPrenotazione(int idPrenotazione) throws PersistenzaException {
        return componente.trovaPerPrenotazione(idPrenotazione);
    }
}

package dao.decorator;

import dao.PrenotazioneDAO;
import exceptions.PersistenzaException;
import model.Prenotazione;

public abstract class PrenotazioneDAODecorator implements PrenotazioneDAO {

    private final PrenotazioneDAO componente;

    protected PrenotazioneDAODecorator(PrenotazioneDAO componente) {
        this.componente = componente;
    }

    @Override
    public int prossimoId() throws PersistenzaException {
        return componente.prossimoId();
    }

    @Override
    public void inserisci(Prenotazione prenotazione) throws PersistenzaException {
        componente.inserisci(prenotazione);
    }

    @Override
    public Prenotazione trovaPerId(int id) throws PersistenzaException {
        return componente.trovaPerId(id);
    }
}

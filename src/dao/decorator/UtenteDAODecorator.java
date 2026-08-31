package dao.decorator;

import dao.UtenteDAO;
import exceptions.PersistenzaException;
import model.Utente;

public abstract class UtenteDAODecorator implements UtenteDAO {

    private final UtenteDAO componente;

    protected UtenteDAODecorator(UtenteDAO componente) {
        this.componente = componente;
    }

    @Override
    public Utente trovaPerId(int id) throws PersistenzaException {
        return componente.trovaPerId(id);
    }

    @Override
    public Utente trovaPerNickname(String nickname) throws PersistenzaException {
        return componente.trovaPerNickname(nickname);
    }
}

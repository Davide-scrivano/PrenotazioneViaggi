package dao.decorator;

import dao.CatalogoDAO;
import exceptions.PersistenzaException;
import model.Catalogo;

public abstract class CatalogoDAODecorator implements CatalogoDAO {

    private final CatalogoDAO componente;

    protected CatalogoDAODecorator(CatalogoDAO componente) {
        this.componente = componente;
    }

    @Override
    public Catalogo carica() throws PersistenzaException {
        return componente.carica();
    }
}

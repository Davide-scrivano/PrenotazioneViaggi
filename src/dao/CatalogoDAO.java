package dao;

import exceptions.PersistenzaException;
import model.Catalogo;

public interface CatalogoDAO {

    Catalogo carica() throws PersistenzaException;
}

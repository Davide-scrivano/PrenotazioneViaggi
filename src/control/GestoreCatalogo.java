package control;

import dao.CatalogoDAO;
import dao.DAOFactory;
import dao.cache.MemoriaCentrale;
import exceptions.PersistenzaException;
import model.Catalogo;

public class GestoreCatalogo {

    private final DAOFactory daoFactory;
    private final MemoriaCentrale memoriaCentrale = MemoriaCentrale.getSingletonInstance();

    public GestoreCatalogo(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public Catalogo consultaCatalogo() throws PersistenzaException {
        Catalogo catalogo = memoriaCentrale.getCatalogo();
        if (catalogo == null) {
            CatalogoDAO catalogoDAO = daoFactory.creaCatalogoDAO();
            catalogo = catalogoDAO.carica();
            memoriaCentrale.memorizzaCatalogo(catalogo);
        }
        return catalogo;
    }
}

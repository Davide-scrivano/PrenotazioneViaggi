package dao.cache;

import dao.CatalogoDAO;
import exceptions.PersistenzaException;
import model.Catalogo;

public final class CatalogoDAOConCache implements CatalogoDAO, OsservatoreCache {

    private final CatalogoDAO componente;
    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private Catalogo catalogo = null;

    public CatalogoDAOConCache(CatalogoDAO componente) {
        this.componente = componente;
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        catalogo = null;
    }

    @Override
    public Catalogo carica() throws PersistenzaException {
        if (catalogo == null) {
            catalogo = componente.carica();
        }
        return catalogo;
    }
}

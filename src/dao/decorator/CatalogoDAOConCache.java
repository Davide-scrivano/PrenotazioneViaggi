package dao.decorator;

import dao.CatalogoDAO;
import dao.cache.MemoriaCentrale;
import dao.cache.OsservatoreCache;
import exceptions.PersistenzaException;
import model.Catalogo;

public final class CatalogoDAOConCache extends CatalogoDAODecorator implements OsservatoreCache {

    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private Catalogo catalogo = null;

    public CatalogoDAOConCache(CatalogoDAO componente) {
        super(componente);
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        catalogo = null;
    }

    @Override
    public Catalogo carica() throws PersistenzaException {
        if (catalogo == null) {
            catalogo = super.carica();
        }
        return catalogo;
    }
}

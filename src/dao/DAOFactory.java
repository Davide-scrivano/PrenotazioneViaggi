package dao;

import config.ConfigurazioneGlobale;
import config.TipoPersistenza;
import dao.database.DAOFactoryDatabase;
import dao.filesystem.DAOFactoryFileSystem;

public abstract class DAOFactory {

    private static DAOFactory istanza = null;

    protected DAOFactory() {
    }

    public static synchronized DAOFactory getSingletonInstance() {
        if (istanza == null) {
            TipoPersistenza persistenza = ConfigurazioneGlobale.getSingletonInstance().getPersistenza();
            if (persistenza == TipoPersistenza.DATABASE) {
                istanza = new DAOFactoryDatabase();
            } else {
                istanza = new DAOFactoryFileSystem();
            }
        }
        return istanza;
    }

    public abstract UtenteDAO creaUtenteDAO();

    public abstract PacchettoDAO creaPacchettoDAO();

    public abstract CatalogoDAO creaCatalogoDAO();

    public abstract PagamentoDAO creaPagamentoDAO();

    public abstract PartecipanteDAO creaPartecipanteDAO();

    public abstract PrenotazioneDAO creaPrenotazioneDAO();
}

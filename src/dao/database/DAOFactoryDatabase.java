package dao.database;

import dao.CatalogoDAO;
import dao.DAOFactory;
import dao.PacchettoDAO;
import dao.PagamentoDAO;
import dao.PartecipanteDAO;
import dao.PrenotazioneDAO;
import dao.UtenteDAO;
import dao.cache.CatalogoDAOConCache;
import dao.cache.MemoriaCentrale;
import dao.cache.PacchettoDAOConCache;
import dao.cache.PagamentoDAOConCache;
import dao.cache.PartecipanteDAOConCache;
import dao.cache.PrenotazioneDAOConCache;
import dao.cache.UtenteDAOConCache;

public class DAOFactoryDatabase extends DAOFactory {

    private final UtenteDAO utenteDAO;
    private final PacchettoDAO pacchettoDAO;
    private final CatalogoDAO catalogoDAO;
    private final PagamentoDAO pagamentoDAO;
    private final PartecipanteDAO partecipanteDAO;
    private final PrenotazioneDAO prenotazioneDAO;

    public DAOFactoryDatabase() {
        MemoriaCentrale.getSingletonInstance().reimposta();

        this.utenteDAO = new UtenteDAOConCache(new UtenteDAODatabase());
        this.pacchettoDAO = new PacchettoDAOConCache(new PacchettoDAODatabase());
        this.catalogoDAO = new CatalogoDAOConCache(new CatalogoDAODatabase(pacchettoDAO));
        this.pagamentoDAO = new PagamentoDAOConCache(new PagamentoDAODatabase());
        this.partecipanteDAO = new PartecipanteDAOConCache(new PartecipanteDAODatabase());
        this.prenotazioneDAO = new PrenotazioneDAOConCache(
                new PrenotazioneDAODatabase(utenteDAO, pacchettoDAO, pagamentoDAO, partecipanteDAO));
    }

    @Override
    public UtenteDAO creaUtenteDAO() {
        return utenteDAO;
    }

    @Override
    public PacchettoDAO creaPacchettoDAO() {
        return pacchettoDAO;
    }

    @Override
    public CatalogoDAO creaCatalogoDAO() {
        return catalogoDAO;
    }

    @Override
    public PagamentoDAO creaPagamentoDAO() {
        return pagamentoDAO;
    }

    @Override
    public PartecipanteDAO creaPartecipanteDAO() {
        return partecipanteDAO;
    }

    @Override
    public PrenotazioneDAO creaPrenotazioneDAO() {
        return prenotazioneDAO;
    }
}

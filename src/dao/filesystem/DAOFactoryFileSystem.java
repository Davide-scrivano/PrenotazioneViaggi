package dao.filesystem;

import config.ConfigurazioneGlobale;
import dao.CatalogoDAO;
import dao.DAOFactory;
import dao.PacchettoDAO;
import dao.PagamentoDAO;
import dao.PartecipanteDAO;
import dao.PrenotazioneDAO;
import dao.UtenteDAO;
import dao.cache.MemoriaCentrale;
import dao.decorator.CatalogoDAOConCache;
import dao.decorator.PacchettoDAOConCache;
import dao.decorator.PagamentoDAOConCache;
import dao.decorator.PartecipanteDAOConCache;
import dao.decorator.PrenotazioneDAOConCache;
import dao.decorator.UtenteDAOConCache;

public class DAOFactoryFileSystem extends DAOFactory {

    private static final String FILE_UTENTI = "utenti.dat";
    private static final String FILE_PACCHETTI = "pacchetti.dat";
    private static final String FILE_CATALOGO = "catalogo.dat";
    private static final String FILE_PAGAMENTI = "pagamenti.dat";
    private static final String FILE_PARTECIPANTI = "partecipanti.dat";
    private static final String FILE_PRENOTAZIONI = "prenotazioni.dat";

    private final UtenteDAO utenteDAO;
    private final PacchettoDAO pacchettoDAO;
    private final CatalogoDAO catalogoDAO;
    private final PagamentoDAO pagamentoDAO;
    private final PartecipanteDAO partecipanteDAO;
    private final PrenotazioneDAO prenotazioneDAO;

    public DAOFactoryFileSystem() {
        this(ConfigurazioneGlobale.getSingletonInstance().getCartellaDati());
    }

    public DAOFactoryFileSystem(String cartellaDati) {
        MemoriaCentrale.getSingletonInstance().reimposta();

        this.utenteDAO = new UtenteDAOConCache(
                new UtenteDAOFileSystem(percorso(cartellaDati, FILE_UTENTI)));
        this.pacchettoDAO = new PacchettoDAOConCache(
                new PacchettoDAOFileSystem(percorso(cartellaDati, FILE_PACCHETTI)));
        this.catalogoDAO = new CatalogoDAOConCache(
                new CatalogoDAOFileSystem(percorso(cartellaDati, FILE_CATALOGO), pacchettoDAO));
        this.pagamentoDAO = new PagamentoDAOConCache(
                new PagamentoDAOFileSystem(percorso(cartellaDati, FILE_PAGAMENTI)));
        this.partecipanteDAO = new PartecipanteDAOConCache(
                new PartecipanteDAOFileSystem(percorso(cartellaDati, FILE_PARTECIPANTI)));
        this.prenotazioneDAO = new PrenotazioneDAOConCache(
                new PrenotazioneDAOFileSystem(percorso(cartellaDati, FILE_PRENOTAZIONI),
                        utenteDAO, pacchettoDAO, pagamentoDAO, partecipanteDAO));
    }

    private static String percorso(String cartellaDati, String nomeFile) {
        return cartellaDati + "/" + nomeFile;
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

package control;

import bean.LoginBean;
import dao.DAOFactory;
import dao.UtenteDAO;
import dao.cache.MemoriaCentrale;
import exceptions.CredenzialiNonValideException;
import exceptions.PersistenzaException;
import model.Utente;

public class GestoreLogin {

    private final DAOFactory daoFactory;
    private final MemoriaCentrale memoriaCentrale = MemoriaCentrale.getSingletonInstance();

    public GestoreLogin(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public Utente autentica(LoginBean credenziali) throws CredenzialiNonValideException, PersistenzaException {
        Utente utente = memoriaCentrale.getUtente(credenziali.getNickname());
        if (utente == null) {
            UtenteDAO utenteDAO = daoFactory.creaUtenteDAO();
            utente = utenteDAO.trovaPerNickname(credenziali.getNickname());
            memoriaCentrale.memorizzaUtente(utente);
        }
        if (utente == null || !utente.credenzialiValide(credenziali.getPassword())) {
            throw new CredenzialiNonValideException("Nickname o password non corretti.");
        }
        return utente;
    }
}

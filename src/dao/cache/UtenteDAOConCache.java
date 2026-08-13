package dao.cache;

import java.util.ArrayList;
import java.util.List;

import dao.UtenteDAO;
import exceptions.PersistenzaException;
import model.Utente;

public class UtenteDAOConCache implements UtenteDAO {

    private final UtenteDAO daoReale;
    private final CacheUtenti cache;
    private final List<OsservatoreCache> osservatori = new ArrayList<>();

    public UtenteDAOConCache(UtenteDAO daoReale) {
        this.daoReale = daoReale;
        this.cache = CacheUtenti.getInstance();
        registraOsservatore(cache);
    }

    public void registraOsservatore(OsservatoreCache osservatore) {
        osservatori.add(osservatore);
    }

    private void notificaOsservatori() {
        for (OsservatoreCache osservatore : osservatori) {
            osservatore.invalida();
        }
    }

    @Override
    public void salva(List<Utente> utenti) throws PersistenzaException {
        daoReale.salva(utenti);
        notificaOsservatori();
    }

    @Override
    public List<Utente> carica() throws PersistenzaException {
        if (cache.isValida()) {
            return cache.leggi();
        }

        List<Utente> utenti = daoReale.carica();
        cache.popola(utenti);
        return utenti;
    }
}

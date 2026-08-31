package dao.decorator;

import java.util.HashMap;
import java.util.Map;

import dao.UtenteDAO;
import dao.cache.MemoriaCentrale;
import dao.cache.OsservatoreCache;
import exceptions.PersistenzaException;
import model.Utente;

public final class UtenteDAOConCache extends UtenteDAODecorator implements OsservatoreCache {

    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private final Map<Integer, Utente> utentiPerId = new HashMap<>();
    private final Map<String, Utente> utentiPerNickname = new HashMap<>();

    public UtenteDAOConCache(UtenteDAO componente) {
        super(componente);
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        utentiPerId.clear();
        utentiPerNickname.clear();
    }

    @Override
    public Utente trovaPerId(int id) throws PersistenzaException {
        Utente utente = utentiPerId.get(id);
        if (utente == null) {
            utente = super.trovaPerId(id);
            memorizza(utente);
        }
        return utente;
    }

    @Override
    public Utente trovaPerNickname(String nickname) throws PersistenzaException {
        Utente utente = utentiPerNickname.get(nickname);
        if (utente == null) {
            utente = super.trovaPerNickname(nickname);
            memorizza(utente);
        }
        return utente;
    }

    private void memorizza(Utente utente) {
        if (utente != null) {
            utentiPerId.put(utente.getId(), utente);
            utentiPerNickname.put(utente.getNickname(), utente);
        }
    }
}

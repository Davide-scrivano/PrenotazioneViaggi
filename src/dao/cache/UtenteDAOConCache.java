package dao.cache;

import java.util.HashMap;
import java.util.Map;

import dao.UtenteDAO;
import exceptions.PersistenzaException;
import model.Utente;

public final class UtenteDAOConCache implements UtenteDAO, OsservatoreCache {

    private final UtenteDAO componente;
    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private final Map<Integer, Utente> utentiPerId = new HashMap<>();
    private final Map<String, Utente> utentiPerNickname = new HashMap<>();

    public UtenteDAOConCache(UtenteDAO componente) {
        this.componente = componente;
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
            utente = componente.trovaPerId(id);
            memorizza(utente);
        }
        return utente;
    }

    @Override
    public Utente trovaPerNickname(String nickname) throws PersistenzaException {
        Utente utente = utentiPerNickname.get(nickname);
        if (utente == null) {
            utente = componente.trovaPerNickname(nickname);
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

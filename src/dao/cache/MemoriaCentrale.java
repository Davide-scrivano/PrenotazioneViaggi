package dao.cache;

import java.util.HashMap;
import java.util.Map;

import model.Catalogo;
import model.Utente;

public class MemoriaCentrale extends SoggettoCache {

    private Catalogo catalogo = null;
    private final Map<Integer, Utente> utentiPerId = new HashMap<>();
    private final Map<String, Utente> utentiPerNickname = new HashMap<>();

    private MemoriaCentrale() {
    }

    private static class Contenitore {
        private static final MemoriaCentrale ISTANZA = new MemoriaCentrale();
    }

    public static final MemoriaCentrale getSingletonInstance() {
        return Contenitore.ISTANZA;
    }

    public void datiModificati() {
        svuota();
        notificaOsservatori();
    }

    private synchronized void svuota() {
        catalogo = null;
        utentiPerId.clear();
        utentiPerNickname.clear();
    }

    public void reimposta() {
        rimuoviTuttiGliOsservatori();
        svuota();
    }

    public synchronized Catalogo getCatalogo() {
        return catalogo;
    }

    public synchronized void memorizzaCatalogo(Catalogo daMemorizzare) {
        this.catalogo = daMemorizzare;
    }

    public synchronized Utente getUtente(int id) {
        return utentiPerId.get(id);
    }

    public synchronized Utente getUtente(String nickname) {
        return utentiPerNickname.get(nickname);
    }

    public synchronized void memorizzaUtente(Utente utente) {
        if (utente != null) {
            utentiPerId.put(utente.getId(), utente);
            utentiPerNickname.put(utente.getNickname(), utente);
        }
    }
}

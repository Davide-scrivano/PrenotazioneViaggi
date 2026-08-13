package dao.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Utente;

public class CacheUtenti implements OsservatoreCache {

    private List<Utente> datiInCache;
    private boolean valida = false;

    private CacheUtenti() {
    }

    private static class Holder {
        private static final CacheUtenti ISTANZA = new CacheUtenti();
    }

    public static CacheUtenti getInstance() {
        return Holder.ISTANZA;
    }

    public boolean isValida() {
        return valida;
    }

    public List<Utente> leggi() {
        if (datiInCache == null) {
            return List.of();
        }
        return Collections.unmodifiableList(datiInCache);
    }

    public void popola(List<Utente> dati) {
        this.datiInCache = new ArrayList<>(dati);
        this.valida = true;
    }

    @Override
    public void invalida() {
        this.datiInCache = null;
        this.valida = false;
    }
}

package dao.cache;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SoggettoCache {

    private final List<OsservatoreCache> osservatori = new CopyOnWriteArrayList<>();

    public void registraOsservatore(OsservatoreCache osservatore) {
        osservatori.add(osservatore);
    }

    public void rimuoviOsservatore(OsservatoreCache osservatore) {
        osservatori.remove(osservatore);
    }

    protected void notificaOsservatori() {
        for (OsservatoreCache osservatore : osservatori) {
            osservatore.aggiorna();
        }
    }

    protected void rimuoviTuttiGliOsservatori() {
        osservatori.clear();
    }
}

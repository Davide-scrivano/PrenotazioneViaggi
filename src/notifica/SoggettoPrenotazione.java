package notifica;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SoggettoPrenotazione {

    private final List<OsservatorePrenotazione> osservatori = new CopyOnWriteArrayList<>();

    public void registraOsservatore(OsservatorePrenotazione osservatore) {
        osservatori.add(osservatore);
    }

    public void rimuoviOsservatore(OsservatorePrenotazione osservatore) {
        osservatori.remove(osservatore);
    }

    protected void notificaOsservatori() {
        for (OsservatorePrenotazione osservatore : osservatori) {
            osservatore.aggiorna();
        }
    }
}

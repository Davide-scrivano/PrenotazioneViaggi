package notifica;

import java.util.logging.Level;
import java.util.logging.Logger;

import model.Prenotazione;

public final class OsservatoreRegistroAgenzia implements OsservatorePrenotazione {

    private static final Logger LOGGER = Logger.getLogger(OsservatoreRegistroAgenzia.class.getName());

    private final NotificatorePrenotazioni soggetto;

    public OsservatoreRegistroAgenzia(NotificatorePrenotazioni soggetto) {
        this.soggetto = soggetto;
    }

    @Override
    public void aggiorna() {
        Prenotazione prenotazione = soggetto.getPrenotazione();
        LOGGER.log(Level.INFO, "Registrata vendita: prenotazione #{0}, {1} partecipanti, {2} euro.",
                new Object[] { prenotazione.getId(), prenotazione.getNumeroPartecipanti(),
                        prenotazione.getImportoTotale() });
    }
}

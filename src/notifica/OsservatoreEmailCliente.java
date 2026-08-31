package notifica;

import java.util.logging.Level;
import java.util.logging.Logger;

import model.Prenotazione;

public final class OsservatoreEmailCliente implements OsservatorePrenotazione {

    private static final Logger LOGGER = Logger.getLogger(OsservatoreEmailCliente.class.getName());

    private final NotificatorePrenotazioni soggetto;

    public OsservatoreEmailCliente(NotificatorePrenotazioni soggetto) {
        this.soggetto = soggetto;
    }

    @Override
    public void aggiorna() {
        Prenotazione prenotazione = soggetto.getPrenotazione();
        LOGGER.log(Level.INFO, "Email di conferma inviata a {0} ({1}): prenotazione #{2} per {3}.",
                new Object[] { prenotazione.getNominativoCliente(), prenotazione.getEmailCliente(),
                        prenotazione.getId(), prenotazione.getDestinazione() });
    }
}

package dao.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.ConfigurazioneGlobale;
import exceptions.PersistenzaException;

public class PoolConnessioni {

    private static final Logger LOGGER = Logger.getLogger(PoolConnessioni.class.getName());

    private Connection connessione;

    private PoolConnessioni() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::chiudi));
    }

    private static class Contenitore {
        private static final PoolConnessioni ISTANZA = new PoolConnessioni();
    }

    public static final PoolConnessioni getSingletonInstance() {
        return Contenitore.ISTANZA;
    }

    public synchronized Connection getConnessione() throws PersistenzaException {
        try {
            if (connessione == null || connessione.isClosed()) {
                ConfigurazioneGlobale configurazione = ConfigurazioneGlobale.getSingletonInstance();
                connessione = DriverManager.getConnection(configurazione.getUrlDatabase(),
                        configurazione.getUtenteDatabase(), configurazione.getPasswordDatabase());
            }
            return connessione;
        } catch (SQLException e) {
            throw new PersistenzaException("Connessione al database non disponibile: " + e.getMessage(), e);
        }
    }

    private synchronized void chiudi() {
        try {
            if (connessione != null && !connessione.isClosed()) {
                connessione.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Chiusura della connessione al database non riuscita.", e);
        }
    }
}

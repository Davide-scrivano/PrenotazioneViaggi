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

    private static PoolConnessioni istanza = null;

    private Connection connessione;

    private PoolConnessioni() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::chiudi));
    }

    public static synchronized PoolConnessioni getSingletonInstance() {
        if (istanza == null) {
            istanza = new PoolConnessioni();
        }
        return istanza;
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

package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurazioneGlobale {

    private static final Logger LOGGER = Logger.getLogger(ConfigurazioneGlobale.class.getName());

    private static final String PERCORSO_FILE = "config/app.properties";
    private static final String VARIABILE_PASSWORD = "PRENOTAZIONEVIAGGI_DB_PASSWORD";

    private TipoPersistenza persistenza = TipoPersistenza.FILE;
    private TipoInterfaccia interfaccia = TipoInterfaccia.JAVAFX;
    private String cartellaDati = "fileData";
    private String urlDatabase = "jdbc:mysql://localhost:3306/prenotazioneviaggi";
    private String utenteDatabase = "root";
    private String passwordDatabase = "";

    private static ConfigurazioneGlobale istanza = null;

    private ConfigurazioneGlobale() {
        caricaDaFile();
    }

    public static synchronized ConfigurazioneGlobale getSingletonInstance() {
        if (istanza == null) {
            istanza = new ConfigurazioneGlobale();
        }
        return istanza;
    }

    private void caricaDaFile() {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(PERCORSO_FILE)) {
            properties.load(input);
            persistenza = leggiScelta(properties, "persistenza", persistenza);
            interfaccia = leggiScelta(properties, "interfaccia", interfaccia);
            cartellaDati = properties.getProperty("cartellaDati", cartellaDati).trim();
            urlDatabase = properties.getProperty("dbUrl", urlDatabase).trim();
            utenteDatabase = properties.getProperty("dbUtente", utenteDatabase).trim();
            passwordDatabase = passwordDatabase(properties);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "File di configurazione {0} non leggibile: uso i valori predefiniti"
                    + " (persistenza=FILE, interfaccia=JAVAFX).", PERCORSO_FILE);
        }
    }

    private String passwordDatabase(Properties properties) {
        String daAmbiente = System.getenv(VARIABILE_PASSWORD);
        if (daAmbiente != null && !daAmbiente.isBlank()) {
            return daAmbiente;
        }
        return properties.getProperty("dbPassword", passwordDatabase).trim();
    }

    private static <E extends Enum<E>> E leggiScelta(Properties properties, String chiave, E predefinito) {
        String valore = properties.getProperty(chiave);
        if (valore == null) {
            return predefinito;
        }
        for (E scelta : predefinito.getDeclaringClass().getEnumConstants()) {
            if (scelta.name().equalsIgnoreCase(valore.trim())) {
                return scelta;
            }
        }
        return predefinito;
    }

    public String getCartellaDati() {
        return cartellaDati;
    }

    public TipoPersistenza getPersistenza() {
        return persistenza;
    }

    public TipoInterfaccia getInterfaccia() {
        return interfaccia;
    }

    public String getUrlDatabase() {
        return urlDatabase;
    }

    public String getUtenteDatabase() {
        return utenteDatabase;
    }

    public String getPasswordDatabase() {
        return passwordDatabase;
    }
}

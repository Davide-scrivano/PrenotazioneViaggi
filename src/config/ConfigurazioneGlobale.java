package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurazioneGlobale {

    private static final Logger LOGGER = Logger.getLogger(ConfigurazioneGlobale.class.getName());

    private static final String PERCORSO_FILE = "config/app.properties";

    private Modalita modalita;
    private TipoPersistenza persistenza;
    private TipoGui gui;
    private String percorsoFileUtenti;
    private String percorsoFilePacchetti;
    private String percorsoFilePrenotazioni;
    private String urlDatabase;
    private String utenteDatabase;
    private String passwordDatabase;

    private ConfigurazioneGlobale() {
        // valori di default, usati se il file manca o e' incompleto
        this.modalita = Modalita.DEMO;
        this.persistenza = TipoPersistenza.FILE;
        this.gui = TipoGui.JAVAFX;
        this.percorsoFileUtenti = "fileData/utenti.txt";
        this.percorsoFilePacchetti = "fileData/pacchetti.txt";
        this.percorsoFilePrenotazioni = "fileData/prenotazioni.txt";
        this.urlDatabase = "jdbc:mysql://localhost:3306/prenotazioneviaggi";
        this.utenteDatabase = "root";
        this.passwordDatabase = "";
        caricaDaFile();
    }

    private static class Holder {
        private static final ConfigurazioneGlobale ISTANZA = new ConfigurazioneGlobale();
    }

    public static ConfigurazioneGlobale getInstance() {
        return Holder.ISTANZA;
    }

    private void caricaDaFile() {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(PERCORSO_FILE)) {
            properties.load(input);

            modalita = Modalita.valueOf(properties.getProperty("modalita", modalita.name()).trim().toUpperCase());
            persistenza = TipoPersistenza.valueOf(properties.getProperty("persistenza", persistenza.name()).trim().toUpperCase());
            gui = TipoGui.valueOf(properties.getProperty("gui", gui.name()).trim().toUpperCase());

            percorsoFileUtenti = properties.getProperty("fileUtenti", percorsoFileUtenti).trim();
            percorsoFilePacchetti = properties.getProperty("filePacchetti", percorsoFilePacchetti).trim();
            percorsoFilePrenotazioni = properties.getProperty("filePrenotazioni", percorsoFilePrenotazioni).trim();
            urlDatabase = properties.getProperty("dbUrl", urlDatabase).trim();
            utenteDatabase = properties.getProperty("dbUtente", utenteDatabase).trim();
            passwordDatabase = properties.getProperty("dbPassword", passwordDatabase).trim();

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "File di configurazione non trovato ({0}), uso i valori di default:"
                    + " modalita=DEMO, persistenza=FILE, gui=JAVAFX.", PERCORSO_FILE);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Valore non valido nel file di configurazione,"
                    + " uso i valori di default rimanenti: {0}", e.getMessage());
        }
    }

    public Modalita getModalita() {
        return modalita;
    }

    public TipoPersistenza getPersistenza() {
        return persistenza;
    }

    public TipoGui getGui() {
        return gui;
    }

    public String getPercorsoFileUtenti() {
        return percorsoFileUtenti;
    }

    public String getPercorsoFilePacchetti() {
        return percorsoFilePacchetti;
    }

    public String getPercorsoFilePrenotazioni() {
        return percorsoFilePrenotazioni;
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

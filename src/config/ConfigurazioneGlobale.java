package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Legge il file config/app.properties per decidere, all'avvio
 * dell'applicazione, quale modalita' (demo/full), quale tipo di
 * persistenza (file/db) e quale GUI (JavaFX/CLI) usare.
 *
 * Pattern Singleton: la configurazione viene letta una sola volta
 * e resta la stessa per tutta la durata dell'esecuzione.
 *
 * Se il file di configurazione manca o e' malformato, l'applicazione
 * non si blocca: usa dei valori di default (DEMO, FILE, JAVAFX) e
 * lo segnala, invece di andare in crash all'avvio.
 */
public class ConfigurazioneGlobale {

    private static final String PERCORSO_FILE = "config/app.properties";

    private static ConfigurazioneGlobale instance;

    private Modalita modalita;
    private TipoPersistenza persistenza;
    private TipoGui gui;

    private ConfigurazioneGlobale() {
        // valori di default, usati se il file manca o e' incompleto
        this.modalita = Modalita.DEMO;
        this.persistenza = TipoPersistenza.FILE;
        this.gui = TipoGui.JAVAFX;
        caricaDaFile();
    }

    public static ConfigurazioneGlobale getInstance() {
        if (instance == null) {
            instance = new ConfigurazioneGlobale();
        }
        return instance;
    }

    private void caricaDaFile() {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(PERCORSO_FILE)) {
            properties.load(input);

            modalita = Modalita.valueOf(properties.getProperty("modalita", modalita.name()).trim().toUpperCase());
            persistenza = TipoPersistenza.valueOf(properties.getProperty("persistenza", persistenza.name()).trim().toUpperCase());
            gui = TipoGui.valueOf(properties.getProperty("gui", gui.name()).trim().toUpperCase());

        } catch (IOException e) {
            System.out.println("File di configurazione non trovato (" + PERCORSO_FILE
                    + "), uso i valori di default: modalita=DEMO, persistenza=FILE, gui=JAVAFX.");
        } catch (IllegalArgumentException e) {
            System.out.println("Valore non valido nel file di configurazione, uso i valori di default rimanenti: "
                    + e.getMessage());
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
}

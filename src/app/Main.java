package app;

import cli.InterfacciaCLI;
import config.ConfigurazioneGlobale;
import config.Modalita;
import config.TipoGui;
import config.TipoPersistenza;
import dao.UtenteDAO;
import dao.UtenteDAOFileSystem;
import dao.UtenteDAOMySQL;
import gui.GuiJavaFXApp;
import model.GestoreUtenti;

/**
 * Punto di ingresso dell'applicazione.
 * Legge la configurazione globale (config/app.properties) e decide:
 * - se restare in memoria (demo-version) o attivare la persistenza (full-version)
 * - quale tipo di persistenza usare (file system o database), se full-version
 * - quale interfaccia avviare (JavaFX o CLI)
 *
 * In questo modo il cambio di modalita' avviene modificando SOLO il file
 * di configurazione, senza toccare il codice, come richiesto dal progetto.
 */
public class Main {

    public static void main(String[] args) {
        ConfigurazioneGlobale config = ConfigurazioneGlobale.getInstance();

        System.out.println("Avvio in modalita' " + config.getModalita()
                + ", persistenza " + config.getPersistenza()
                + ", gui " + config.getGui());

        if (config.getModalita() == Modalita.FULL) {
            UtenteDAO dao = creaDao(config.getPersistenza());
            GestoreUtenti.getInstance().attivaPersistenza(dao);
        }

        if (config.getGui() == TipoGui.JAVAFX) {
            GuiJavaFXApp.avvia(args);
        } else {
            new InterfacciaCLI().avvia();
        }
    }

    private static UtenteDAO creaDao(TipoPersistenza tipo) {
        if (tipo == TipoPersistenza.DB) {
            return new UtenteDAOMySQL("jdbc:mysql://localhost:3306/prenotazioneviaggi", "root", "");
        }
        return new UtenteDAOFileSystem("fileData/utenti.txt");
    }
}

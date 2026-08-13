package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.PersistenzaException;
import model.TipoUtente;
import model.Utente;

// Autore: Davide Scrivano
class TestUtenteDAOFileSystem {

    private static final Logger LOGGER = Logger.getLogger(TestUtenteDAOFileSystem.class.getName());

    // Cartella dentro al progetto (non la temp di sistema, che SonarCloud
    // segnala come "publicly writable directory" e quindi a rischio sicurezza).
    private static final String CARTELLA_TEST = "test-output";
    private static final String PERCORSO_FILE_TEST = CARTELLA_TEST + "/test_utenti.txt";

    @AfterEach
    void pulisci() {
        try {
            Files.deleteIfExists(Paths.get(PERCORSO_FILE_TEST));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Impossibile cancellare il file di test {0}", PERCORSO_FILE_TEST);
        }
    }

    private void preparaCartella() {
        File cartella = new File(CARTELLA_TEST);
        if (!cartella.exists() && !cartella.mkdirs()) {
            fail("Impossibile creare la cartella di test " + CARTELLA_TEST);
        }
    }

    @Test
    void testSalvaECaricaRestituisconoStessiDati() throws PersistenzaException {
        preparaCartella();
        UtenteDAOFileSystem dao = new UtenteDAOFileSystem(PERCORSO_FILE_TEST);

        List<Utente> utenti = new ArrayList<>();
        utenti.add(new Utente(1, "mariorossi", "Mario", "Rossi", "mario@email.com", "pass123"));
        utenti.add(new Utente(2, "annaverdi", "Anna", "Verdi", "anna@email.com", "pass456"));

        dao.salva(utenti);
        List<Utente> caricati = dao.carica();

        assertEquals(2, caricati.size());
        assertEquals("mariorossi", caricati.get(0).getNickname());
        assertEquals("anna@email.com", caricati.get(1).getEmail());
    }

    /**
     * Il ruolo dell'account viene salvato e riletto: senza di lui, in
     * full-version, al secondo avvio l'account agenzia tornerebbe un
     * cliente e il menu dell'agenzia diventerebbe irraggiungibile.
     */
    @Test
    void testRuoloUtenteSopravviveAlSalvataggio() throws PersistenzaException {
        preparaCartella();
        UtenteDAOFileSystem dao = new UtenteDAOFileSystem(PERCORSO_FILE_TEST);

        List<Utente> utenti = new ArrayList<>();
        utenti.add(new Utente(1, "agenzia", "Agenzia", "Viaggi", "ag@email.com", "pass", TipoUtente.AGENZIA));
        utenti.add(new Utente(2, "cliente", "Cliente", "Qualunque", "cl@email.com", "pass"));

        dao.salva(utenti);
        List<Utente> caricati = dao.carica();

        assertTrue(caricati.get(0).isAgenzia());
        assertFalse(caricati.get(1).isAgenzia());
    }

    @Test
    void testCaricaSuFileInesistenteRestituisceListaVuota() throws PersistenzaException {
        UtenteDAOFileSystem dao = new UtenteDAOFileSystem("file_che_non_esiste_12345.txt");

        List<Utente> caricati = dao.carica();

        assertTrue(caricati.isEmpty());
    }

    /**
     * Una riga con un numero di campi diverso da quello atteso viene
     * saltata invece di far fallire l'intero caricamento: un file rovinato
     * a meta' non deve impedire di recuperare i dati buoni. E' una
     * questione di formato del supporto, non una validazione del
     * contenuto, che nella persistenza non deve mai avvenire.
     */
    @Test
    void testRigheMalformateVengonoSaltate() throws PersistenzaException, IOException {
        preparaCartella();
        Files.write(Paths.get(PERCORSO_FILE_TEST),
                List.of("1;valido;Nome;Cognome;v@email.com;pw;CONSUMER",
                        "riga rovinata",
                        "2;altro;Nome;Cognome;a@email.com;pw;AGENZIA"));

        List<Utente> caricati = new UtenteDAOFileSystem(PERCORSO_FILE_TEST).carica();

        assertEquals(2, caricati.size());
        assertEquals("valido", caricati.get(0).getNickname());
        assertTrue(caricati.get(1).isAgenzia());
    }
}

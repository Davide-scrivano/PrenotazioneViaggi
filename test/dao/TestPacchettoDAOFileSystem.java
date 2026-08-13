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
import model.DettagliOfferta;
import model.Pacchetto;
import model.TipoVolo;

// Autore: Davide Scrivano
class TestPacchettoDAOFileSystem {

    private static final Logger LOGGER = Logger.getLogger(TestPacchettoDAOFileSystem.class.getName());

    // Cartella dentro al progetto (non la temp di sistema, che SonarCloud
    // segnala come "publicly writable directory" e quindi a rischio sicurezza).
    private static final String CARTELLA_TEST = "test-output";
    private static final String PERCORSO_FILE_TEST = CARTELLA_TEST + "/test_pacchetti.txt";

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
        PacchettoDAOFileSystem dao = new PacchettoDAOFileSystem(PERCORSO_FILE_TEST);
        List<Pacchetto> daSalvare = new ArrayList<>();
        daSalvare.add(new Pacchetto(1, "Roma", 1000L, 2000L, 350f, 20,
                new DettagliOfferta(4, TipoVolo.DIRETTO)));
        daSalvare.add(new Pacchetto(1000, "Tokyo", 3000L, 4000L, 1500f, 8,
                new DettagliOfferta(5, TipoVolo.CON_SCALO)));

        dao.salva(daSalvare);
        List<Pacchetto> caricati = dao.carica();

        assertEquals(2, caricati.size());
        Pacchetto tokyo = caricati.get(1);
        assertEquals(1000, tokyo.getId());
        assertEquals("Tokyo", tokyo.getDestinazione());
        assertEquals(3000L, tokyo.getDataPartenza());
        assertEquals(1500f, tokyo.getPrezzo());
        assertEquals(8, tokyo.getPostiDisponibili());
        assertEquals(5, tokyo.getStelleHotel());
        assertEquals(TipoVolo.CON_SCALO, tokyo.getTipoVolo());
    }

    @Test
    void testCaricaSenzaFileRestituisceListaVuota() throws PersistenzaException {
        PacchettoDAOFileSystem dao = new PacchettoDAOFileSystem(CARTELLA_TEST + "/file_inesistente.txt");

        assertTrue(dao.carica().isEmpty());
    }

    /**
     * Il salvataggio riscrive l'intero file: e' quello che fa funzionare le
     * rimozioni, perche' un catalogo puo' anche rimpicciolirsi.
     */
    @Test
    void testSalvataggioRiscriveIlFileEGestisceLeRimozioni() throws PersistenzaException {
        preparaCartella();
        PacchettoDAOFileSystem dao = new PacchettoDAOFileSystem(PERCORSO_FILE_TEST);

        List<Pacchetto> due = new ArrayList<>();
        due.add(new Pacchetto(1, "Roma", 1000L, 2000L, 350f, 20, new DettagliOfferta(4, TipoVolo.DIRETTO)));
        due.add(new Pacchetto(2, "Parigi", 1000L, 2000L, 420f, 20, new DettagliOfferta(4, TipoVolo.DIRETTO)));
        dao.salva(due);

        List<Pacchetto> uno = new ArrayList<>();
        uno.add(due.get(0));
        dao.salva(uno);

        assertEquals(1, dao.carica().size());
    }
}

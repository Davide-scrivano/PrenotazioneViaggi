package dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.PersistenzaException;
import model.Utente;

// Autore: Davide Scrivano
public class TestUtenteDAOFileSystem {

    @Test
    public void testSalvaECaricaRestituisconoStessiDati() throws IOException, PersistenzaException {
        File fileTemporaneo = File.createTempFile("test_utenti", ".txt");
        fileTemporaneo.deleteOnExit();

        UtenteDAOFileSystem dao = new UtenteDAOFileSystem(fileTemporaneo.getAbsolutePath());

        List<Utente> utenti = new ArrayList<>();
        utenti.add(new Utente(1, "mariorossi", "Mario", "Rossi", "mario@email.com", "pass123"));
        utenti.add(new Utente(2, "annaverdi", "Anna", "Verdi", "anna@email.com", "pass456"));

        dao.salva(utenti);
        List<Utente> caricati = dao.carica();

        assertEquals(2, caricati.size());
        assertEquals("mariorossi", caricati.get(0).getNickname());
        assertEquals("anna@email.com", caricati.get(1).getEmail());
    }

    @Test
    public void testCaricaSuFileInesistenteRestituisceListaVuota() throws PersistenzaException {
        UtenteDAOFileSystem dao = new UtenteDAOFileSystem("file_che_non_esiste_12345.txt");

        List<Utente> caricati = dao.carica();

        assertTrue(caricati.isEmpty());
    }
}

package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.CredenzialiNonValideException;

// Autore: Davide Scrivano
public class TestGestoreUtenti {

    @Test
    public void testLoginConCredenzialiCorrette() throws CredenzialiNonValideException {
        GestoreUtenti gestore = GestoreUtenti.getInstance();
        gestore.registraUtente("mariorossi", "Mario", "Rossi", "mario@email.com", "pass123");

        Utente loggato = gestore.login("mariorossi", "pass123");

        assertEquals("mariorossi", loggato.getNickname());
        assertEquals(loggato, gestore.getUtenteLoggato());
    }

    @Test
    public void testLoginConPasswordErrataLanciaEccezione() {
        GestoreUtenti gestore = GestoreUtenti.getInstance();
        gestore.registraUtente("annaverdi", "Anna", "Verdi", "anna@email.com", "correcta");

        assertThrows(CredenzialiNonValideException.class,
                () -> gestore.login("annaverdi", "sbagliata"));
    }

    @Test
    public void testRecuperaPasswordUtenteInesistenteLanciaEccezione() {
        GestoreUtenti gestore = GestoreUtenti.getInstance();

        assertThrows(CredenzialiNonValideException.class,
                () -> gestore.recuperaPassword("nonesiste@email.com"));
    }
}

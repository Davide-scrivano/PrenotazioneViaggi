package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.PagamentoRifiutatoException;
import payment.CartaDiCreditoPagamento;
import payment.Pagamento;

// Autore: Davide Scrivano
public class TestGestorePrenotazioni {

    @Test
    public void testCreaPrenotazioneConPagamentoAccettato() throws PagamentoRifiutatoException {
        GestorePrenotazioni gestore = GestorePrenotazioni.getInstance();
        Utente utente = new Utente(100, "utente1", "Nome", "Cognome", "u1@email.com", "pass");
        Pacchetto pacchetto = new Pacchetto(50, "Parigi", 1000L, 2000L, 400f);
        Pagamento pagamento = new CartaDiCreditoPagamento("1234567812345678", "Nome Cognome", "12/28", "123", 400f);

        Prenotazione prenotazione = gestore.creaPrenotazione(utente, pacchetto, pagamento, 1500L);

        assertEquals(StatoPrenotazione.CONFERMATA, prenotazione.getStato());
        assertTrue(utente.getPrenotazioniEffettuate().contains(prenotazione));
    }

    @Test
    public void testCreaPrenotazioneConPagamentoRifiutatoLanciaEccezione() {
        GestorePrenotazioni gestore = GestorePrenotazioni.getInstance();
        Utente utente = new Utente(101, "utente2", "Nome", "Cognome", "u2@email.com", "pass");
        Pacchetto pacchetto = new Pacchetto(51, "Londra", 1000L, 2000L, 400f);
        // cvv non valido (non ha 3 cifre) -> metodoPagamento() restituisce false
        Pagamento pagamento = new CartaDiCreditoPagamento("1234567812345678", "Nome Cognome", "12/28", "12", 400f);

        assertThrows(PagamentoRifiutatoException.class,
                () -> gestore.creaPrenotazione(utente, pacchetto, pagamento, 1500L));
        assertTrue(utente.getPrenotazioniEffettuate().isEmpty());
    }

    @Test
    public void testAnnullaPrenotazioneCambiaStato() throws PagamentoRifiutatoException {
        GestorePrenotazioni gestore = GestorePrenotazioni.getInstance();
        Utente utente = new Utente(102, "utente3", "Nome", "Cognome", "u3@email.com", "pass");
        Pacchetto pacchetto = new Pacchetto(52, "Berlino", 1000L, 2000L, 250f);
        Pagamento pagamento = new CartaDiCreditoPagamento("1234567812345678", "Nome Cognome", "12/28", "321", 250f);

        Prenotazione prenotazione = gestore.creaPrenotazione(utente, pacchetto, pagamento, 1500L);
        boolean risultato = gestore.annullaPrenotazione(prenotazione.getId());

        assertTrue(risultato);
        assertEquals(StatoPrenotazione.ANNULLATA, prenotazione.getStato());
    }
}

package control;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bean.RecensioneBean;
import exceptions.RecensioneNonConsentitaException;
import model.Pacchetto;
import model.Prenotazione;
import model.Recensione;
import model.Utente;

class TestGestoreRecensioni {

    private static final String COGNOME = "Cognome";

    // Intervallo di id riservato a questa classe (il Catalogo e' condiviso).
    private static final int BASE_ID = 6000;

    private GestoreRecensioni gestore;

    @BeforeEach
    void creaGestore() {
        gestore = new GestoreRecensioni();
    }

    /**
     * Il pacchetto va messo a catalogo perche' il Bean ne trasporta solo
     * l'id: e' il Controller Applicativo a risolverlo, esattamente come
     * accade quando la richiesta arriva dalla Boundary.
     */
    private Pacchetto pacchettoACatalogo(int offset, String destinazione, float prezzo) {
        Pacchetto pacchetto = new Pacchetto(BASE_ID + offset, destinazione, 1000L, 2000L, prezzo);
        Catalogo.getInstance().aggiungiPacchetto(pacchetto);
        return pacchetto;
    }

    private RecensioneBean bean(int idPacchetto, int voto, String commento) {
        RecensioneBean dati = new RecensioneBean();
        dati.setIdPacchetto(idPacchetto);
        dati.setVoto(voto);
        dati.setCommento(commento);
        return dati;
    }

    @Test
    void testAggiungiRecensioneSuPacchettoPrenotatoFunziona() throws RecensioneNonConsentitaException {
        Utente utente = new Utente(BASE_ID + 1, "recensore1", "Nome", COGNOME, "r1@email.com", "pass");
        Pacchetto pacchetto = pacchettoACatalogo(1, "Venezia", 300f);
        prenotaFittiziamente(utente, pacchetto);

        Recensione recensione = gestore.aggiungiRecensione(utente, bean(pacchetto.getId(), 5, "Bellissimo viaggio!"));

        assertEquals(5, recensione.getVoto());
        assertEquals(1, gestore.getRecensioniPacchetto(pacchetto.getId()).size());
    }

    @Test
    void testAggiungiRecensioneSuPacchettoNonPrenotatoLanciaEccezione() {
        Utente utente = new Utente(BASE_ID + 2, "recensore2", "Nome", COGNOME, "r2@email.com", "pass");
        Pacchetto pacchetto = pacchettoACatalogo(2, "Milano", 200f);

        assertThrows(RecensioneNonConsentitaException.class,
                () -> gestore.aggiungiRecensione(utente, bean(pacchetto.getId(), 4, "Non ci sono mai stato")));
        assertTrue(gestore.getRecensioniPacchetto(pacchetto.getId()).isEmpty());
    }

    /**
     * Una prenotazione annullata non da' diritto a recensire: il viaggio
     * non e' stato fatto. Senza questo filtro basterebbe prenotare e
     * annullare subito per poter recensire qualunque pacchetto.
     */
    @Test
    void testAggiungiRecensioneDopoAnnullamentoLanciaEccezione() {
        Utente utente = new Utente(BASE_ID + 3, "recensore3", "Nome", COGNOME, "r3@email.com", "pass");
        Pacchetto pacchetto = pacchettoACatalogo(3, "Firenze", 280f);
        Prenotazione prenotazione = prenotaFittiziamente(utente, pacchetto);
        prenotazione.annulla();

        assertThrows(RecensioneNonConsentitaException.class,
                () -> gestore.aggiungiRecensione(utente, bean(pacchetto.getId(), 5, "Prenotato e annullato")));
    }

    @Test
    void testAggiungiRecensioneSuPacchettoInesistenteLanciaEccezione() {
        Utente utente = new Utente(BASE_ID + 4, "recensore4", "Nome", COGNOME, "r4@email.com", "pass");

        assertThrows(RecensioneNonConsentitaException.class,
                () -> gestore.aggiungiRecensione(utente, bean(-999, 4, "Pacchetto che non esiste")));
    }

    @Test
    void testMediaVotiCalcolataCorrettamente() throws RecensioneNonConsentitaException {
        Utente utente1 = new Utente(BASE_ID + 5, "recensore5", "Nome", COGNOME, "r5@email.com", "pass");
        Utente utente2 = new Utente(BASE_ID + 6, "recensore6", "Nome", COGNOME, "r6@email.com", "pass");
        Pacchetto pacchetto = pacchettoACatalogo(5, "Napoli", 250f);
        prenotaFittiziamente(utente1, pacchetto);
        prenotaFittiziamente(utente2, pacchetto);

        gestore.aggiungiRecensione(utente1, bean(pacchetto.getId(), 4, "Buono"));
        gestore.aggiungiRecensione(utente2, bean(pacchetto.getId(), 2, "Cosi' cosi'"));

        assertEquals(3.0, gestore.getMediaVoti(pacchetto.getId()), 0.001);
    }

    @Test
    void testMediaVotiSenzaRecensioniValeZero() {
        Pacchetto pacchetto = pacchettoACatalogo(6, "Bologna", 190f);

        assertEquals(0.0, gestore.getMediaVoti(pacchetto.getId()), 0.001);
    }

    /**
     * Il Bean rifiuta i voti fuori dall'intervallo del form (controllo
     * sintattico); il Model, se costruito direttamente, li riporta
     * comunque all'estremo piu' vicino invece di rifiutare l'intera
     * recensione.
     */
    @Test
    void testVotoFuoriRangeRifiutatoDalBeanERiportatoAiLimitiDalModel() {
        assertNotNull(bean(BASE_ID + 7, 10, "Voto esagerato").validaSintassi());

        Utente utente = new Utente(BASE_ID + 7, "recensore7", "Nome", COGNOME, "r7@email.com", "pass");
        Pacchetto pacchetto = pacchettoACatalogo(7, "Torino", 180f);

        assertEquals(5, new Recensione(1, utente, pacchetto, 10, "Voto esagerato").getVoto());
        assertEquals(1, new Recensione(2, utente, pacchetto, -3, "Voto negativo").getVoto());
    }

    /**
     * Simula una prenotazione gia' effettuata senza passare da
     * GestorePrenotazioni: basta che compaia tra le prenotazioni
     * dell'utente, che e' l'unica cosa che GestoreRecensioni controlla.
     */
    private Prenotazione prenotaFittiziamente(Utente utente, Pacchetto pacchetto) {
        Prenotazione prenotazione = new Prenotazione(0, utente, pacchetto, null, 1300L, 1600L, List.of(utente));
        utente.aggiungiPrenotazione(prenotazione);
        return prenotazione;
    }
}

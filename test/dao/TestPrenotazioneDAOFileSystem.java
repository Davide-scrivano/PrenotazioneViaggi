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

import bean.RegistrazioneBean;
import control.Catalogo;
import control.GestoreUtenti;
import exceptions.PersistenzaException;
import exceptions.RegistrazioneNonConsentitaException;
import model.DatiAnagrafici;
import model.DettagliRicostruzionePrenotazione;
import model.Pacchetto;
import model.Prenotazione;
import model.StatoPrenotazione;
import model.Utente;
import payment.Pagamento;
import payment.PagamentoRegistrato;

// Autore: Davide Scrivano
class TestPrenotazioneDAOFileSystem {

    private static final Logger LOGGER = Logger.getLogger(TestPrenotazioneDAOFileSystem.class.getName());

    // Cartella dentro al progetto (non la temp di sistema, che SonarCloud
    // segnala come "publicly writable directory" e quindi a rischio sicurezza).
    private static final String CARTELLA_TEST = "test-output";
    private static final String PERCORSO_FILE_TEST = CARTELLA_TEST + "/test_prenotazioni.txt";

    // Il Catalogo e' un Singleton condiviso da tutta la suite: questo
    // intervallo di id non si sovrappone a quello usato dalle altre classi.
    private static final int BASE_ID_PACCHETTO = 6000;

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

    private Utente registraUtente(GestoreUtenti gestoreUtenti, int suffisso) throws RegistrazioneNonConsentitaException {
        RegistrazioneBean dati = new RegistrazioneBean();
        dati.setNickname("cliente" + suffisso);
        dati.setNome("Nome");
        dati.setCognome("Cognome");
        dati.setEmail("cliente" + suffisso + "@email.com");
        dati.setPassword("pass123");
        return gestoreUtenti.registraUtente(dati);
    }

    private Pacchetto pacchettoACatalogo(int offset) {
        Pacchetto pacchetto = new Pacchetto(BASE_ID_PACCHETTO + offset, "Oslo", 1000L, 2000L, 500f, 10);
        Catalogo.getInstance().aggiungiPacchetto(pacchetto);
        return pacchetto;
    }

    @Test
    void testSalvaECaricaRestituisconoStessiDati() throws RegistrazioneNonConsentitaException, PersistenzaException {
        preparaCartella();
        GestoreUtenti gestoreUtenti = new GestoreUtenti();
        Utente utente = registraUtente(gestoreUtenti, 1);
        Pacchetto pacchetto = pacchettoACatalogo(1);

        List<Utente> partecipanti = new ArrayList<>();
        partecipanti.add(new Utente(-1, "", "Luca", "Bianchi", "", "", new DatiAnagrafici(12345L, "BNCLCU00A01H501Z")));
        partecipanti.add(new Utente(-2, "", "Sara", "Verdi", "", "", new DatiAnagrafici(54321L, "VRDSRA00A01H501Y")));

        Pagamento pagamento = new PagamentoRegistrato("PayPal", 850f);
        DettagliRicostruzionePrenotazione dettagli =
                new DettagliRicostruzionePrenotazione(1200L, 1900L, 999L, StatoPrenotazione.ANNULLATA);
        Prenotazione prenotazione = new Prenotazione(1, utente, pacchetto, pagamento, dettagli, partecipanti);

        PrenotazioneDAOFileSystem dao = new PrenotazioneDAOFileSystem(PERCORSO_FILE_TEST, gestoreUtenti);
        dao.salva(List.of(prenotazione));

        List<Prenotazione> caricate = dao.carica();

        assertEquals(1, caricate.size());
        Prenotazione ricaricata = caricate.get(0);
        assertEquals(1, ricaricata.getId());
        assertEquals(utente, ricaricata.getDettagliUtente());
        assertEquals(pacchetto, ricaricata.getDettagliPacchetto());
        assertEquals(1200L, ricaricata.getDataPartenzaViaggio());
        assertEquals(1900L, ricaricata.getDataRientroViaggio());
        assertEquals(999L, ricaricata.getDataPrenotazione());
        assertEquals(StatoPrenotazione.ANNULLATA, ricaricata.getStato());
        assertEquals("PayPal", ricaricata.getDettagliPagamento().descrizione());
        assertEquals(850f, ricaricata.getDettagliPagamento().costo());
        assertEquals(2, ricaricata.getNumeroPartecipanti());
        assertEquals("Luca", ricaricata.getDettagliPartecipanti().get(0).getName());
        assertEquals("BNCLCU00A01H501Z", ricaricata.getDettagliPartecipanti().get(0).getCodiceFiscale());
    }

    @Test
    void testCaricaSenzaFileRestituisceListaVuota() throws PersistenzaException {
        PrenotazioneDAOFileSystem dao = new PrenotazioneDAOFileSystem(
                CARTELLA_TEST + "/file_inesistente.txt", new GestoreUtenti());

        assertTrue(dao.carica().isEmpty());
    }

    @Test
    void testCaricaConUtenteNonPiuPresenteSaltaLaRiga()
            throws RegistrazioneNonConsentitaException, PersistenzaException {
        preparaCartella();
        GestoreUtenti gestoreUtentiCheSalva = new GestoreUtenti();
        Utente utente = registraUtente(gestoreUtentiCheSalva, 2);
        Pacchetto pacchetto = pacchettoACatalogo(2);

        List<Utente> partecipanti = new ArrayList<>();
        partecipanti.add(new Utente(-3, "", "Gino", "Neri", "", "", new DatiAnagrafici(1L, "NRIGNO00A01H501X")));

        Prenotazione prenotazione = new Prenotazione(2, utente, pacchetto,
                new PagamentoRegistrato("Carta di credito", 300f), 1000L, 1500L, partecipanti);

        PrenotazioneDAOFileSystem daoScrittura = new PrenotazioneDAOFileSystem(PERCORSO_FILE_TEST, gestoreUtentiCheSalva);
        daoScrittura.salva(List.of(prenotazione));

        // un GestoreUtenti diverso, senza quell'utente registrato
        PrenotazioneDAOFileSystem daoLettura = new PrenotazioneDAOFileSystem(PERCORSO_FILE_TEST, new GestoreUtenti());

        assertTrue(daoLettura.carica().isEmpty());
    }
}

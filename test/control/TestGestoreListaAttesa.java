package control;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bean.ListaAttesaBean;
import bean.PartecipanteBean;
import bean.PrenotazioneBean;
import exceptions.IscrizioneListaAttesaNonConsentitaException;
import exceptions.OperazioneNonConsentitaException;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PagamentoRifiutatoException;
import model.Pacchetto;
import model.Prenotazione;
import model.RichiestaListaAttesa;
import model.Utente;

class TestGestoreListaAttesa {

    private static final String COGNOME = "Cognome";
    private static final long GIORNO = 24L * 60 * 60 * 1000;

    // Intervallo di id riservato a questa classe (il Catalogo e' condiviso).
    private static final int BASE_ID = 7000;

    private final long adesso = System.currentTimeMillis();

    private GestoreListaAttesa gestore;
    private GestorePrenotazioni gestorePrenotazioni;

    @BeforeEach
    void creaGestori() {
        gestore = new GestoreListaAttesa();
        gestorePrenotazioni = new GestorePrenotazioni(gestore);
    }

    private Pacchetto pacchettoACatalogo(int offset, String destinazione, int posti) {
        Pacchetto pacchetto = new Pacchetto(BASE_ID + offset, destinazione,
                adesso, adesso + 90 * GIORNO, 300f, posti);
        Catalogo.getInstance().aggiungiPacchetto(pacchetto);
        return pacchetto;
    }

    private ListaAttesaBean bean(int idPacchetto, int numeroPosti) {
        ListaAttesaBean dati = new ListaAttesaBean();
        dati.setIdPacchetto(idPacchetto);
        dati.setNumeroPosti(numeroPosti);
        return dati;
    }

    private Utente cliente(int id, String nickname) {
        return new Utente(id, nickname, "Nome", COGNOME, nickname + "@email.com", "pass");
    }

    @Test
    void testIscrizioneRifiutataSeIlPacchettoHaGiaPostiSufficienti() {
        Utente utente = cliente(BASE_ID + 1, "attesa1");
        Pacchetto pacchetto = pacchettoACatalogo(1, "Oslo", 10);

        assertThrows(IscrizioneListaAttesaNonConsentitaException.class,
                () -> gestore.iscrivi(utente, bean(pacchetto.getId(), 1)));
    }

    /**
     * Il numero di posti non positivo e' un errore di forma, quindi lo
     * intercetta il Bean prima ancora di disturbare il Controller
     * Applicativo.
     */
    @Test
    void testNumeroPostiNonPositivoRifiutatoDalBean() {
        assertNotNull(bean(BASE_ID + 2, 0).validaSintassi());
        assertNotNull(bean(BASE_ID + 2, -3).validaSintassi());
        assertNull(bean(BASE_ID + 2, 2).validaSintassi());
    }

    @Test
    void testIscrizioneRifiutataSuPacchettoInesistente() {
        Utente utente = cliente(BASE_ID + 3, "attesa3");

        assertThrows(IscrizioneListaAttesaNonConsentitaException.class,
                () -> gestore.iscrivi(utente, bean(-999, 2)));
    }

    @Test
    void testIscrizioneRegistrataQuandoIlPacchettoNonHaPostiSufficienti()
            throws IscrizioneListaAttesaNonConsentitaException {
        Utente utente = cliente(BASE_ID + 4, "attesa4");
        Pacchetto pacchetto = pacchettoACatalogo(4, "Reykjavik", 0);

        RichiestaListaAttesa richiesta = gestore.iscrivi(utente, bean(pacchetto.getId(), 2));

        assertFalse(richiesta.isNotificata());
        assertEquals(2, richiesta.getNumeroPosti());
        assertTrue(gestore.getRichiesteUtente(utente).contains(richiesta));
    }

    @Test
    void testDoppiaIscrizioneAlloStessoPacchettoRifiutata()
            throws IscrizioneListaAttesaNonConsentitaException {
        Utente utente = cliente(BASE_ID + 5, "attesa5");
        Pacchetto pacchetto = pacchettoACatalogo(5, "Bergen", 0);

        gestore.iscrivi(utente, bean(pacchetto.getId(), 2));

        assertThrows(IscrizioneListaAttesaNonConsentitaException.class,
                () -> gestore.iscrivi(utente, bean(pacchetto.getId(), 2)));
    }

    @Test
    void testNessunaNotificaSeIPostiLiberatiNonBastanoPerIlGruppoInAttesa()
            throws IscrizioneListaAttesaNonConsentitaException {
        Utente utente = cliente(BASE_ID + 6, "attesa6");
        Pacchetto pacchetto = pacchettoACatalogo(6, "Tallinn", 0);

        RichiestaListaAttesa richiesta = gestore.iscrivi(utente, bean(pacchetto.getId(), 3));
        // il pacchetto resta a 0 posti disponibili: la notifica non deve scattare
        gestore.postiLiberati(pacchetto, 1);

        assertFalse(richiesta.isNotificata());
    }

    /**
     * Test di integrazione sull'Observer: verifica che GestorePrenotazioni
     * (Subject) avvisi davvero GestoreListaAttesa (Observer) quando una
     * cancellazione libera abbastanza posti, non solo la logica di
     * GestoreListaAttesa presa isolatamente.
     *
     * E' il test che dimostra perche' la lista d'attesa e' stata aggiunta
     * senza modificare il caso d'uso "Cancel a reservation".
     */
    @Test
    void testAnnullamentoPrenotazioneNotificaLaListaDAttesa()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException,
            IscrizioneListaAttesaNonConsentitaException, OperazioneNonConsentitaException {
        Utente clienteConPrenotazione = cliente(BASE_ID + 7, "attesa7");
        Utente clienteInAttesa = cliente(BASE_ID + 8, "attesa8");
        Pacchetto pacchetto = pacchettoACatalogo(7, "Riga", 1);

        // partenza ben oltre i 10 giorni di preavviso: l'annullamento e' consentito
        Prenotazione prenotazione = gestorePrenotazioni.compilaPrenotazione(clienteConPrenotazione,
                beanPrenotazione(pacchetto.getId(), adesso + 20 * GIORNO));
        // ora il pacchetto e' pieno: l'iscrizione alla lista d'attesa e' legittima
        RichiestaListaAttesa richiesta = gestore.iscrivi(clienteInAttesa, bean(pacchetto.getId(), 1));

        assertFalse(richiesta.isNotificata());

        gestorePrenotazioni.annullaPrenotazione(clienteConPrenotazione, prenotazione.getId());

        assertTrue(richiesta.isNotificata());
    }

    private PrenotazioneBean beanPrenotazione(int idPacchetto, long dataPartenzaViaggio) {
        PrenotazioneBean dati = new PrenotazioneBean();
        dati.setIdPacchetto(idPacchetto);
        dati.setDataPartenzaViaggio(dataPartenzaViaggio);
        dati.setSettimaneSoggiorno(1);

        List<PartecipanteBean> partecipanti = new ArrayList<>();
        PartecipanteBean partecipante = new PartecipanteBean();
        partecipante.setNome("Nome");
        partecipante.setCognome(COGNOME);
        partecipanti.add(partecipante);
        dati.setPartecipanti(partecipanti);

        dati.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_CARTA);
        dati.setNumeroCarta("1234567812345678");
        dati.setTitolare("Nome Cognome");
        dati.setScadenza("12/28");
        dati.setCvv("123");
        return dati;
    }
}

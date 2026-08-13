package control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bean.PartecipanteBean;
import bean.PrenotazioneBean;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PagamentoRifiutatoException;
import model.Pacchetto;
import model.Prenotazione;
import model.StatoPrenotazione;
import model.Utente;

/**
 * Verifica il punto d'ingresso pubblico del caso d'uso "Make a reservation":
 * quello che riceve un PrenotazioneBean con soli dati grezzi.
 *
 * E' il test che dimostra il disaccoppiamento richiesto dall'architettura
 * BCE: nessun oggetto del Model viene costruito dal chiamante (che nella
 * realta' e' la Boundary), ma solo un id, del testo e il metodo di
 * pagamento scelto. Pacchetto, Utente-partecipante e Pagamento nascono
 * tutti dentro il Controller Applicativo.
 *
 * Autore: Davide Scrivano
 */
class TestPrenotazioneDaBean {

    private static final long GIORNO = 24L * 60 * 60 * 1000;
    private static final String NUMERO_CARTA_VALIDO = "1234567812345678";
    private static final String SCADENZA_VALIDA = "12/28";

    private GestorePrenotazioni gestore;

    @BeforeEach
    void creaGestore() {
        gestore = new GestorePrenotazioni(new GestoreListaAttesa());
    }

    private PrenotazioneBean beanValido(int idPacchetto, long dataPartenzaViaggio) {
        PrenotazioneBean bean = new PrenotazioneBean();
        bean.setIdPacchetto(idPacchetto);
        bean.setDataPartenzaViaggio(dataPartenzaViaggio);
        bean.setSettimaneSoggiorno(1);

        PartecipanteBean partecipante = new PartecipanteBean();
        partecipante.setNome("Mario");
        partecipante.setCognome("Rossi");
        partecipante.setDataNascita("12/04/1998");
        partecipante.setCodiceFiscale("RSSMRA98D12H501K");
        bean.aggiungiPartecipante(partecipante);

        bean.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_CARTA);
        bean.setNumeroCarta(NUMERO_CARTA_VALIDO);
        bean.setTitolare("Mario Rossi");
        bean.setScadenza(SCADENZA_VALIDA);
        bean.setCvv("123");
        return bean;
    }

    @Test
    void testPrenotazioneDaBeanCostruisceIlModel()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException {
        long adesso = System.currentTimeMillis();
        Pacchetto pacchetto = new Pacchetto(9100, "Lisbona", adesso, adesso + 30 * GIORNO, 300f);
        Catalogo.getInstance().aggiungiPacchetto(pacchetto);

        Utente utente = new Utente(9100, "cliente9100", "Nome", "Cognome", "c9100@email.com", "pass");
        long dataPartenzaViaggio = adesso + 5 * GIORNO;
        int postiPrima = pacchetto.getPostiDisponibili();

        Prenotazione prenotazione = gestore.compilaPrenotazione(utente, beanValido(9100, dataPartenzaViaggio));

        assertEquals(StatoPrenotazione.CONFERMATA, prenotazione.getStato());
        // il Pacchetto e' stato risolto dall'id passato nel Bean
        assertEquals("Lisbona", prenotazione.getDettagliPacchetto().getDestinazione());
        // il partecipante e' stato costruito dal Controller Applicativo
        assertEquals(1, prenotazione.getNumeroPartecipanti());
        assertEquals("Mario", prenotazione.getDettagliPartecipanti().get(0).getName());
        // la data di nascita, testo nel Bean, e' stata interpretata qui
        assertTrue(prenotazione.getDettagliPartecipanti().get(0).getDataNascita() > 0);
        // il Pagamento e' stato creato dalla Factory dentro il controller
        assertNotNull(prenotazione.getDettagliPagamento());
        assertEquals(postiPrima - 1, pacchetto.getPostiDisponibili());
    }

    @Test
    void testPrenotazioneDaBeanConIdInesistenteVieneRifiutata() {
        Utente utente = new Utente(9101, "cliente9101", "Nome", "Cognome", "c9101@email.com", "pass");
        PrenotazioneBean bean = beanValido(-999, System.currentTimeMillis() + 5 * GIORNO);

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.compilaPrenotazione(utente, bean));
    }

    @Test
    void testValidaSintassiSegnalaICampiMancanti() {
        PrenotazioneBean bean = new PrenotazioneBean();
        // Bean vuoto: manca tutto, a partire dalla data.
        assertNotNull(bean.validaSintassi());

        PrenotazioneBean completo = beanValido(1, System.currentTimeMillis());
        assertNull(completo.validaSintassi());

        // Il controllo e' solo sintattico: senza metodo di pagamento il Bean
        // se ne accorge da solo, senza coinvolgere il Controller Applicativo.
        completo.setMetodoPagamento(null);
        assertNotNull(completo.validaSintassi());
    }

    /**
     * Il preventivo non richiede i dati di pagamento ne' i nomi dei
     * partecipanti: serve a mostrare il prezzo mentre il modulo e' ancora
     * in compilazione, quindi pretenderli renderebbe impossibile
     * soddisfare FR-2.
     */
    @Test
    void testValidaSintassiPreventivoNonRichiedeIDatiDiPagamento() {
        PrenotazioneBean bean = new PrenotazioneBean();
        bean.setIdPacchetto(1);
        bean.setDataPartenzaViaggio(System.currentTimeMillis());
        bean.setSettimaneSoggiorno(1);
        bean.aggiungiPartecipante(new PartecipanteBean());

        assertNull(bean.validaSintassiPreventivo());
        // la validazione completa, invece, li pretende
        assertNotNull(bean.validaSintassi());
    }

    @Test
    void testDurataFuoriDaiBlocchiPrevistiVieneRifiutata() {
        PrenotazioneBean bean = beanValido(1, System.currentTimeMillis());
        bean.setSettimaneSoggiorno(3);

        assertNotNull(bean.validaSintassi());
    }
}

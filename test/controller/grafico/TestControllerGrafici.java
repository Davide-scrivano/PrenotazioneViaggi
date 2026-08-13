package controller.grafico;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bean.EsitoLogin;
import bean.EsitoOperazione;
import bean.EsitoPrenotazione;
import bean.EsitoPreventivo;
import bean.EsitoRegistrazione;
import bean.PacchettoBean;
import bean.PacchettoVistaBean;
import bean.PartecipanteBean;
import bean.PrenotazioneBean;
import bean.PrenotazioneVistaBean;
import bean.UtenteVistaBean;
import control.Catalogo;
import control.GestoreListaAttesa;
import control.GestorePrenotazioni;
import control.GestoreRecensioni;
import control.GestoreUtenti;
import model.Pacchetto;

/**
 * Verifica il livello che sta fra la Boundary e il Controller Applicativo.
 *
 * Sono i test che documentano la proprieta' architetturale piu' importante
 * del progetto: quello che il Controller Grafico consegna alla View sono
 * sempre e solo Bean, mai entita' del Model, e le eccezioni di business
 * non arrivano mai fino alla Boundary perche' vengono tradotte qui in un
 * esito con un messaggio.
 *
 * Autore: Davide Scrivano
 */
class TestControllerGrafici {

    private static final long GIORNO = 24L * 60 * 60 * 1000;

    // Intervallo di id riservato a questa classe (il Catalogo e' condiviso).
    private static final int BASE_ID = 8000;

    private final long adesso = System.currentTimeMillis();

    private GestoreUtenti gestoreUtenti;
    private GestorePrenotazioni gestorePrenotazioni;

    private LoginControllerGraficoCLI loginControllerGrafico;
    private RegistrazioneControllerGraficoCLI registrazioneControllerGrafico;
    private CatalogoControllerGraficoCLI catalogoControllerGrafico;
    private PrenotazioneControllerGraficoCLI prenotazioneControllerGrafico;
    private PacchettoControllerGraficoCLI pacchettoControllerGrafico;
    private PrenotazioniPacchettoControllerGraficoCLI prenotazioniPacchettoControllerGrafico;

    /**
     * Stessa composizione fatta da app.Main: un'unica istanza di ciascun
     * Controller Applicativo, condivisa fra i Controller Grafici che la
     * usano, cosi' ad esempio la sessione impostata dal login e' visibile
     * anche al Controller Grafico delle prenotazioni. Catalogo resta un
     * Singleton, quindi non viene creato qui.
     */
    @BeforeEach
    void creaControllerGrafici() {
        gestoreUtenti = new GestoreUtenti();
        gestorePrenotazioni = new GestorePrenotazioni(new GestoreListaAttesa());

        loginControllerGrafico = new LoginControllerGraficoCLI(gestoreUtenti);
        registrazioneControllerGrafico = new RegistrazioneControllerGraficoCLI(gestoreUtenti);
        catalogoControllerGrafico = new CatalogoControllerGraficoCLI(new GestoreRecensioni());
        prenotazioneControllerGrafico = new PrenotazioneControllerGraficoCLI(gestorePrenotazioni, gestoreUtenti);
        pacchettoControllerGrafico = new PacchettoControllerGraficoCLI();
        prenotazioniPacchettoControllerGrafico =
                new PrenotazioniPacchettoControllerGraficoCLI(gestorePrenotazioni);
    }

    private Pacchetto pacchettoACatalogo(int offset, String destinazione, float prezzo, int posti) {
        Pacchetto pacchetto = new Pacchetto(BASE_ID + offset, destinazione,
                adesso, adesso + 90 * GIORNO, prezzo, posti);
        Catalogo.getInstance().aggiungiPacchetto(pacchetto);
        return pacchetto;
    }

    /** Registra e autentica un cliente, restituendo il Bean di vista. */
    private UtenteVistaBean clienteLoggato(String nickname) {
        registrazioneControllerGrafico.gestisciRegistrazione(nickname, "Nome", "Cognome",
                nickname + "@email.com", "pass");
        EsitoLogin esito = loginControllerGrafico.gestisciLogin(nickname, "pass");
        assertTrue(esito.isSuccesso(), esito.getMessaggioErrore());
        return esito.getUtente();
    }

    private PrenotazioneBean beanPrenotazione(int idPacchetto, int numeroPartecipanti) {
        PrenotazioneBean dati = new PrenotazioneBean();
        dati.setIdPacchetto(idPacchetto);
        dati.setDataPartenzaViaggio(adesso + 30 * GIORNO);
        dati.setSettimaneSoggiorno(1);
        for (int i = 1; i <= numeroPartecipanti; i++) {
            PartecipanteBean partecipante = new PartecipanteBean();
            partecipante.setNome("Nome" + i);
            partecipante.setCognome("Cognome");
            dati.aggiungiPartecipante(partecipante);
        }
        dati.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_CARTA);
        dati.setNumeroCarta("1234567812345678");
        dati.setTitolare("Nome Cognome");
        dati.setScadenza("12/28");
        dati.setCvv("123");
        return dati;
    }

    /**
     * L'esito del login non contiene un model.Utente ma un
     * UtenteVistaBean: la View non riceve mai un'entita' del Model, e il
     * ruolo le arriva come semplice booleano invece che come enum.
     */
    @Test
    void testEsitoLoginRestituisceUnBeanENonIlModel() {
        UtenteVistaBean utente = clienteLoggato("grafico1");

        assertEquals("grafico1", utente.getNickname());
        assertEquals("Nome", utente.getNome());
        assertFalse(utente.isAgenzia());
    }

    /**
     * Le eccezioni di business non raggiungono mai la Boundary: il
     * Controller Grafico le intercetta e le traduce in un esito con un
     * messaggio da mostrare.
     */
    @Test
    void testCredenzialiErrateDiventanoUnMessaggioENonUnEccezione() {
        EsitoLogin esito = loginControllerGrafico.gestisciLogin("nonesiste", "password");

        assertFalse(esito.isSuccesso());
        assertNotNull(esito.getMessaggioErrore());
        assertNull(esito.getUtente());
    }

    @Test
    void testCampiVuotiFermatiDalBeanPrimaDelControllerApplicativo() {
        EsitoLogin esito = loginControllerGrafico.gestisciLogin("", "");

        assertFalse(esito.isSuccesso());
        assertNotNull(esito.getMessaggioErrore());
    }

    @Test
    void testRegistrazioneConNicknameGiaInUsoDiventaUnMessaggio() {
        registrazioneControllerGrafico.gestisciRegistrazione("grafico2", "Nome", "Cognome", "g2@email.com", "pass");

        EsitoRegistrazione esito = registrazioneControllerGrafico
                .gestisciRegistrazione("grafico2", "Altro", "Utente", "altro@email.com", "pass");

        assertFalse(esito.isSuccesso());
        assertNotNull(esito.getMessaggioErrore());
    }

    /**
     * Il catalogo consegnato alla View e' fatto di PacchettoVistaBean, con
     * "esaurito" e "votoMedio" gia' calcolati: la Boundary non deve
     * chiamare isDisponibile() sul Model per sapere se mostrare la lista
     * d'attesa.
     */
    @Test
    void testCatalogoConsegnatoComeBeanConDatiGiaCalcolati() {
        pacchettoACatalogo(1, "Anversa", 300f, 5);
        pacchettoACatalogo(2, "Gand", 280f, 0);

        List<PacchettoVistaBean> catalogo = catalogoControllerGrafico.cercaPerDestinazione("Anversa");
        assertEquals(1, catalogo.size());
        PacchettoVistaBean anversa = catalogo.get(0);
        assertEquals("Anversa", anversa.getDestinazione());
        assertFalse(anversa.isEsaurito());
        assertEquals(0, anversa.getNumeroRecensioni());
        assertNotNull(anversa.getDescrizioneVolo());

        PacchettoVistaBean gand = catalogoControllerGrafico.dettaglioPacchetto(BASE_ID + 2);
        assertTrue(gand.isEsaurito());
    }

    @Test
    void testDettaglioDiUnPacchettoInesistenteRestituisceNull() {
        assertNull(catalogoControllerGrafico.dettaglioPacchetto(-999));
    }

    /**
     * FR-2: il prezzo arriva alla View gia' calcolato dal livello di
     * controllo. La Boundary non conosce la formula.
     */
    @Test
    void testPreventivoArrivaGiaCalcolato() {
        Pacchetto pacchetto = pacchettoACatalogo(3, "Bruges", 200f, 10);

        PrenotazioneBean richiesta = beanPrenotazione(pacchetto.getId(), 3);
        richiesta.setSettimaneSoggiorno(2);
        EsitoPreventivo preventivo = prenotazioneControllerGrafico.calcolaPreventivo(richiesta);

        assertTrue(preventivo.isSuccesso());
        assertEquals(1200f, preventivo.getImportoTotale(), 0.001f);
    }

    @Test
    void testPreventivoSuPacchettoEsauritoDiventaUnMessaggio() {
        Pacchetto pacchetto = pacchettoACatalogo(4, "Lovanio", 200f, 0);

        EsitoPreventivo preventivo = prenotazioneControllerGrafico
                .calcolaPreventivo(beanPrenotazione(pacchetto.getId(), 1));

        assertFalse(preventivo.isSuccesso());
        assertNotNull(preventivo.getMessaggioErrore());
    }

    /**
     * Il caso d'uso completo visto dalla Boundary: si entra con un Bean di
     * dati grezzi e si esce con un Bean di vista, senza che nessuna entita'
     * del Model attraversi il confine.
     */
    @Test
    void testPrenotazioneRestituisceUnBeanDiVista() {
        clienteLoggato("grafico3");
        Pacchetto pacchetto = pacchettoACatalogo(5, "Namur", 250f, 10);

        EsitoPrenotazione esito = prenotazioneControllerGrafico
                .creaPrenotazione(beanPrenotazione(pacchetto.getId(), 2));

        assertTrue(esito.isSuccesso(), esito.getMessaggioErrore());
        PrenotazioneVistaBean prenotazione = esito.getPrenotazione();
        assertEquals("Namur", prenotazione.getDestinazione());
        assertEquals(2, prenotazione.getNumeroPartecipanti());
        assertEquals("CONFERMATA", prenotazione.getStato());
        assertTrue(prenotazione.isModificabile());
        assertEquals("Carta di credito", prenotazione.getDescrizionePagamento());
        assertEquals(500f, prenotazione.getImportoPagato(), 0.001f);
    }

    /**
     * La sessione vive nel Controller Applicativo: senza login il
     * Controller Grafico rifiuta l'operazione da solo, e la View non deve
     * passargli nessun Utente.
     */
    @Test
    void testSenzaLoginLaPrenotazioneVieneRifiutata() {
        loginControllerGrafico.logout();
        Pacchetto pacchetto = pacchettoACatalogo(6, "Liegi", 250f, 10);

        EsitoPrenotazione esito = prenotazioneControllerGrafico
                .creaPrenotazione(beanPrenotazione(pacchetto.getId(), 1));

        assertFalse(esito.isSuccesso());
        assertTrue(prenotazioneControllerGrafico.miePrenotazioni().isEmpty());
    }

    /**
     * L'agenzia riceve le prenotazioni del pacchetto come Bean, completi
     * dei dati del cliente, che le servono per organizzare la partenza.
     */
    @Test
    void testPrenotazioniDelPacchettoPerLAgenzia() {
        clienteLoggato("grafico4");
        Pacchetto pacchetto = pacchettoACatalogo(7, "Mons", 250f, 10);
        prenotazioneControllerGrafico.creaPrenotazione(beanPrenotazione(pacchetto.getId(), 3));

        List<PrenotazioneVistaBean> prenotazioni =
                prenotazioniPacchettoControllerGrafico.prenotazioniDelPacchetto(pacchetto.getId());

        assertEquals(1, prenotazioni.size());
        assertEquals("grafico4@email.com", prenotazioni.get(0).getEmailCliente());
        assertEquals(3, prenotazioniPacchettoControllerGrafico.postiVenduti(pacchetto.getId()));
    }

    /**
     * Il Controller Grafico fa validare il Bean prima di invocare il
     * Controller Applicativo: un pacchetto con la data di rientro
     * precedente alla partenza non arriva mai al Catalogo.
     */
    @Test
    void testPacchettoConDatiIncoerentiFermatoDallaValidazione() {
        PacchettoBean dati = new PacchettoBean();
        dati.setDestinazione("Incoerente");
        dati.setDataPartenza(adesso + 30 * GIORNO);
        dati.setDataRientro(adesso + 10 * GIORNO);
        dati.setPrezzo(100f);
        dati.setPosti(5);
        dati.setStelleHotel(3);
        dati.setTipoVolo(PacchettoBean.VOLO_DIRETTO);

        EsitoOperazione esito = pacchettoControllerGrafico.aggiungiPacchetto(dati);

        assertFalse(esito.isSuccesso());
        assertNotNull(esito.getMessaggio());
    }

    @Test
    void testPacchettoSenzaDestinazioneFermatoDallaValidazione() {
        EsitoOperazione esito = pacchettoControllerGrafico.aggiungiPacchetto(new PacchettoBean());

        assertFalse(esito.isSuccesso());
    }
}

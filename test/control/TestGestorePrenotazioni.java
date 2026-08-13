package control;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bean.PartecipanteBean;
import bean.PrenotazioneBean;
import exceptions.OperazioneNonConsentitaException;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PagamentoRifiutatoException;
import model.Pacchetto;
import model.Prenotazione;
import model.StatoPrenotazione;
import model.Utente;

/**
 * Tutti i test entrano dal punto d'ingresso pubblico del caso d'uso, quello
 * che riceve un PrenotazioneBean: e' la stessa strada che percorre la
 * Boundary, quindi il test verifica il caso d'uso per intero invece di un
 * pezzo interno. Nessun oggetto del Model viene costruito dal chiamante,
 * solo un id, del testo e il metodo di pagamento scelto.
 *
 * Il vincolo di preavviso (annulla/modifica) confronta le date con
 * System.currentTimeMillis(), quindi le date dei test sono relative ad
 * "adesso" e non timestamp arbitrari.
 *
 * Autore: Davide Scrivano
 */
class TestGestorePrenotazioni {

    private static final String COGNOME = "Cognome";
    private static final String NUMERO_CARTA_VALIDO = "1234567812345678";
    private static final String SCADENZA_VALIDA = "12/28";
    private static final String TITOLARE = "Nome Cognome";
    private static final long GIORNO = 24L * 60 * 60 * 1000;

    // Intervallo di id riservato a questa classe: il Catalogo e' un
    // Singleton condiviso da tutta la suite, quindi ogni classe di test usa
    // un intervallo diverso per non sovrascrivere i pacchetti delle altre.
    private static final int BASE_ID = 5000;

    private final long adesso = System.currentTimeMillis();

    private GestorePrenotazioni gestore;

    @BeforeEach
    void creaGestore() {
        gestore = new GestorePrenotazioni(new GestoreListaAttesa());
    }

    private Pacchetto pacchettoACatalogo(int offset, String destinazione, long dataPartenza, long dataRientro,
            float prezzo, int posti) {
        Pacchetto pacchetto = new Pacchetto(BASE_ID + offset, destinazione, dataPartenza, dataRientro, prezzo, posti);
        Catalogo.getInstance().aggiungiPacchetto(pacchetto);
        return pacchetto;
    }

    private Pacchetto pacchettoACatalogo(int offset, String destinazione, long dataPartenza, long dataRientro,
            float prezzo) {
        return pacchettoACatalogo(offset, destinazione, dataPartenza, dataRientro, prezzo, 10);
    }

    private PrenotazioneBean bean(int idPacchetto, long dataPartenzaViaggio, int numeroPartecipanti, String cvv) {
        PrenotazioneBean dati = new PrenotazioneBean();
        dati.setIdPacchetto(idPacchetto);
        dati.setDataPartenzaViaggio(dataPartenzaViaggio);
        dati.setSettimaneSoggiorno(1);

        List<PartecipanteBean> partecipanti = new ArrayList<>();
        for (int i = 1; i <= numeroPartecipanti; i++) {
            PartecipanteBean partecipante = new PartecipanteBean();
            partecipante.setNome("Nome" + i);
            partecipante.setCognome(COGNOME);
            partecipanti.add(partecipante);
        }
        dati.setPartecipanti(partecipanti);

        dati.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_CARTA);
        dati.setNumeroCarta(NUMERO_CARTA_VALIDO);
        dati.setTitolare(TITOLARE);
        dati.setScadenza(SCADENZA_VALIDA);
        dati.setCvv(cvv);
        return dati;
    }

    private PrenotazioneBean bean(int idPacchetto, long dataPartenzaViaggio) {
        return bean(idPacchetto, dataPartenzaViaggio, 1, "123");
    }

    private Utente cliente(int id) {
        return new Utente(id, "cliente" + id, "Nome", COGNOME, "c" + id + "@email.com", "pass");
    }

    @Test
    void testCreaPrenotazioneConPagamentoAccettato()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException {
        Pacchetto pacchetto = pacchettoACatalogo(1, "Parigi", adesso, adesso + 30 * GIORNO, 400f);
        Utente utente = cliente(BASE_ID + 1);
        int postiPrima = pacchetto.getPostiDisponibili();
        long dataPartenzaViaggio = adesso + 5 * GIORNO;

        Prenotazione prenotazione = gestore.compilaPrenotazione(utente, bean(pacchetto.getId(), dataPartenzaViaggio));

        assertEquals(StatoPrenotazione.CONFERMATA, prenotazione.getStato());
        assertTrue(utente.getPrenotazioniEffettuate().contains(prenotazione));
        assertEquals(postiPrima - 1, pacchetto.getPostiDisponibili());
        assertEquals(dataPartenzaViaggio, prenotazione.getDataPartenzaViaggio());
        // il rientro non e' scelto dal cliente: e' sempre partenza + durata del blocco
        assertEquals(dataPartenzaViaggio + 7 * GIORNO, prenotazione.getDataRientroViaggio());
        // il Pagamento e' stato creato dalla Factory dentro il Controller Applicativo
        assertNotNull(prenotazione.getDettagliPagamento());
        assertEquals("Carta di credito", prenotazione.getDettagliPagamento().descrizione());
    }

    @Test
    void testCreaPrenotazioneConPagamentoRifiutatoLanciaEccezione() {
        Pacchetto pacchetto = pacchettoACatalogo(2, "Londra", adesso, adesso + 30 * GIORNO, 400f);
        Utente utente = cliente(BASE_ID + 2);
        int postiPrima = pacchetto.getPostiDisponibili();

        // cvv non valido (non ha 3 cifre) -> metodoPagamento() restituisce false
        assertThrows(PagamentoRifiutatoException.class,
                () -> gestore.compilaPrenotazione(utente, bean(pacchetto.getId(), adesso + 5 * GIORNO, 1, "12")));
        assertTrue(utente.getPrenotazioniEffettuate().isEmpty());
        // i posti restano liberi: il pagamento fallisce PRIMA di occuparli
        assertEquals(postiPrima, pacchetto.getPostiDisponibili());
    }

    @Test
    void testCreaPrenotazioneConDurataFuoriFinestraLanciaEccezione() {
        // finestra di soli 5 giorni: un blocco di 1 settimana (7 giorni) non ci sta
        Pacchetto pacchetto = pacchettoACatalogo(3, "Dublino", adesso, adesso + 5 * GIORNO, 300f);
        Utente utente = cliente(BASE_ID + 3);

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.compilaPrenotazione(utente, bean(pacchetto.getId(), adesso)));
        assertTrue(utente.getPrenotazioniEffettuate().isEmpty());
    }

    @Test
    void testCreaPrenotazioneConPartenzaPrimaDellaFinestraLanciaEccezione() {
        Pacchetto pacchetto = pacchettoACatalogo(4, "Amsterdam", adesso + 10 * GIORNO, adesso + 40 * GIORNO, 300f);
        Utente utente = cliente(BASE_ID + 4);

        // la partenza scelta cade prima dell'inizio della finestra del pacchetto
        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.compilaPrenotazione(utente, bean(pacchetto.getId(), adesso)));
        assertTrue(utente.getPrenotazioniEffettuate().isEmpty());
    }

    @Test
    void testCreaPrenotazioneSuPacchettoSenzaPostiLanciaEccezione() {
        Pacchetto pacchetto = pacchettoACatalogo(5, "Madrid", adesso, adesso + 30 * GIORNO, 300f, 0);
        Utente utente = cliente(BASE_ID + 5);

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.compilaPrenotazione(utente, bean(pacchetto.getId(), adesso + 5 * GIORNO)));
        assertTrue(utente.getPrenotazioniEffettuate().isEmpty());
    }

    @Test
    void testCreaPrenotazioneConIdPacchettoInesistenteLanciaEccezione() {
        Utente utente = cliente(BASE_ID + 6);

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.compilaPrenotazione(utente, bean(-999, adesso + 5 * GIORNO)));
    }

    /**
     * La disponibilita' viene verificata PRIMA di calcolare il prezzo e di
     * preparare il pagamento: non ha senso quotare e costruire un addebito
     * per un pacchetto che non ha posti. Qui il gruppo e' piu' grande dei
     * posti rimasti e il caso d'uso si ferma subito.
     */
    @Test
    void testDisponibilitaVerificataPrimaDelPagamento() {
        Pacchetto pacchetto = pacchettoACatalogo(7, "Siviglia", adesso, adesso + 60 * GIORNO, 300f, 2);
        Utente utente = cliente(BASE_ID + 7);

        // cvv volutamente non valido: se il pagamento venisse tentato per
        // primo arriverebbe PagamentoRifiutatoException invece di questa
        PacchettoNonDisponibileException errore = assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.compilaPrenotazione(utente, bean(pacchetto.getId(), adesso + 5 * GIORNO, 5, "12")));
        assertTrue(errore.getMessage().contains("posti disponibili"));
        assertEquals(2, pacchetto.getPostiDisponibili());
    }

    /**
     * FR-2: il prezzo totale mostrato prima della conferma lo calcola il
     * Controller Applicativo interrogando il Model, non la Boundary.
     */
    @Test
    void testPreventivoCalcolatoDalControllerApplicativo() throws PacchettoNonDisponibileException {
        Pacchetto pacchetto = pacchettoACatalogo(8, "Bilbao", adesso, adesso + 200 * GIORNO, 200f);

        PrenotazioneBean dueSettimaneTrePersone = bean(pacchetto.getId(), adesso + 10 * GIORNO, 3, "123");
        dueSettimaneTrePersone.setSettimaneSoggiorno(2);

        // 200 euro a persona a settimana x 2 settimane x 3 persone
        assertEquals(1200f, gestore.calcolaPreventivo(dueSettimaneTrePersone), 0.001f);
    }

    @Test
    void testPreventivoSuPacchettoEsauritoSegnalaLIndisponibilita() {
        Pacchetto pacchetto = pacchettoACatalogo(9, "Porto", adesso, adesso + 60 * GIORNO, 300f, 0);

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.calcolaPreventivo(bean(pacchetto.getId(), adesso + 5 * GIORNO)));
    }

    @Test
    void testAnnullaPrenotazioneCambiaStato()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException, OperazioneNonConsentitaException {
        Pacchetto pacchetto = pacchettoACatalogo(10, "Berlino", adesso, adesso + 60 * GIORNO, 250f);
        Utente utente = cliente(BASE_ID + 10);
        int postiPrima = pacchetto.getPostiDisponibili();

        // partenza ben oltre i 10 giorni di preavviso richiesti: annullabile
        Prenotazione prenotazione = gestore.compilaPrenotazione(utente, bean(pacchetto.getId(), adesso + 20 * GIORNO));
        boolean risultato = gestore.annullaPrenotazione(utente, prenotazione.getId());

        assertTrue(risultato);
        assertEquals(StatoPrenotazione.ANNULLATA, prenotazione.getStato());
        assertTrue(prenotazione.isAnnullata());
        assertEquals(postiPrima, pacchetto.getPostiDisponibili());
    }

    @Test
    void testAnnullaPrenotazioneEntroLimitePreavvisoLanciaEccezione()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException {
        Pacchetto pacchetto = pacchettoACatalogo(11, "Zurigo", adesso, adesso + 30 * GIORNO, 280f);
        Utente utente = cliente(BASE_ID + 11);

        // partenza tra 3 giorni: sotto il limite di preavviso di 10 giorni
        Prenotazione prenotazione = gestore.compilaPrenotazione(utente, bean(pacchetto.getId(), adesso + 3 * GIORNO));

        assertFalse(prenotazione.isModificabile());
        assertThrows(OperazioneNonConsentitaException.class, () -> gestore.annullaPrenotazione(utente, prenotazione.getId()));
        assertEquals(StatoPrenotazione.CONFERMATA, prenotazione.getStato());
    }

    @Test
    void testModificaPrenotazioneLiberaPostiVecchiEOccupaNuovi()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException, OperazioneNonConsentitaException {
        Pacchetto pacchettoVecchio = pacchettoACatalogo(12, "Atene", adesso, adesso + 60 * GIORNO, 300f);
        // il nuovo pacchetto costa meno: il cambio verso uno piu' caro non e' consentito
        Pacchetto pacchettoNuovo = pacchettoACatalogo(13, "Lisbona", adesso, adesso + 60 * GIORNO, 260f);
        Utente utente = cliente(BASE_ID + 12);
        int postiVecchioPrima = pacchettoVecchio.getPostiDisponibili();
        int postiNuovoPrima = pacchettoNuovo.getPostiDisponibili();

        Prenotazione prenotazione = gestore.compilaPrenotazione(utente,
                bean(pacchettoVecchio.getId(), adesso + 20 * GIORNO));
        gestore.modificaPrenotazione(utente, prenotazione.getId(), pacchettoNuovo.getId());

        assertEquals(pacchettoNuovo, prenotazione.getDettagliPacchetto());
        assertEquals(postiVecchioPrima, pacchettoVecchio.getPostiDisponibili());
        assertEquals(postiNuovoPrima - 1, pacchettoNuovo.getPostiDisponibili());
    }

    /**
     * Il pagamento e' gia' stato incassato per intero e il sistema non
     * gestisce conguagli: consentire il passaggio a un pacchetto piu' caro
     * significherebbe regalare la differenza.
     */
    @Test
    void testModificaVersoPacchettoPiuCaroLanciaEccezione()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException {
        Pacchetto pacchettoVecchio = pacchettoACatalogo(14, "Sofia", adesso, adesso + 60 * GIORNO, 200f);
        Pacchetto pacchettoCaro = pacchettoACatalogo(15, "Maldive", adesso, adesso + 60 * GIORNO, 3000f);
        Utente utente = cliente(BASE_ID + 14);

        Prenotazione prenotazione = gestore.compilaPrenotazione(utente,
                bean(pacchettoVecchio.getId(), adesso + 20 * GIORNO));

        PacchettoNonDisponibileException errore = assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.modificaPrenotazione(utente, prenotazione.getId(), pacchettoCaro.getId()));
        assertTrue(errore.getMessage().contains("costa piu'"));
        assertEquals(pacchettoVecchio, prenotazione.getDettagliPacchetto());
    }

    @Test
    void testModificaPrenotazioneSuPacchettoSenzaPostiLanciaEccezione()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException {
        Pacchetto pacchettoVecchio = pacchettoACatalogo(16, "Praga", adesso, adesso + 60 * GIORNO, 240f);
        Pacchetto pacchettoPieno = pacchettoACatalogo(17, "Vienna", adesso, adesso + 60 * GIORNO, 220f, 0);
        Utente utente = cliente(BASE_ID + 16);

        Prenotazione prenotazione = gestore.compilaPrenotazione(utente,
                bean(pacchettoVecchio.getId(), adesso + 20 * GIORNO));

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.modificaPrenotazione(utente, prenotazione.getId(), pacchettoPieno.getId()));
        assertEquals(pacchettoVecchio, prenotazione.getDettagliPacchetto());
    }

    @Test
    void testModificaPrenotazioneSuPacchettoCheNonCopreLeDateLanciaEccezione()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException {
        Pacchetto pacchettoVecchio = pacchettoACatalogo(18, "Cracovia", adesso, adesso + 60 * GIORNO, 230f);
        // finestra del nuovo pacchetto troppo stretta per coprire il viaggio gia' prenotato
        Pacchetto pacchettoStretto = pacchettoACatalogo(19, "Bruxelles",
                adesso + 18 * GIORNO, adesso + 24 * GIORNO, 210f);
        Utente utente = cliente(BASE_ID + 18);

        Prenotazione prenotazione = gestore.compilaPrenotazione(utente,
                bean(pacchettoVecchio.getId(), adesso + 20 * GIORNO));

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestore.modificaPrenotazione(utente, prenotazione.getId(), pacchettoStretto.getId()));
        assertEquals(pacchettoVecchio, prenotazione.getDettagliPacchetto());
    }

    @Test
    void testModificaPrenotazioneEntroLimitePreavvisoLanciaEccezione()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException {
        Pacchetto pacchettoVecchio = pacchettoACatalogo(20, "Malta", adesso, adesso + 30 * GIORNO, 210f);
        Pacchetto pacchettoNuovo = pacchettoACatalogo(21, "Cipro", adesso, adesso + 30 * GIORNO, 200f);
        Utente utente = cliente(BASE_ID + 20);

        // partenza tra 2 giorni: sotto il limite di preavviso di 10 giorni
        Prenotazione prenotazione = gestore.compilaPrenotazione(utente,
                bean(pacchettoVecchio.getId(), adesso + 2 * GIORNO));

        assertThrows(OperazioneNonConsentitaException.class,
                () -> gestore.modificaPrenotazione(utente, prenotazione.getId(), pacchettoNuovo.getId()));
        assertEquals(pacchettoVecchio, prenotazione.getDettagliPacchetto());
    }

    /**
     * Caso d'uso "View package reservations": l'agenzia vede solo le
     * prenotazioni ancora attive, perche' quelle annullate non impegnano
     * piu' posti.
     */
    @Test
    void testPrenotazioniDelPacchettoEscludonoLeAnnullate()
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException, OperazioneNonConsentitaException {
        Pacchetto pacchetto = pacchettoACatalogo(22, "Copenaghen", adesso, adesso + 90 * GIORNO, 400f);
        Utente primo = cliente(BASE_ID + 22);
        Utente secondo = cliente(BASE_ID + 23);

        gestore.compilaPrenotazione(primo, bean(pacchetto.getId(), adesso + 30 * GIORNO));
        Prenotazione daAnnullare = gestore.compilaPrenotazione(secondo, bean(pacchetto.getId(), adesso + 30 * GIORNO));
        gestore.annullaPrenotazione(secondo, daAnnullare.getId());

        assertEquals(1, gestore.getPrenotazioniPacchetto(pacchetto.getId()).size());
    }
}

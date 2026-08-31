package controller.grafico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bean.EsitoCatalogoBean;
import bean.EsitoLoginBean;
import bean.EsitoPrenotazioneBean;
import bean.EsitoPreventivoBean;
import bean.LoginBean;
import bean.PrenotazioneBean;
import control.DatiDiProva;
import dao.DAOFactory;
import dao.filesystem.DAOFactoryFileSystem;
import notifica.NotificatorePrenotazioni;
import payment.FacadePagamento;

class TestPrenotazioneControllerGrafico {

    @TempDir
    Path cartellaDati;

    private PrenotazioneControllerGrafico prenotazioneControllerGrafico;
    private LoginControllerGrafico loginControllerGrafico;

    @BeforeEach
    void preparaSistema() {
        DAOFactory daoFactory = new DAOFactoryFileSystem(cartellaDati.toString());

        prenotazioneControllerGrafico = new PrenotazioneControllerGrafico(daoFactory,
                new FacadePagamento(), new NotificatorePrenotazioni());
        loginControllerGrafico = new LoginControllerGrafico(daoFactory);
    }

    private EsitoLoginBean login(String nickname, String password) {
        LoginBean credenziali = new LoginBean();
        credenziali.setNickname(nickname);
        credenziali.setPassword(password);
        return loginControllerGrafico.effettuaLogin(credenziali);
    }

    @Test
    void ilLoginRiuscitoRestituisceUnBeanEnonUnaEntity() {
        EsitoLoginBean esito = login("mariorossi", "cliente123");

        assertTrue(esito.isSuccesso());
        assertEquals("Mario", esito.getUtente().getNome());
    }

    @Test
    void credenzialiVuoteTornanoUnMessaggioSenzaEccezioni() {
        EsitoLoginBean esito = login("  ", "cliente123");

        assertFalse(esito.isSuccesso());
        assertEquals("Inserisci il nickname.", esito.getMessaggio());
    }

    @Test
    void credenzialiErrateTornanoIlMessaggioDelControl() {
        EsitoLoginBean esito = login("mariorossi", "sbagliata");

        assertFalse(esito.isSuccesso());
        assertTrue(esito.getMessaggio().contains("non corretti"));
    }

    @Test
    void ilCatalogoArrivaAllaBoundaryComeBean() {
        EsitoCatalogoBean esito = prenotazioneControllerGrafico.cercaNelCatalogo(null);

        assertTrue(esito.isSuccesso());
        assertEquals("Catalogo viaggi PrenotazioneViaggi", esito.getCatalogo().getTitolo());
        assertEquals("Roma", esito.getCatalogo().getPacchetti().get(0).getDestinazione());
        assertFalse(esito.getCatalogo().getPacchetti().get(0).isEsaurito());
    }

    @Test
    void unPacchettoInesistenteTornaUnMessaggio() {
        assertFalse(prenotazioneControllerGrafico.dettaglioPacchetto(999).isSuccesso());
    }

    @Test
    void ilPreventivoTornaLimportoCalcolatoDalPacchetto() {
        EsitoPreventivoBean esito =
                prenotazioneControllerGrafico.calcolaPreventivo(DatiDiProva.prenotazioneValida(2));

        assertTrue(esito.isSuccesso());
        assertEquals(700f, esito.getImportoTotale());
    }

    @Test
    void senzaPartecipantiLaPrenotazioneNonParte() {
        EsitoPrenotazioneBean esito =
                prenotazioneControllerGrafico.compilaPrenotazione(DatiDiProva.prenotazioneValida(0));

        assertFalse(esito.isSuccesso());
        assertTrue(esito.getMessaggio().contains("partecipante"));
    }

    @Test
    void nomeDelPartecipanteMancanteVieneSegnalato() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.getPartecipanti().get(0).setNome("");

        assertFalse(prenotazioneControllerGrafico.compilaPrenotazione(dati).isSuccesso());
    }

    @Test
    void dataDiNascitaMalScrittaVieneSegnalata() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.getPartecipanti().get(0).setDataNascita("1990-01-01");

        assertFalse(prenotazioneControllerGrafico.compilaPrenotazione(dati).isSuccesso());
    }

    @Test
    void codiceFiscaleMalScrittoVieneSegnalato() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.getPartecipanti().get(0).setCodiceFiscale("XYZ");

        assertFalse(prenotazioneControllerGrafico.compilaPrenotazione(dati).isSuccesso());
    }

    @Test
    void metodoDiPagamentoNonSceltoVieneSegnalato() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.getDatiPagamento().setMetodoPagamento(null);

        assertFalse(prenotazioneControllerGrafico.compilaPrenotazione(dati).isSuccesso());
    }

    @Test
    void iDatiDelViaggioSonoVerificabiliPrimaDelPagamento() {
        assertTrue(prenotazioneControllerGrafico
                .verificaDatiViaggio(DatiDiProva.prenotazioneValida(1)).isSuccesso());
    }

    @Test
    void iPostiInsufficientiDiventanoUnMessaggioPerLaView() {
        EsitoPrenotazioneBean esito =
                prenotazioneControllerGrafico.compilaPrenotazione(DatiDiProva.prenotazioneValida(11));

        assertFalse(esito.isSuccesso());
        assertTrue(esito.getMessaggio().contains("posti"));
    }

    @Test
    void ilPagamentoRifiutatoDiventaUnMessaggioPerLaView() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.setDatiPagamento(DatiDiProva.cartaRifiutataDalCircuito());

        EsitoPrenotazioneBean esito = prenotazioneControllerGrafico.compilaPrenotazione(dati);

        assertFalse(esito.isSuccesso());
        assertTrue(esito.getMessaggio().contains("pagamento"));
    }

    @Test
    void laConfermaTornaAllaBoundaryComeBeanDiVista() {
        EsitoPrenotazioneBean esito =
                prenotazioneControllerGrafico.compilaPrenotazione(DatiDiProva.prenotazioneValida(2));

        assertTrue(esito.isSuccesso());
        assertEquals("Roma", esito.getPrenotazione().getDestinazione());
        assertEquals(2, esito.getPrenotazione().getPartecipanti().size());
        assertEquals(700f, esito.getPrenotazione().getImportoTotale());
        assertEquals("Nome1 Cognome1", esito.getPrenotazione().getPartecipanti().get(0).getNominativo());
    }
}

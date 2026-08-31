package bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import control.DatiDiProva;

class TestValidazioneBean {

    @Test
    void credenzialiCompleteSonoSintatticamenteValide() {
        LoginBean credenziali = new LoginBean();
        credenziali.setNickname("mariorossi");
        credenziali.setPassword("cliente123");

        assertNull(credenziali.validaSintassi());
    }

    @Test
    void nicknameVuotoVieneSegnalato() {
        LoginBean credenziali = new LoginBean();
        credenziali.setNickname("   ");
        credenziali.setPassword("cliente123");

        assertEquals("Inserisci il nickname.", credenziali.validaSintassi());
    }

    @Test
    void passwordMancanteVieneSegnalata() {
        LoginBean credenziali = new LoginBean();
        credenziali.setNickname("mariorossi");

        assertEquals("Inserisci la password.", credenziali.validaSintassi());
    }

    @Test
    void unaPrenotazioneCompletaPassaTuttiIControlli() {
        assertNull(DatiDiProva.prenotazioneValida(2).validaSintassi());
    }

    @Test
    void senzaDataDiPartenzaIlViaggioNonEvalido() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.setDataPartenzaViaggio(0L);

        assertEquals("Seleziona la data di partenza.", dati.validaSintassiViaggio());
    }

    @Test
    void unaDurataDiversaDaUnoODueSettimaneNonEvalida() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.setSettimaneSoggiorno(3);

        assertNotNull(dati.validaSintassiViaggio());
    }

    @Test
    void senzaPartecipantiIlViaggioNonEvalido() {
        assertTrue(DatiDiProva.prenotazioneValida(0).validaSintassiViaggio().contains("partecipante"));
    }

    @Test
    void ilMessaggioIndicaQualePartecipanteEincompleto() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(3);
        dati.getPartecipanti().get(2).setCognome("");

        assertEquals("Inserisci il cognome del partecipante 3.", dati.validaSintassiPartecipanti());
    }

    @Test
    void dataDiNascitaFacoltativaMaSeCompilataDeveEssereCorretta() {
        PartecipanteBean partecipante = DatiDiProva.partecipante("Mario", "Rossi");

        partecipante.setDataNascita("");
        assertNull(partecipante.validaSintassi(1));

        partecipante.setDataNascita("1990-01-01");
        assertNotNull(partecipante.validaSintassi(1));
    }

    @Test
    void codiceFiscaleFacoltativoMaSeCompilatoDeveEssereCorretto() {
        PartecipanteBean partecipante = DatiDiProva.partecipante("Mario", "Rossi");

        partecipante.setCodiceFiscale("RSSMRA90A01H501U");
        assertNull(partecipante.validaSintassi(1));

        partecipante.setCodiceFiscale("XYZ");
        assertNotNull(partecipante.validaSintassi(1));
    }

    @Test
    void ilMetodoDiPagamentoDeveEssereScelto() {
        assertEquals("Seleziona un metodo di pagamento.", new DatiPagamentoBean().validaSintassi());
    }

    @Test
    void laCartaRichiedeTuttiISuoiCampi() {
        DatiPagamentoBean dati = DatiDiProva.cartaValida();
        assertNull(dati.validaSintassi());

        dati.setCvv("");
        assertEquals("Inserisci il CVV.", dati.validaSintassi());
    }

    @Test
    void paypalRichiedeEmailEpassword() {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_PAYPAL);
        assertEquals("Inserisci l'email PayPal.", dati.validaSintassi());

        dati.setEmailPaypal("mario@rossi.it");
        assertEquals("Inserisci la password PayPal.", dati.validaSintassi());

        dati.setPasswordPaypal("segreta");
        assertNull(dati.validaSintassi());
    }

    @Test
    void laValidazioneCompletaArrivaFinoAlPagamento() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.getDatiPagamento().setNumeroCarta("");

        assertNull(dati.validaSintassiPartecipanti());
        assertEquals("Inserisci il numero della carta.", dati.validaSintassi());
    }

    @Test
    void unCvvDiLunghezzaSbagliataVieneSegnalato() {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_CARTA);
        dati.setNumeroCarta("4111111111111111");
        dati.setTitolare("Mario Rossi");
        dati.setScadenza("12/30");
        dati.setCvv("12");

        assertNotNull(dati.validaSintassi());
    }

    @Test
    void unaEmailPaypalSenzaChiocciolaVieneSegnalata() {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_PAYPAL);
        dati.setEmailPaypal("mario.rossi.it");
        dati.setPasswordPaypal("segreta");

        assertNotNull(dati.validaSintassi());
    }
}

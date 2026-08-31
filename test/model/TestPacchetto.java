package model;

import model.valori.DettagliOfferta;
import model.valori.DurataViaggio;
import model.valori.PeriodoViaggio;
import model.valori.TipoVolo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exceptions.PacchettoNonDisponibileException;

class TestPacchetto {

    private static final long GIORNO = DurataViaggio.giorniInMillisecondi(1);

    private Pacchetto pacchetto;

    @BeforeEach
    void preparaPacchetto() {
        pacchetto = new Pacchetto(1, "Roma", new PeriodoViaggio(0L, 30 * GIORNO), 350f, 10,
                new DettagliOfferta(4, TipoVolo.DIRETTO));
    }

    @Test
    void ilPrezzoDipendeDaPersoneEsettimane() {
        assertEquals(350f, pacchetto.calcolaPrezzoTotale(1, DurataViaggio.UNA_SETTIMANA));
        assertEquals(2100f, pacchetto.calcolaPrezzoTotale(3, DurataViaggio.DUE_SETTIMANE));
    }

    @Test
    void unaRichiestaCompatibileEprenotabile() {
        assertDoesNotThrow(() -> pacchetto.verificaPrenotabilita(10, new PeriodoViaggio(2 * GIORNO, 9 * GIORNO)));
    }

    @Test
    void ilPacchettoSpiegaSeIPostiNonBastano() {
        PacchettoNonDisponibileException errore = assertThrows(PacchettoNonDisponibileException.class,
                () -> pacchetto.verificaPrenotabilita(11, new PeriodoViaggio(2 * GIORNO, 9 * GIORNO)));

        assertTrue(errore.getMessage().contains("posti"));
        assertTrue(errore.getMessage().contains("Roma"));
    }

    @Test
    void ilPacchettoSpiegaSeLeDateSonoFuoriPeriodo() {
        PacchettoNonDisponibileException errore = assertThrows(PacchettoNonDisponibileException.class,
                () -> pacchetto.verificaPrenotabilita(2, new PeriodoViaggio(25 * GIORNO, 40 * GIORNO)));

        assertTrue(errore.getMessage().contains("date"));
    }

    @Test
    void occupareIPostiRiduceLaDisponibilita() {
        pacchetto.occupaPosti(4);
        assertEquals(6, pacchetto.getPostiDisponibili());
    }

    @Test
    void unPacchettoSenzaPostiRisultaEsaurito() {
        assertFalse(pacchetto.isEsaurito());
        pacchetto.occupaPosti(10);
        assertTrue(pacchetto.isEsaurito());
    }

    @Test
    void ilPeriodoSiRicavaDaPartenzaEdurata() {
        PeriodoViaggio periodo = PeriodoViaggio.daPartenzaEDurata(GIORNO, DurataViaggio.DUE_SETTIMANE);

        assertEquals(GIORNO, periodo.getDataPartenza());
        assertEquals(15 * GIORNO, periodo.getDataRientro());
    }

    @Test
    void leStelleFuoriScalaVengonoRiportateNellIntervallo() {
        assertEquals(5, new DettagliOfferta(9, TipoVolo.DIRETTO).getStelleHotel());
        assertEquals(1, new DettagliOfferta(-2, TipoVolo.DIRETTO).getStelleHotel());
    }
}

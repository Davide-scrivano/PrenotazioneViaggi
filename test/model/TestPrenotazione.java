package model;

import model.valori.DatiAnagrafici;
import model.valori.DettagliOfferta;
import model.valori.DurataViaggio;
import model.valori.PeriodoViaggio;
import model.valori.TipoUtente;
import model.valori.TipoVolo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestPrenotazione {

    private static final long GIORNO = DurataViaggio.giorniInMillisecondi(1);

    private Utente cliente;
    private Pacchetto pacchetto;
    private Pagamento pagamento;
    private PeriodoViaggio periodo;
    private List<Partecipante> partecipanti;

    @BeforeEach
    void preparaDati() {
        cliente = new Utente(1, "mariorossi", "Mario", "Rossi", "mario@rossi.it", "segreta", TipoUtente.CONSUMER);
        pacchetto = new Pacchetto(1, "Roma", new PeriodoViaggio(0L, 30 * GIORNO), 350f, 10,
                new DettagliOfferta(4, TipoVolo.DIRETTO));
        pagamento = new Pagamento(1, "Carta di credito", 700f, "AUTH-PV1");
        periodo = new PeriodoViaggio(2 * GIORNO, 9 * GIORNO);
        partecipanti = List.of(
                new Partecipante(1, "Mario", "Rossi", new DatiAnagrafici(0L, "")),
                new Partecipante(2, "Anna", "Verdi", new DatiAnagrafici(12345L, "RSSMRA90A01H501U")));
    }

    private Prenotazione prenotazione() {
        return cliente.prenota(7, pacchetto, pagamento, periodo, partecipanti);
    }

    @Test
    void laPrenotazioneRispondeSenzaEsporreLaPropriaReteDiOggetti() {
        Prenotazione prenotazione = prenotazione();

        assertEquals(2, prenotazione.getNumeroPartecipanti());
        assertEquals("Roma", prenotazione.getDestinazione());
        assertEquals(700f, prenotazione.getImportoTotale());
        assertEquals("Mario Rossi", prenotazione.getNominativoCliente());
        assertEquals("mario@rossi.it", prenotazione.getEmailCliente());
        assertTrue(prenotazione.getDescrizionePagamento().contains("Carta di credito"));
    }

    @Test
    void ilPartecipanteSaQualiDatiFacoltativiPossiede() {
        Partecipante senzaAnagrafica = partecipanti.get(0);
        Partecipante conAnagrafica = partecipanti.get(1);

        assertFalse(senzaAnagrafica.haDataNascita());
        assertFalse(senzaAnagrafica.haCodiceFiscale());
        assertTrue(conAnagrafica.haDataNascita());
        assertTrue(conAnagrafica.haCodiceFiscale());
        assertEquals("Anna Verdi", conAnagrafica.nominativo());
    }
}

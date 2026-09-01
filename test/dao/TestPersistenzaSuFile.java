package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dao.filesystem.PacchettoDAOFileSystem;
import dao.filesystem.PagamentoDAOFileSystem;
import dao.filesystem.PartecipanteDAOFileSystem;
import dao.filesystem.PrenotazioneDAOFileSystem;
import dao.filesystem.UtenteDAOFileSystem;
import exceptions.PersistenzaException;
import model.valori.DatiAnagrafici;
import model.valori.DurataViaggio;
import model.Pacchetto;
import model.Pagamento;
import model.Partecipante;
import model.valori.PeriodoViaggio;
import model.Prenotazione;
import model.Utente;

class TestPersistenzaSuFile {

    @TempDir
    Path cartella;

    private UtenteDAO utenteDAO;
    private PacchettoDAO pacchettoDAO;
    private PagamentoDAO pagamentoDAO;
    private PartecipanteDAO partecipanteDAO;
    private PrenotazioneDAO prenotazioneDAO;

    @BeforeEach
    void preparaArchivi() {
        utenteDAO = new UtenteDAOFileSystem(percorso("utenti.dat"));
        pacchettoDAO = new PacchettoDAOFileSystem(percorso("pacchetti.dat"));
        pagamentoDAO = new PagamentoDAOFileSystem(percorso("pagamenti.dat"));
        partecipanteDAO = new PartecipanteDAOFileSystem(percorso("partecipanti.dat"));
        prenotazioneDAO = new PrenotazioneDAOFileSystem(percorso("prenotazioni.dat"),
                utenteDAO, pacchettoDAO, pagamentoDAO, partecipanteDAO);
    }

    private String percorso(String nomeFile) {
        return cartella.resolve(nomeFile).toString();
    }

    @Test
    void alPrimoAvvioGliArchiviVengonoPopolati() throws PersistenzaException {
        assertEquals(8, pacchettoDAO.trovaTutti().size());
        assertNotNull(utenteDAO.trovaPerNickname("mariorossi"));
    }

    @Test
    void ilNumeroDiPostiSopravviveAllaRiscrittura() throws PersistenzaException {
        Pacchetto pacchetto = pacchettoDAO.trovaPerId(1);
        pacchetto.occupaPosti(3);
        pacchettoDAO.aggiorna(pacchetto);

        PacchettoDAO riletto = new PacchettoDAOFileSystem(percorso("pacchetti.dat"));
        assertEquals(7, riletto.trovaPerId(1).getPostiDisponibili());
        assertEquals(8, riletto.trovaTutti().size());
    }

    @Test
    void gliIdSonoProgressiviEnonSiRipetono() throws PersistenzaException {
        assertEquals(1, pagamentoDAO.prossimoId());
        assertEquals(2, pagamentoDAO.prossimoId());
        assertEquals(1, prenotazioneDAO.prossimoId());
    }

    @Test
    void laPrenotazioneViaggiaInteraFraScritturaERilettura() throws PersistenzaException {
        Prenotazione salvata = costruisciPrenotazione();
        prenotazioneDAO.inserisci(salvata);

        Prenotazione riletta = prenotazioneDAO.trovaPerId(salvata.getId());

        assertNotNull(riletta);
        assertEquals("Roma", riletta.getDestinazione());
        assertEquals(2, riletta.getNumeroPartecipanti());
        assertEquals(700f, riletta.getImportoTotale());
        assertEquals("Anna Verdi", riletta.getPartecipanti().get(1).nominativo());
        assertEquals(salvata.getDataPrenotazione(), riletta.getDataPrenotazione());
    }

    @Test
    void unaPrenotazioneInesistenteNonEsiste() throws PersistenzaException {
        assertNull(prenotazioneDAO.trovaPerId(42));
    }

    private Prenotazione costruisciPrenotazione() throws PersistenzaException {
        Utente cliente = utenteDAO.trovaPerId(1);
        Pacchetto pacchetto = pacchettoDAO.trovaPerId(1);
        Pagamento pagamento = new Pagamento(pagamentoDAO.prossimoId(), "Carta di credito", 700f, "AUTH-PV1");

        List<Partecipante> partecipanti = List.of(
                new Partecipante(partecipanteDAO.prossimoId(), "Mario", "Rossi", new DatiAnagrafici(0L, "")),
                new Partecipante(partecipanteDAO.prossimoId(), "Anna", "Verdi", new DatiAnagrafici(12345L, "ABC")));

        PeriodoViaggio periodo = PeriodoViaggio.daPartenzaEDurata(
                pacchetto.getDataPartenzaDisponibilita(), DurataViaggio.UNA_SETTIMANA);
        return cliente.prenota(prenotazioneDAO.prossimoId(), pacchetto, pagamento, periodo, partecipanti);
    }
}

package control;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bean.PrenotazioneBean;
import dao.DAOFactory;
import dao.PacchettoDAO;
import dao.PrenotazioneDAO;
import dao.filesystem.DAOFactoryFileSystem;
import exceptions.ApplicazioneException;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PagamentoRifiutatoException;
import model.Pacchetto;
import model.Prenotazione;
import notifica.NotificatorePrenotazioni;
import payment.FacadePagamento;

class TestGestorePrenotazioni {

    @TempDir
    Path cartellaDati;

    private DAOFactory daoFactory;
    private GestorePrenotazioni gestorePrenotazioni;
    private List<Prenotazione> notificate;

    @BeforeEach
    void preparaSistema() {
        daoFactory = new DAOFactoryFileSystem(cartellaDati.toString());
        notificate = new ArrayList<>();

        NotificatorePrenotazioni notificatore = new NotificatorePrenotazioni();
        notificatore.registraOsservatore(() -> notificate.add(notificatore.getPrenotazione()));

        gestorePrenotazioni = new GestorePrenotazioni(daoFactory,
                new FacadePagamento(),
                notificatore);
    }

    @Test
    void prenotazioneCompletataConSuccesso() throws ApplicazioneException {
        Prenotazione prenotazione = gestorePrenotazioni.compilaPrenotazione(DatiDiProva.prenotazioneValida(2));

        assertEquals(2, prenotazione.getNumeroPartecipanti());
        assertEquals("Roma", prenotazione.getDestinazione());
        assertEquals(DatiDiProva.ID_UTENTE_MARIO, prenotazione.getCliente().getId());
    }

    @Test
    void ilPrezzoTotaleTieneContoDiPersoneESettimane() throws ApplicazioneException {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(3);
        dati.setSettimaneSoggiorno(2);

        assertEquals(350f * 2 * 3, gestorePrenotazioni.calcolaPreventivo(dati));
    }

    @Test
    void iPostiVengonoScalatiDalPacchetto() throws ApplicazioneException {
        PacchettoDAO pacchettoDAO = daoFactory.creaPacchettoDAO();
        int postiIniziali = pacchettoDAO.trovaPerId(DatiDiProva.ID_PACCHETTO_ROMA).getPostiDisponibili();

        gestorePrenotazioni.compilaPrenotazione(DatiDiProva.prenotazioneValida(4));

        Pacchetto pacchetto = pacchettoDAO.trovaPerId(DatiDiProva.ID_PACCHETTO_ROMA);
        assertEquals(postiIniziali - 4, pacchetto.getPostiDisponibili());
    }

    @Test
    void laPrenotazioneVieneResaPersistente() throws ApplicazioneException {
        Prenotazione prenotazione = gestorePrenotazioni.compilaPrenotazione(DatiDiProva.prenotazioneValida(1));

        PrenotazioneDAO prenotazioneDAO = daoFactory.creaPrenotazioneDAO();
        assertNotNull(prenotazioneDAO.trovaPerId(prenotazione.getId()));
        assertNotNull(daoFactory.creaPagamentoDAO().trovaPerId(prenotazione.getPagamento().getId()));
        assertEquals(1, daoFactory.creaPartecipanteDAO().trovaPerPrenotazione(prenotazione.getId()).size());
    }

    @Test
    void gliOsservatoriVengonoAvvisatiDellaConferma() throws ApplicazioneException {
        Prenotazione prenotazione = gestorePrenotazioni.compilaPrenotazione(DatiDiProva.prenotazioneValida(1));

        assertEquals(1, notificate.size());
        assertEquals(prenotazione.getId(), notificate.get(0).getId());
    }

    @Test
    void postiInsufficientiBloccanoLaPrenotazione() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(11);

        PacchettoNonDisponibileException errore = assertThrows(PacchettoNonDisponibileException.class,
                () -> gestorePrenotazioni.compilaPrenotazione(dati));
        assertTrue(errore.getMessage().contains("posti"));
    }

    @Test
    void dateFuoriDalPeriodoBloccanoLaPrenotazione() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.setDataPartenzaViaggio(DatiDiProva.fraGiorni(2));

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestorePrenotazioni.compilaPrenotazione(dati));
    }

    @Test
    void pacchettoInesistenteBloccaLaPrenotazione() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.setIdPacchetto(999);

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestorePrenotazioni.compilaPrenotazione(dati));
    }

    @Test
    void pagamentoRifiutatoLasciaIPostiInvariati() throws ApplicazioneException {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(2);
        dati.setDatiPagamento(DatiDiProva.cartaRifiutataDalCircuito());

        PacchettoDAO pacchettoDAO = daoFactory.creaPacchettoDAO();
        int postiIniziali = pacchettoDAO.trovaPerId(DatiDiProva.ID_PACCHETTO_ROMA).getPostiDisponibili();

        assertThrows(PagamentoRifiutatoException.class, () -> gestorePrenotazioni.compilaPrenotazione(dati));
        assertEquals(postiIniziali, pacchettoDAO.trovaPerId(DatiDiProva.ID_PACCHETTO_ROMA).getPostiDisponibili());
        assertTrue(notificate.isEmpty());
    }

    @Test
    void ilPacchettoSiPuoSelezionarePrimaDiPrenotare() throws ApplicazioneException {
        assertEquals("Roma", gestorePrenotazioni.selezionaPacchetto(DatiDiProva.ID_PACCHETTO_ROMA)
                .getDestinazione());
    }

    @Test
    void laDisponibilitaSiVerificaSenzaArrivareAlPagamento() {
        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestorePrenotazioni.verificaDisponibilita(DatiDiProva.prenotazioneValida(11)));
    }

    @Test
    void unPeriodoFuoriFinestraSiScopreAlPassoCinque() {
        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(1);
        dati.setDataPartenzaViaggio(DatiDiProva.fraGiorni(2));

        assertThrows(PacchettoNonDisponibileException.class,
                () -> gestorePrenotazioni.verificaDisponibilita(dati));
    }

    @Test
    void unaRichiestaValidaSuperaLaVerificaDiDisponibilita() {
        assertDoesNotThrow(() -> gestorePrenotazioni.verificaDisponibilita(DatiDiProva.prenotazioneValida(3)));
    }
}

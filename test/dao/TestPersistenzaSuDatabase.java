package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bean.PrenotazioneBean;
import control.DatiDiProva;
import control.GestoreCatalogo;
import control.GestorePrenotazioni;
import dao.database.DAOFactoryDatabase;
import exceptions.ApplicazioneException;
import model.Catalogo;
import model.Pacchetto;
import model.Partecipante;
import model.Prenotazione;
import model.Utente;
import notifica.NotificatorePrenotazioni;
import payment.FacadePagamento;

class TestPersistenzaSuDatabase {

    private DAOFactory daoFactory;

    @BeforeEach
    void preparaSistema() {
        assumeTrue(databaseRaggiungibile(), "database non raggiungibile: prova saltata");
        daoFactory = new DAOFactoryDatabase();
    }

    private static boolean databaseRaggiungibile() {
        try {
            new DAOFactoryDatabase().creaUtenteDAO().trovaPerId(DatiDiProva.ID_UTENTE_MARIO);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    void gliUtentiInizialiSonoSulDatabase() throws ApplicazioneException {
        Utente mario = daoFactory.creaUtenteDAO().trovaPerNickname("mariorossi");

        assertNotNull(mario);
        assertEquals("Mario", mario.getNome());
        assertTrue(mario.credenzialiValide("cliente123"));
    }

    @Test
    void ilCatalogoSiRicostruisceDallaCooperazioneFraDao() throws ApplicazioneException {
        Catalogo catalogo = new GestoreCatalogo(daoFactory).consultaCatalogo();

        assertEquals("Catalogo viaggi PrenotazioneViaggi", catalogo.getTitolo());
        assertEquals(8, catalogo.pacchettiDisponibili().size());
        assertNotNull(catalogo.trovaPacchetto(DatiDiProva.ID_PACCHETTO_ROMA));
    }

    @Test
    void laPrenotazioneAttraversaTuttoIlCasoDusoEfinisceInPersistenza() throws ApplicazioneException {
        GestorePrenotazioni gestore = new GestorePrenotazioni(daoFactory, new FacadePagamento(),
                new NotificatorePrenotazioni());

        PacchettoDAO pacchettoDAO = daoFactory.creaPacchettoDAO();
        int postiPrima = pacchettoDAO.trovaPerId(DatiDiProva.ID_PACCHETTO_ROMA).getPostiDisponibili();
        assumeTrue(postiPrima >= 2, "posti esauriti sul pacchetto di prova");

        PrenotazioneBean dati = DatiDiProva.prenotazioneValida(2);
        Prenotazione prenotazione = gestore.compilaPrenotazione(dati);

        assertNotNull(prenotazione);
        assertEquals(2, prenotazione.getNumeroPartecipanti());

        Prenotazione riletta = daoFactory.creaPrenotazioneDAO().trovaPerId(prenotazione.getId());
        assertNotNull(riletta);
        assertEquals(prenotazione.getDestinazione(), riletta.getDestinazione());
        assertEquals(prenotazione.getImportoTotale(), riletta.getImportoTotale());

        List<Partecipante> partecipanti = riletta.getPartecipanti();
        assertEquals(2, partecipanti.size());

        Pacchetto dopo = pacchettoDAO.trovaPerId(DatiDiProva.ID_PACCHETTO_ROMA);
        assertEquals(postiPrima - 2, dopo.getPostiDisponibili());
    }
}

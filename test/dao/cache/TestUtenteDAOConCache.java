package dao.cache;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dao.UtenteDAO;
import exceptions.PersistenzaException;
import model.Utente;

class TestUtenteDAOConCache {

    /**
     * Fake UtenteDAO che conta quante volte viene interrogata la
     * persistenza vera, per poter verificare che la cache eviti letture
     * ripetute. Non si usa Mockito nel progetto, quindi il fake e'
     * scritto a mano.
     */
    private static class UtenteDAOFinto implements UtenteDAO {
        private int chiamateCarica = 0;
        private int chiamateSalva = 0;
        private List<Utente> datiSalvati = new ArrayList<>();

        @Override
        public void salva(List<Utente> utenti) throws PersistenzaException {
            chiamateSalva++;
            datiSalvati = new ArrayList<>(utenti);
        }

        @Override
        public List<Utente> carica() throws PersistenzaException {
            chiamateCarica++;
            return datiSalvati;
        }
    }

    private UtenteDAOFinto daoFinto;
    private UtenteDAOConCache daoConCache;

    @BeforeEach
    void resetCache() {
        daoFinto = new UtenteDAOFinto();
        daoConCache = new UtenteDAOConCache(daoFinto);
        // La cache e' un Singleton condiviso tra i test: la invalidiamo
        // esplicitamente prima di ogni test per partire da uno stato pulito.
        CacheUtenti.getInstance().invalida();
    }

    @Test
    void testPrimaLetturaInterrogaLaPersistenzaReale() throws PersistenzaException {
        daoFinto.datiSalvati.add(new Utente(1, "mariorossi", "Mario", "Rossi", "mario@email.com", "pass123"));

        daoConCache.carica();

        assertEquals(1, daoFinto.chiamateCarica);
    }

    @Test
    void testSecondaLetturaUsaLaCacheENonReinterrogaLaPersistenza() throws PersistenzaException {
        daoFinto.datiSalvati.add(new Utente(1, "mariorossi", "Mario", "Rossi", "mario@email.com", "pass123"));

        daoConCache.carica();
        daoConCache.carica();

        assertEquals(1, daoFinto.chiamateCarica);
    }

    @Test
    void testSalvataggioInvalidaLaCacheEForzaUnaNuovaLettura() throws PersistenzaException {
        daoConCache.carica();
        daoConCache.salva(new ArrayList<>());
        daoConCache.carica();

        assertEquals(2, daoFinto.chiamateCarica);
        assertEquals(1, daoFinto.chiamateSalva);
    }
}

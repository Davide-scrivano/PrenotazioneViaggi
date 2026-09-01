package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.cache.MemoriaCentrale;
import dao.cache.PacchettoDAOConCache;
import exceptions.PersistenzaException;
import model.valori.DettagliOfferta;
import model.Pacchetto;
import model.valori.PeriodoViaggio;
import model.valori.TipoVolo;

class TestMemoriaCentrale {

    private static final class PacchettoDAOContatore implements PacchettoDAO {

        private int lettureEseguite = 0;
        private final List<Pacchetto> pacchetti = new ArrayList<>(List.of(
                new Pacchetto(1, "Roma", new PeriodoViaggio(0L, 100L), 350f, 10,
                        new DettagliOfferta(4, TipoVolo.DIRETTO))));

        @Override
        public List<Pacchetto> trovaTutti() {
            lettureEseguite++;
            return new ArrayList<>(pacchetti);
        }

        @Override
        public Pacchetto trovaPerId(int id) {
            lettureEseguite++;
            return pacchetti.get(0);
        }

        @Override
        public void aggiorna(Pacchetto pacchetto) {
            pacchetti.set(0, pacchetto);
        }
    }

    private MemoriaCentrale memoria;

    @BeforeEach
    void preparaMemoria() {
        memoria = MemoriaCentrale.getSingletonInstance();
        memoria.reimposta();
    }

    @Test
    void laMemoriaCentraleEunicaPerTuttoIlSistema() {
        assertSame(memoria, MemoriaCentrale.getSingletonInstance());
    }

    @Test
    void leLettureRipetuteNonRaggiungonoLaPersistenza() throws PersistenzaException {
        PacchettoDAOContatore archivio = new PacchettoDAOContatore();
        PacchettoDAO conCache = new PacchettoDAOConCache(archivio);

        conCache.trovaTutti();
        conCache.trovaTutti();
        conCache.trovaPerId(1);
        conCache.trovaPerId(1);

        assertEquals(1, archivio.lettureEseguite);
    }

    @Test
    void unaScritturaSvuotaLaCacheDelDaoEcostringeARileggere() throws PersistenzaException {
        PacchettoDAOContatore archivio = new PacchettoDAOContatore();
        PacchettoDAO conCache = new PacchettoDAOConCache(archivio);

        Pacchetto pacchetto = conCache.trovaPerId(1);
        conCache.trovaTutti();
        assertEquals(1, archivio.lettureEseguite);

        conCache.aggiorna(pacchetto);

        conCache.trovaTutti();
        assertEquals(2, archivio.lettureEseguite);
    }

    @Test
    void laScritturaSvuotaAncheGliArchiviDelleAltreEntita() throws PersistenzaException {
        PacchettoDAOContatore archivio = new PacchettoDAOContatore();
        PacchettoDAO conCache = new PacchettoDAOConCache(archivio);

        memoria.memorizzaUtente(new model.Utente(1, "mariorossi", "Mario", "Rossi", "m@r.it", "x",
                model.valori.TipoUtente.CONSUMER));
        assertNotNull(memoria.getUtente(1));

        conCache.aggiorna(conCache.trovaPerId(1));

        assertNull(memoria.getUtente(1));
    }

    @Test
    void gliOsservatoriVengonoAvvisatiDelSvuotamento() {
        List<String> notifiche = new ArrayList<>();
        memoria.registraOsservatore(() -> notifiche.add("svuotata"));

        memoria.datiModificati();

        assertEquals(1, notifiche.size());
    }
}

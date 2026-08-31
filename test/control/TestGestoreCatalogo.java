package control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dao.filesystem.DAOFactoryFileSystem;
import exceptions.ApplicazioneException;
import model.Catalogo;

class TestGestoreCatalogo {

    @TempDir
    Path cartellaDati;

    private GestoreCatalogo gestoreCatalogo;

    @BeforeEach
    void preparaSistema() {
        gestoreCatalogo = new GestoreCatalogo(new DAOFactoryFileSystem(cartellaDati.toString()));
    }

    @Test
    void ilCatalogoElencaIPacchettiDisponibili() throws ApplicazioneException {
        Catalogo catalogo = gestoreCatalogo.consultaCatalogo();

        assertEquals(8, catalogo.pacchettiDisponibili().size());
        assertEquals("Catalogo viaggi PrenotazioneViaggi", catalogo.getTitolo());
    }

    @Test
    void ilCatalogoSaCercareAlProprioInterno() throws ApplicazioneException {
        Catalogo catalogo = gestoreCatalogo.consultaCatalogo();

        assertEquals(1, catalogo.cercaPerDestinazione("roma").size());
        assertEquals("Roma", catalogo.cercaPerDestinazione("ROM").get(0).getDestinazione());
        assertEquals(8, catalogo.cercaPerDestinazione("  ").size());
        assertTrue(catalogo.cercaPerDestinazione("Atlantide").isEmpty());
    }

    @Test
    void unPacchettoInesistenteNonStaNelCatalogo() throws ApplicazioneException {
        Catalogo catalogo = gestoreCatalogo.consultaCatalogo();

        assertNull(catalogo.trovaPacchetto(999));
    }

    @Test
    void laSecondaConsultazioneRestituisceLoStessoCatalogo() throws ApplicazioneException {
        Catalogo primaLettura = gestoreCatalogo.consultaCatalogo();
        Catalogo secondaLettura = gestoreCatalogo.consultaCatalogo();

        assertEquals(primaLettura.getId(), secondaLettura.getId());
        assertEquals(primaLettura.pacchettiDisponibili().size(), secondaLettura.pacchettiDisponibili().size());
    }
}

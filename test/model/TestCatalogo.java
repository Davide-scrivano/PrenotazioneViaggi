package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Autore: Davide Scrivano
public class TestCatalogo {

    @Test
    public void testGetInstanceRestituisceStessaIstanza() {
        Catalogo istanza1 = Catalogo.getInstance();
        Catalogo istanza2 = Catalogo.getInstance();

        assertSame(istanza1, istanza2);
    }

    @Test
    public void testAggiungiPacchettoVisibileDaOgniIstanza() {
        Catalogo catalogo = Catalogo.getInstance();
        Pacchetto pacchetto = new Pacchetto(1, "Roma", 1000L, 2000L, 300f);

        catalogo.aggiungiPacchetto(pacchetto);

        assertTrue(Catalogo.getInstance().pacchettiDisponibili().contains(pacchetto));
    }
}
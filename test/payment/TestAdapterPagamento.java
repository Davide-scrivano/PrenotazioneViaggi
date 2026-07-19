package payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Autore: Davide Scrivano
public class TestAdapterPagamento {

    @Test
    public void testMetodoPagamentoImportoValido() {
        PagamentoEsternoGateway gateway = new PagamentoEsternoGateway();
        AdapterPagamento adapter = new AdapterPagamento(gateway, 100f);

        assertTrue(adapter.metodoPagamento());
    }

    @Test
    public void testMetodoPagamentoImportoNonValido() {
        PagamentoEsternoGateway gateway = new PagamentoEsternoGateway();
        AdapterPagamento adapter = new AdapterPagamento(gateway, -50f);

        assertFalse(adapter.metodoPagamento());
    }

    @Test
    public void testCostoRestituitoCorrettamente() {
        PagamentoEsternoGateway gateway = new PagamentoEsternoGateway();
        AdapterPagamento adapter = new AdapterPagamento(gateway, 75f);

        assertEquals(75f, adapter.costo(), 0.001);
    }
}

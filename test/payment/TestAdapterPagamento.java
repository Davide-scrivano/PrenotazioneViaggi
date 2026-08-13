package payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * L'Adapter traduce l'unica operazione attesa dal Target (elaboraPagamento)
 * nella sequenza di due chiamate offerta dall'Adaptee (autorizza, poi
 * addebita).
 *
 * Autore: Davide Scrivano
 */
class TestAdapterPagamento {

    @Test
    void testElaboraPagamentoImportoValido() {
        PagamentoEsternoGateway gateway = new PagamentoEsternoGateway();
        AdapterPagamento adapter = new AdapterPagamento(gateway, 100f);

        assertTrue(adapter.elaboraPagamento());
    }

    @Test
    void testElaboraPagamentoImportoNonValido() {
        PagamentoEsternoGateway gateway = new PagamentoEsternoGateway();
        AdapterPagamento adapter = new AdapterPagamento(gateway, -50f);

        assertFalse(adapter.elaboraPagamento());
    }

    @Test
    void testCostoRestituitoCorrettamente() {
        PagamentoEsternoGateway gateway = new PagamentoEsternoGateway();
        AdapterPagamento adapter = new AdapterPagamento(gateway, 75f);

        assertEquals(75f, adapter.costo(), 0.001f);
    }

    /**
     * L'Adapter implementa il Target, quindi puo' essere usato ovunque il
     * sistema si aspetti un Pagamento: e' esattamente il punto del pattern.
     */
    @Test
    void testAdapterUsabileComePagamento() {
        Pagamento pagamento = new AdapterPagamento(new PagamentoEsternoGateway(), 40f);

        assertEquals(40f, pagamento.costo(), 0.001f);
        assertNotNull(pagamento.descrizione());
    }
}

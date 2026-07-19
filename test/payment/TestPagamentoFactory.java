package payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Autore: Davide Scrivano
public class TestPagamentoFactory {

    private PagamentoFactory factory = new PagamentoFactory();

    @Test
    public void testCreaCartaDiCreditoPagamentoTipoCorretto() {
        Pagamento pagamento = factory.creaCartaDiCreditoPagamento("1234567812345678", "Mario Rossi", "12/28", "123", 250f);

        assertTrue(pagamento instanceof CartaDiCreditoPagamento);
    }

    @Test
    public void testCreaCartaDiCreditoPagamentoCostoCorretto() {
        Pagamento pagamento = factory.creaCartaDiCreditoPagamento("1234567812345678", "Mario Rossi", "12/28", "123", 250f);

        assertEquals(250f, pagamento.costo(), 0.001);
    }

    @Test
    public void testCreaPaypalPagamentoTipoCorretto() {
        Pagamento pagamento = factory.creaPaypalPagamento("mario.rossi@email.com", "password123", 180f);

        assertTrue(pagamento instanceof PayPalPagamento);
    }

    @Test
    public void testCreaPaypalPagamentoCostoCorretto() {
        Pagamento pagamento = factory.creaPaypalPagamento("mario.rossi@email.com", "password123", 180f);

        assertEquals(180f, pagamento.costo(), 0.001);
    }
}
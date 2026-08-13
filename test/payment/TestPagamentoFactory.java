package payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * La Factory riceve il metodo scelto dal cliente e restituisce il tipo
 * astratto Pagamento: e' lei a decidere quale classe concreta creare, e
 * questo e' cio' che la rende una Factory e non un contenitore di
 * costruttori. I test verificano proprio quella responsabilita'.
 */
class TestPagamentoFactory {

    private final PagamentoFactory factory = new PagamentoFactory();

    private DatiPagamento datiCarta() {
        return DatiPagamento.perCarta("1234567812345678", "Mario Rossi", "12/28", "123");
    }

    private DatiPagamento datiPaypal() {
        return DatiPagamento.perPaypal("mario.rossi@email.com", "password123");
    }

    @Test
    void testCreaCartaDiCreditoPagamentoTipoCorretto() {
        Pagamento pagamento = factory.crea(MetodoPagamento.CARTA_DI_CREDITO, datiCarta(), 250f);

        assertTrue(pagamento instanceof CartaDiCreditoPagamento);
    }

    @Test
    void testCreaCartaDiCreditoPagamentoCostoCorretto() {
        Pagamento pagamento = factory.crea(MetodoPagamento.CARTA_DI_CREDITO, datiCarta(), 250f);

        assertEquals(250f, pagamento.costo(), 0.001f);
    }

    @Test
    void testCreaPaypalPagamentoTipoCorretto() {
        Pagamento pagamento = factory.crea(MetodoPagamento.PAYPAL, datiPaypal(), 180f);

        assertTrue(pagamento instanceof PayPalPagamento);
    }

    @Test
    void testCreaPaypalPagamentoCostoCorretto() {
        Pagamento pagamento = factory.crea(MetodoPagamento.PAYPAL, datiPaypal(), 180f);

        assertEquals(180f, pagamento.costo(), 0.001f);
    }

    /**
     * Ogni implementazione dichiara come si chiama: e' polimorfismo, non
     * una catena di instanceof scritta da qualche altra parte.
     */
    @Test
    void testDescrizioneDipendeDallImplementazione() {
        assertEquals("Carta di credito",
                factory.crea(MetodoPagamento.CARTA_DI_CREDITO, datiCarta(), 10f).descrizione());
        assertEquals("PayPal",
                factory.crea(MetodoPagamento.PAYPAL, datiPaypal(), 10f).descrizione());
    }

    @Test
    void testCartaConCvvNonValidoNonSuperaLaVerificaDelMetodo() {
        Pagamento pagamento = factory.crea(MetodoPagamento.CARTA_DI_CREDITO,
                DatiPagamento.perCarta("1234567812345678", "Mario Rossi", "12/28", "12"), 100f);

        assertFalse(pagamento.elaboraPagamento());
    }

    @Test
    void testPaypalConEmailNonValidaNonSuperaLaVerificaDelMetodo() {
        Pagamento pagamento = factory.crea(MetodoPagamento.PAYPAL,
                DatiPagamento.perPaypal("email-senza-chiocciola", "password123"), 100f);

        assertFalse(pagamento.elaboraPagamento());
    }

    /**
     * Il codice del metodo arriva dal Bean come testo: la Factory lavora
     * sull'enum, quindi la traduzione deve riconoscere i codici previsti e
     * respingere gli altri.
     */
    @Test
    void testCodiceMetodoRiconosciutoSoloSePrevisto() {
        assertEquals(MetodoPagamento.PAYPAL, MetodoPagamento.daCodice("PAYPAL"));
        assertEquals(MetodoPagamento.CARTA_DI_CREDITO, MetodoPagamento.daCodice("carta_di_credito"));
        assertNull(MetodoPagamento.daCodice("BONIFICO"));
        assertNull(MetodoPagamento.daCodice(null));
    }

    @Test
    void testMetodoNonSpecificatoLanciaEccezione() {
        // I dati della carta si preparano prima: dentro la lambda deve
        // restare la sola crea(), altrimenti non e' detto quale delle due
        // chiamate ha lanciato l'eccezione attesa.
        DatiPagamento dati = datiCarta();

        assertThrows(IllegalArgumentException.class, () -> factory.crea(null, dati, 100f));
    }
}

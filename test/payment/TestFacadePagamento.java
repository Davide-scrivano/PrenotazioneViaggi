package payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bean.DatiPagamentoBean;
import exceptions.PagamentoRifiutatoException;
import model.Pagamento;

class TestFacadePagamento {

    private FacadePagamento facadePagamento;
    private FactoryStrumentoPagamento factoryStrumento;

    @BeforeEach
    void preparaSottosistema() {
        facadePagamento = new FacadePagamento();
        factoryStrumento = new FactoryStrumentoPagamento();
    }

    private DatiPagamentoBean carta(String numero, String titolare, String scadenza, String cvv) {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_CARTA);
        dati.setNumeroCarta(numero);
        dati.setTitolare(titolare);
        dati.setScadenza(scadenza);
        dati.setCvv(cvv);
        return dati;
    }

    private DatiPagamentoBean paypal(String email, String password) {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_PAYPAL);
        dati.setEmailPaypal(email);
        dati.setPasswordPaypal(password);
        return dati;
    }

    @Test
    void ogniFactoryMethodProduceIlProprioStrumento() {
        StrumentoPagamento conCarta = factoryStrumento.creaPagamentoConCarta("4111111111111111", "Mario Rossi", "12/30", "123");
        StrumentoPagamento conPaypal = factoryStrumento.creaPagamentoConPayPal("mario@rossi.it", "segreta");

        assertTrue(conCarta.descrizione().startsWith("Carta di credito"));
        assertTrue(conPaypal.descrizione().startsWith("PayPal"));
    }

    @Test
    void adapterTraduceGliEuroInCentesimiPerIlGateway() {
        AddebitoPagamento adapter = new AdapterGatewayPagamento(new GatewayPagamentoEsterno(), "PV99");

        assertEquals("AUTH-PV99", adapter.addebita(12.34f, "4111111111111111", "123"));
    }

    @Test
    void adapterRiportaIlRifiutoDelGateway() {
        AddebitoPagamento adapter = new AdapterGatewayPagamento(new GatewayPagamentoEsterno(), "PV99");

        assertNull(adapter.addebita(-5f, "4111111111111111", "123"));
    }

    @Test
    void ilPagamentoConCartaValidaProduceLaTracciaContabile() throws PagamentoRifiutatoException {
        Pagamento pagamento = facadePagamento.incassa(7,
                carta("4111111111111111", "Mario Rossi", "12/30", "123"), 700f);

        assertEquals(7, pagamento.getId());
        assertEquals(700f, pagamento.getImporto());
        assertTrue(pagamento.getMetodo().startsWith("Carta di credito"));
        assertTrue(pagamento.getCodiceAutorizzazione().contains("PV7"));
    }

    @Test
    void ilPagamentoPaypalValidoVieneAccettato() throws PagamentoRifiutatoException {
        Pagamento pagamento = facadePagamento.incassa(1, paypal("mario@rossi.it", "segreta"), 350f);

        assertTrue(pagamento.getMetodo().startsWith("PayPal"));
    }

    @Test
    void unMetodoNonRiconosciutoRicadeSullaCarta() throws PagamentoRifiutatoException {
        DatiPagamentoBean dati = carta("4111111111111111", "Mario Rossi", "12/30", "123");
        dati.setMetodoPagamento("BONIFICO");

        assertTrue(facadePagamento.incassa(1, dati, 350f).getMetodo().startsWith("Carta di credito"));
    }

    @Test
    void unaCartaNonAccettataDalCircuitoVieneRifiutata() {
        assertThrows(PagamentoRifiutatoException.class,
                () -> facadePagamento.incassa(1, carta("4111111111110000", "Mario Rossi", "12/30", "123"), 700f));
    }

    @Test
    void importoNonPositivoVieneRifiutato() {
        assertThrows(PagamentoRifiutatoException.class,
                () -> facadePagamento.incassa(1, carta("4111111111111111", "Mario Rossi", "12/30", "123"), 0f));
    }
}

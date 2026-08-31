package payment;

import bean.DatiPagamentoBean;
import exceptions.PagamentoRifiutatoException;
import model.Pagamento;

public class FacadePagamento {

    private final FactoryStrumentoPagamento factoryStrumento = new FactoryStrumentoPagamento();
    private final GatewayPagamentoEsterno gateway = new GatewayPagamentoEsterno();

    public Pagamento incassa(int idPagamento, DatiPagamentoBean dati, float importo)
            throws PagamentoRifiutatoException {

        StrumentoPagamento strumento = creaStrumento(dati);

        AddebitoPagamento addebito = new AdapterGatewayPagamento(gateway, riferimento(idPagamento));
        String codiceAutorizzazione = addebito.addebita(importo, strumento.riferimento(), strumento.codiceSicurezza());
        if (codiceAutorizzazione == null) {
            throw new PagamentoRifiutatoException("Il pagamento non e' stato autorizzato dal circuito.");
        }

        return new Pagamento(idPagamento, strumento.descrizione(), importo, codiceAutorizzazione);
    }

    private StrumentoPagamento creaStrumento(DatiPagamentoBean dati) {
        if (MetodoPagamento.daCodice(dati.getMetodoPagamento()) == MetodoPagamento.PAYPAL) {
            return factoryStrumento.creaPagamentoConPayPal(dati.getEmailPaypal(), dati.getPasswordPaypal());
        }
        return factoryStrumento.creaPagamentoConCarta(dati.getNumeroCarta(), dati.getTitolare(),
                dati.getScadenza(), dati.getCvv());
    }

    private String riferimento(int idPagamento) {
        return "PV" + idPagamento;
    }
}

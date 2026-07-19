package payment;

public class AdapterPagamento implements Pagamento {

    private PagamentoEsternoGateway externalGateway;
    private float importo;

    public AdapterPagamento(PagamentoEsternoGateway gateway, float importo) {
        this.externalGateway = gateway;
        this.importo = importo;
    }

    @Override
    public boolean metodoPagamento() {
        
        boolean autorizzato = externalGateway.autorizza(importo);
        if (!autorizzato) {
            return false;
        }
        return externalGateway.addebita(importo);
    }

    @Override
    public float costo() {
        return importo;
    }
}

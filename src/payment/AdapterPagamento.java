package payment;

public class AdapterPagamento implements Pagamento {

    private final PagamentoEsternoGateway externalGateway;
    private final float importo;

    public AdapterPagamento(PagamentoEsternoGateway gateway, float importo) {
        this.externalGateway = gateway;
        this.importo = importo;
    }

    @Override
    public boolean elaboraPagamento() {
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

    @Override
    public String descrizione() {
        return "Gateway esterno";
    }
}

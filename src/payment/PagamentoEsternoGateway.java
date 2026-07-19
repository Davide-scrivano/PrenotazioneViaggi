package payment;

public class PagamentoEsternoGateway {

    public boolean autorizza(float importo) {
        
        return importo > 0;
    }

    public boolean addebita(float importo) {
        
        return importo > 0;
    }
}

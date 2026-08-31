package dao.decorator;

import dao.PagamentoDAO;
import exceptions.PersistenzaException;
import model.Pagamento;

public abstract class PagamentoDAODecorator implements PagamentoDAO {

    private final PagamentoDAO componente;

    protected PagamentoDAODecorator(PagamentoDAO componente) {
        this.componente = componente;
    }

    @Override
    public int prossimoId() throws PersistenzaException {
        return componente.prossimoId();
    }

    @Override
    public void inserisci(Pagamento pagamento) throws PersistenzaException {
        componente.inserisci(pagamento);
    }

    @Override
    public Pagamento trovaPerId(int id) throws PersistenzaException {
        return componente.trovaPerId(id);
    }
}

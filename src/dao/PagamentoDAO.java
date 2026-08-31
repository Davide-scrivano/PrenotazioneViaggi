package dao;

import exceptions.PersistenzaException;
import model.Pagamento;

public interface PagamentoDAO {

    int prossimoId() throws PersistenzaException;

    void inserisci(Pagamento pagamento) throws PersistenzaException;

    Pagamento trovaPerId(int id) throws PersistenzaException;
}

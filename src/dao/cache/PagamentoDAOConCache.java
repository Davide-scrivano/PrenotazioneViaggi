package dao.cache;

import java.util.HashMap;
import java.util.Map;

import dao.PagamentoDAO;
import exceptions.PersistenzaException;
import model.Pagamento;

public final class PagamentoDAOConCache implements PagamentoDAO, OsservatoreCache {

    private final PagamentoDAO componente;
    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private final Map<Integer, Pagamento> pagamenti = new HashMap<>();

    public PagamentoDAOConCache(PagamentoDAO componente) {
        this.componente = componente;
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        pagamenti.clear();
    }

    @Override
    public int prossimoId() throws PersistenzaException {
        return componente.prossimoId();
    }

    @Override
    public void inserisci(Pagamento pagamento) throws PersistenzaException {
        componente.inserisci(pagamento);
        soggetto.datiModificati();
    }

    @Override
    public Pagamento trovaPerId(int id) throws PersistenzaException {
        Pagamento pagamento = pagamenti.get(id);
        if (pagamento == null) {
            pagamento = componente.trovaPerId(id);
            if (pagamento != null) {
                pagamenti.put(pagamento.getId(), pagamento);
            }
        }
        return pagamento;
    }
}

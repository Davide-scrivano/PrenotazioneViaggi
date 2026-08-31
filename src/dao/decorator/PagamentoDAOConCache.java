package dao.decorator;

import java.util.HashMap;
import java.util.Map;

import dao.PagamentoDAO;
import dao.cache.MemoriaCentrale;
import dao.cache.OsservatoreCache;
import exceptions.PersistenzaException;
import model.Pagamento;

public final class PagamentoDAOConCache extends PagamentoDAODecorator implements OsservatoreCache {

    private final MemoriaCentrale soggetto = MemoriaCentrale.getSingletonInstance();

    private final Map<Integer, Pagamento> pagamenti = new HashMap<>();

    public PagamentoDAOConCache(PagamentoDAO componente) {
        super(componente);
        soggetto.registraOsservatore(this);
    }

    @Override
    public void aggiorna() {
        pagamenti.clear();
    }

    @Override
    public void inserisci(Pagamento pagamento) throws PersistenzaException {
        super.inserisci(pagamento);
        soggetto.datiModificati();
    }

    @Override
    public Pagamento trovaPerId(int id) throws PersistenzaException {
        Pagamento pagamento = pagamenti.get(id);
        if (pagamento == null) {
            pagamento = super.trovaPerId(id);
            if (pagamento != null) {
                pagamenti.put(pagamento.getId(), pagamento);
            }
        }
        return pagamento;
    }
}

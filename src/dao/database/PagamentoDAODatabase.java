package dao.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.PagamentoDAO;
import exceptions.PersistenzaException;
import model.Pagamento;

public class PagamentoDAODatabase implements PagamentoDAO {

    private static final String SELECT_PROSSIMO_ID = "SELECT COALESCE(MAX(id), 0) + 1 FROM pagamento";
    private static final String INSERT =
            "INSERT INTO pagamento (id, metodo, importo, codice_autorizzazione, data_esecuzione)"
                    + " VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_PER_ID =
            "SELECT id, metodo, importo, codice_autorizzazione, data_esecuzione FROM pagamento WHERE id = ?";

    private final PoolConnessioni pool = PoolConnessioni.getSingletonInstance();

    @Override
    public int prossimoId() throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_PROSSIMO_ID);
                ResultSet risultato = comando.executeQuery()) {
            return risultato.next() ? risultato.getInt(1) : 1;
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    @Override
    public void inserisci(Pagamento pagamento) throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(INSERT)) {
            comando.setInt(1, pagamento.getId());
            comando.setString(2, pagamento.getMetodo());
            comando.setFloat(3, pagamento.getImporto());
            comando.setString(4, pagamento.getCodiceAutorizzazione());
            comando.setLong(5, pagamento.getDataEsecuzione());
            comando.executeUpdate();
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    @Override
    public Pagamento trovaPerId(int id) throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_PER_ID)) {
            comando.setInt(1, id);
            try (ResultSet risultato = comando.executeQuery()) {
                if (!risultato.next()) {
                    return null;
                }
                return new Pagamento(risultato.getInt("id"), risultato.getString("metodo"),
                        risultato.getFloat("importo"), risultato.getString("codice_autorizzazione"),
                        risultato.getLong("data_esecuzione"));
            }
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    private PersistenzaException errore(SQLException causa) {
        return new PersistenzaException("Accesso ai pagamenti non riuscito.", causa);
    }
}

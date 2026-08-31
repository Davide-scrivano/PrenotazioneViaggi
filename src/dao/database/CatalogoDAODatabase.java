package dao.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.CatalogoDAO;
import dao.PacchettoDAO;
import exceptions.PersistenzaException;
import model.Catalogo;

public class CatalogoDAODatabase implements CatalogoDAO {

    private static final String SELECT_TESTATA = "SELECT id, titolo FROM catalogo ORDER BY id";

    private final PoolConnessioni pool = PoolConnessioni.getSingletonInstance();
    private final PacchettoDAO pacchettoDAO;

    public CatalogoDAODatabase(PacchettoDAO pacchettoDAO) {
        this.pacchettoDAO = pacchettoDAO;
    }

    @Override
    public Catalogo carica() throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_TESTATA);
                ResultSet risultato = comando.executeQuery()) {

            if (!risultato.next()) {
                throw new PersistenzaException("Nessun catalogo definito sul database.");
            }
            return new Catalogo(risultato.getInt("id"), risultato.getString("titolo"), pacchettoDAO.trovaTutti());
        } catch (SQLException e) {
            throw new PersistenzaException("Accesso al catalogo non riuscito: " + e.getMessage(), e);
        }
    }
}

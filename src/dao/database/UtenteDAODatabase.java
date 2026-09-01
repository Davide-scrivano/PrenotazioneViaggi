package dao.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.UtenteDAO;
import exceptions.PersistenzaException;
import model.valori.TipoUtente;
import model.Utente;

public class UtenteDAODatabase implements UtenteDAO {

    private static final String SELECT_PER_ID =
            "SELECT id, nickname, nome, cognome, email, password, tipo FROM utente WHERE id = ?";
    private static final String SELECT_PER_NICKNAME =
            "SELECT id, nickname, nome, cognome, email, password, tipo FROM utente WHERE nickname = ?";

    private final PoolConnessioni pool = PoolConnessioni.getSingletonInstance();

    @Override
    public Utente trovaPerId(int id) throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_PER_ID)) {
            comando.setInt(1, id);
            return eseguiRicerca(comando);
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    @Override
    public Utente trovaPerNickname(String nickname) throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_PER_NICKNAME)) {
            comando.setString(1, nickname);
            return eseguiRicerca(comando);
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    private Utente eseguiRicerca(PreparedStatement comando) throws SQLException {
        try (ResultSet risultato = comando.executeQuery()) {
            if (!risultato.next()) {
                return null;
            }
            return new Utente(risultato.getInt("id"), risultato.getString("nickname"),
                    risultato.getString("nome"), risultato.getString("cognome"),
                    risultato.getString("email"), risultato.getString("password"),
                    TipoUtente.daCodice(risultato.getString("tipo")));
        }
    }

    private PersistenzaException errore(SQLException causa) {
        return new PersistenzaException("Lettura degli utenti non riuscita.", causa);
    }
}

package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import exceptions.PersistenzaException;
import model.TipoUtente;
import model.Utente;

public class UtenteDAOMySQL implements UtenteDAO {

    private static final String COLONNE = "id, nickname, name, surname, email, password, tipo";

    private final String url;
    private final String username;
    private final String password;

    public UtenteDAOMySQL(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void salva(List<Utente> utenti) throws PersistenzaException {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            conn.setAutoCommit(false);
            riscriviTabella(conn, utenti);
        } catch (SQLException e) {
            throw new PersistenzaException("Impossibile salvare gli utenti sul database: " + e.getMessage(), e);
        }
    }

    private void riscriviTabella(Connection conn, List<Utente> utenti) throws SQLException {
        String sql = "INSERT INTO utenti (" + COLONNE + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Statement pulizia = conn.createStatement();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            pulizia.executeUpdate("DELETE FROM utenti");
            for (Utente u : utenti) {
                stmt.setInt(1, u.getId());
                stmt.setString(2, u.getNickname());
                stmt.setString(3, u.getName());
                stmt.setString(4, u.getSurname());
                stmt.setString(5, u.getEmail());
                stmt.setString(6, u.getPassword());
                stmt.setString(7, u.getTipo().name());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    @Override
    public List<Utente> carica() throws PersistenzaException {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT " + COLONNE + " FROM utenti";

        try (Connection conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                utenti.add(new Utente(
                        rs.getInt("id"),
                        rs.getString("nickname"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        TipoUtente.daCodice(rs.getString("tipo"))));
            }
        } catch (SQLException e) {
            throw new PersistenzaException("Impossibile leggere gli utenti dal database: " + e.getMessage(), e);
        }
        return utenti;
    }
}

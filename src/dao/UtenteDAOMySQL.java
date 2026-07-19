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
import model.Utente;

/**
 * Versione "DBMS" del DAO: salva/carica gli utenti da un database MySQL
 * tramite JDBC. Si appoggia alla tabella:
 *
 * CREATE TABLE utenti (
 *   id INT PRIMARY KEY,
 *   nickname VARCHAR(50),
 *   name VARCHAR(50),
 *   surname VARCHAR(50),
 *   email VARCHAR(100),
 *   password VARCHAR(50)
 * );
 *
 * Richiede il driver JDBC di MySQL nel classpath del progetto per
 * funzionare a runtime (per la sola compilazione non serve, perche'
 * si usano solo le classi standard di java.sql).
 */
public class UtenteDAOMySQL implements UtenteDAO {

    private String url;
    private String username;
    private String password;

    public UtenteDAOMySQL(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void salva(List<Utente> utenti) throws PersistenzaException {
        String sql = "REPLACE INTO utenti (id, nickname, name, surname, email, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Utente u : utenti) {
                stmt.setInt(1, u.getId());
                stmt.setString(2, u.getNickname());
                stmt.setString(3, u.getName());
                stmt.setString(4, u.getSurname());
                stmt.setString(5, u.getEmail());
                stmt.setString(6, u.getPassword());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new PersistenzaException("Impossibile salvare gli utenti sul database: " + e.getMessage());
        }
    }

    @Override
    public List<Utente> carica() throws PersistenzaException {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT id, nickname, name, surname, email, password FROM utenti";

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
                        rs.getString("password")));
            }
        } catch (SQLException e) {
            throw new PersistenzaException("Impossibile leggere gli utenti dal database: " + e.getMessage());
        }
        return utenti;
    }
}

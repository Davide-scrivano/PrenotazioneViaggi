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
import model.DettagliOfferta;
import model.Pacchetto;
import model.TipoVolo;

public class PacchettoDAOMySQL implements PacchettoDAO {

    private static final String COLONNE = "id, destinazione, data_partenza, data_rientro, "
            + "prezzo, posti_disponibili, stelle_hotel, tipo_volo";

    private final String url;
    private final String username;
    private final String password;

    public PacchettoDAOMySQL(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void salva(List<Pacchetto> pacchetti) throws PersistenzaException {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            conn.setAutoCommit(false);
            riscriviTabella(conn, pacchetti);
        } catch (SQLException e) {
            throw new PersistenzaException("Impossibile salvare i pacchetti sul database: " + e.getMessage(), e);
        }
    }

    private void riscriviTabella(Connection conn, List<Pacchetto> pacchetti) throws SQLException {
        String sql = "INSERT INTO pacchetti (" + COLONNE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Statement pulizia = conn.createStatement();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            pulizia.executeUpdate("DELETE FROM pacchetti");
            for (Pacchetto p : pacchetti) {
                stmt.setInt(1, p.getId());
                stmt.setString(2, p.getDestinazione());
                stmt.setLong(3, p.getDataPartenza());
                stmt.setLong(4, p.getDataRientro());
                stmt.setFloat(5, p.getPrezzo());
                stmt.setInt(6, p.getPostiDisponibili());
                stmt.setInt(7, p.getStelleHotel());
                stmt.setString(8, p.getTipoVolo().name());
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
    public List<Pacchetto> carica() throws PersistenzaException {
        List<Pacchetto> pacchetti = new ArrayList<>();
        String sql = "SELECT " + COLONNE + " FROM pacchetti";

        try (Connection conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pacchetti.add(new Pacchetto(
                        rs.getInt("id"),
                        rs.getString("destinazione"),
                        rs.getLong("data_partenza"),
                        rs.getLong("data_rientro"),
                        rs.getFloat("prezzo"),
                        rs.getInt("posti_disponibili"),
                        new DettagliOfferta(rs.getInt("stelle_hotel"),
                                TipoVolo.valueOf(rs.getString("tipo_volo")))));
            }
        } catch (SQLException | IllegalArgumentException e) {
            throw new PersistenzaException("Impossibile leggere i pacchetti dal database: " + e.getMessage(), e);
        }
        return pacchetti;
    }
}

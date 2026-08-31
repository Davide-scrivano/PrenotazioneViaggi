package dao.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.PacchettoDAO;
import exceptions.PersistenzaException;
import model.valori.DettagliOfferta;
import model.Pacchetto;
import model.valori.PeriodoViaggio;
import model.valori.TipoVolo;

public class PacchettoDAODatabase implements PacchettoDAO {

    private static final String COLONNE =
            "id, destinazione, data_partenza, data_rientro, prezzo, posti, stelle, tipo_volo";
    private static final String SELECT_TUTTI = "SELECT " + COLONNE + " FROM pacchetto ORDER BY id";
    private static final String SELECT_PER_ID = "SELECT " + COLONNE + " FROM pacchetto WHERE id = ?";
    private static final String UPDATE_POSTI = "UPDATE pacchetto SET posti = ? WHERE id = ?";

    private final PoolConnessioni pool = PoolConnessioni.getSingletonInstance();

    @Override
    public List<Pacchetto> trovaTutti() throws PersistenzaException {
        List<Pacchetto> pacchetti = new ArrayList<>();
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_TUTTI);
                ResultSet risultato = comando.executeQuery()) {
            while (risultato.next()) {
                pacchetti.add(leggi(risultato));
            }
        } catch (SQLException e) {
            throw errore(e);
        }
        return pacchetti;
    }

    @Override
    public Pacchetto trovaPerId(int id) throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_PER_ID)) {
            comando.setInt(1, id);
            try (ResultSet risultato = comando.executeQuery()) {
                return risultato.next() ? leggi(risultato) : null;
            }
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    @Override
    public void aggiorna(Pacchetto pacchetto) throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(UPDATE_POSTI)) {
            comando.setInt(1, pacchetto.getPostiDisponibili());
            comando.setInt(2, pacchetto.getId());
            comando.executeUpdate();
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    private Pacchetto leggi(ResultSet risultato) throws SQLException {
        PeriodoViaggio disponibilita = new PeriodoViaggio(risultato.getLong("data_partenza"),
                risultato.getLong("data_rientro"));
        DettagliOfferta offerta = new DettagliOfferta(risultato.getInt("stelle"),
                TipoVolo.daCodice(risultato.getString("tipo_volo")));
        return new Pacchetto(risultato.getInt("id"), risultato.getString("destinazione"), disponibilita,
                risultato.getFloat("prezzo"), risultato.getInt("posti"), offerta);
    }

    private PersistenzaException errore(SQLException causa) {
        return new PersistenzaException("Accesso al catalogo non riuscito: " + causa.getMessage(), causa);
    }
}

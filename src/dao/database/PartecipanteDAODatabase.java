package dao.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.PartecipanteDAO;
import exceptions.PersistenzaException;
import model.valori.DatiAnagrafici;
import model.Partecipante;

public class PartecipanteDAODatabase implements PartecipanteDAO {

    private static final String SELECT_PROSSIMO_ID = "SELECT COALESCE(MAX(id), 0) + 1 FROM partecipante";
    private static final String INSERT =
            "INSERT INTO partecipante (id, id_prenotazione, nome, cognome, data_nascita, codice_fiscale)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PER_PRENOTAZIONE =
            "SELECT id, nome, cognome, data_nascita, codice_fiscale FROM partecipante"
                    + " WHERE id_prenotazione = ? ORDER BY id";

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
    public void inserisci(Partecipante partecipante, int idPrenotazione) throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(INSERT)) {
            comando.setInt(1, partecipante.getId());
            comando.setInt(2, idPrenotazione);
            comando.setString(3, partecipante.getNome());
            comando.setString(4, partecipante.getCognome());
            comando.setLong(5, partecipante.getDataNascita());
            comando.setString(6, partecipante.getCodiceFiscale());
            comando.executeUpdate();
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    @Override
    public List<Partecipante> trovaPerPrenotazione(int idPrenotazione) throws PersistenzaException {
        List<Partecipante> partecipanti = new ArrayList<>();
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_PER_PRENOTAZIONE)) {
            comando.setInt(1, idPrenotazione);
            try (ResultSet risultato = comando.executeQuery()) {
                while (risultato.next()) {
                    partecipanti.add(leggi(risultato));
                }
            }
        } catch (SQLException e) {
            throw errore(e);
        }
        return partecipanti;
    }

    private Partecipante leggi(ResultSet risultato) throws SQLException {
        DatiAnagrafici anagrafica = new DatiAnagrafici(risultato.getLong("data_nascita"),
                risultato.getString("codice_fiscale"));
        return new Partecipante(risultato.getInt("id"), risultato.getString("nome"),
                risultato.getString("cognome"), anagrafica);
    }

    private PersistenzaException errore(SQLException causa) {
        return new PersistenzaException("Accesso ai partecipanti non riuscito.", causa);
    }
}

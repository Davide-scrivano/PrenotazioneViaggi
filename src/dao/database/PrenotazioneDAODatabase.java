package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dao.PacchettoDAO;
import dao.PagamentoDAO;
import dao.PartecipanteDAO;
import dao.PrenotazioneDAO;
import dao.UtenteDAO;
import exceptions.PersistenzaException;
import model.Pacchetto;
import model.Pagamento;
import model.Partecipante;
import model.valori.PeriodoViaggio;
import model.Prenotazione;
import model.Utente;

public class PrenotazioneDAODatabase implements PrenotazioneDAO {

    private static final Logger LOGGER = Logger.getLogger(PrenotazioneDAODatabase.class.getName());

    private static final String SELECT_PROSSIMO_ID = "SELECT COALESCE(MAX(id), 0) + 1 FROM prenotazione";
    private static final String INSERT =
            "INSERT INTO prenotazione (id, id_utente, id_pacchetto, id_pagamento, data_partenza, data_rientro,"
                    + " data_prenotazione) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PER_ID =
            "SELECT id, id_utente, id_pacchetto, id_pagamento, data_partenza, data_rientro, data_prenotazione"
                    + " FROM prenotazione WHERE id = ?";

    private final PoolConnessioni pool = PoolConnessioni.getSingletonInstance();
    private final UtenteDAO utenteDAO;
    private final PacchettoDAO pacchettoDAO;
    private final PagamentoDAO pagamentoDAO;
    private final PartecipanteDAO partecipanteDAO;

    public PrenotazioneDAODatabase(UtenteDAO utenteDAO, PacchettoDAO pacchettoDAO, PagamentoDAO pagamentoDAO,
            PartecipanteDAO partecipanteDAO) {
        this.utenteDAO = utenteDAO;
        this.pacchettoDAO = pacchettoDAO;
        this.pagamentoDAO = pagamentoDAO;
        this.partecipanteDAO = partecipanteDAO;
    }

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
    public void inserisci(Prenotazione prenotazione) throws PersistenzaException {
        Connection connessione = pool.getConnessione();
        try {
            connessione.setAutoCommit(false);
            salvaAggregato(prenotazione);
            connessione.commit();
        } catch (SQLException | PersistenzaException e) {
            throw annullaESegnala(connessione, e);
        } finally {
            ripristinaAutoCommit(connessione);
        }
    }

    private void salvaAggregato(Prenotazione prenotazione) throws PersistenzaException, SQLException {
        pagamentoDAO.inserisci(prenotazione.getPagamento());
        scriviRiga(prenotazione);
        for (Partecipante partecipante : prenotazione.getPartecipanti()) {
            partecipanteDAO.inserisci(partecipante, prenotazione.getId());
        }
    }

    private void scriviRiga(Prenotazione prenotazione) throws PersistenzaException, SQLException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(INSERT)) {
            comando.setInt(1, prenotazione.getId());
            comando.setInt(2, prenotazione.getIdCliente());
            comando.setInt(3, prenotazione.getIdPacchetto());
            comando.setInt(4, prenotazione.getIdPagamento());
            comando.setLong(5, prenotazione.getDataPartenzaViaggio());
            comando.setLong(6, prenotazione.getDataRientroViaggio());
            comando.setLong(7, prenotazione.getDataPrenotazione());
            comando.executeUpdate();
        }
    }

    @Override
    public Prenotazione trovaPerId(int id) throws PersistenzaException {
        try (PreparedStatement comando = pool.getConnessione().prepareStatement(SELECT_PER_ID)) {
            comando.setInt(1, id);
            try (ResultSet risultato = comando.executeQuery()) {
                return risultato.next() ? ricostruisci(risultato) : null;
            }
        } catch (SQLException e) {
            throw errore(e);
        }
    }

    private Prenotazione ricostruisci(ResultSet risultato) throws SQLException, PersistenzaException {
        int id = risultato.getInt("id");
        Utente cliente = utenteDAO.trovaPerId(risultato.getInt("id_utente"));
        Pacchetto pacchetto = pacchettoDAO.trovaPerId(risultato.getInt("id_pacchetto"));
        Pagamento pagamento = pagamentoDAO.trovaPerId(risultato.getInt("id_pagamento"));
        if (cliente == null || pacchetto == null || pagamento == null) {
            throw new PersistenzaException("La prenotazione " + id + " rimanda a dati non piu' presenti.");
        }

        List<Partecipante> partecipanti = partecipanteDAO.trovaPerPrenotazione(id);
        PeriodoViaggio periodo = new PeriodoViaggio(risultato.getLong("data_partenza"),
                risultato.getLong("data_rientro"));
        return Prenotazione.ricostruisci(id, cliente, pacchetto, pagamento, periodo, partecipanti,
                risultato.getLong("data_prenotazione"));
    }

    private PersistenzaException annullaESegnala(Connection connessione, Exception causa) {
        PersistenzaException errore =
                new PersistenzaException("Salvataggio della prenotazione non riuscito.", causa);
        try {
            connessione.rollback();
        } catch (SQLException e) {
            errore.addSuppressed(e);
        }
        return errore;
    }

    private void ripristinaAutoCommit(Connection connessione) {
        try {
            connessione.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Impossibile ripristinare l'auto-commit sulla connessione.", e);
        }
    }

    private PersistenzaException errore(SQLException causa) {
        return new PersistenzaException("Accesso alle prenotazioni non riuscito.", causa);
    }
}

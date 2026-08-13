package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import control.Catalogo;
import control.GestoreUtenti;
import exceptions.PersistenzaException;
import model.DatiAnagrafici;
import model.DettagliRicostruzionePrenotazione;
import model.Pacchetto;
import model.Prenotazione;
import model.StatoPrenotazione;
import model.Utente;
import payment.Pagamento;
import payment.PagamentoRegistrato;

public class PrenotazioneDAOMySQL implements PrenotazioneDAO {

    private static final String COLONNE_PRENOTAZIONE = "id, id_utente, id_pacchetto, data_partenza_viaggio, "
            + "data_rientro_viaggio, data_prenotazione, stato, descrizione_pagamento, costo_pagamento";
    private static final String COLONNE_PARTECIPANTE = "id_partecipante, id_prenotazione, nome, cognome, "
            + "data_nascita, codice_fiscale";

    private final String url;
    private final String username;
    private final String password;
    private final GestoreUtenti gestoreUtenti;

    public PrenotazioneDAOMySQL(String url, String username, String password, GestoreUtenti gestoreUtenti) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.gestoreUtenti = gestoreUtenti;
    }

    @Override
    public void salva(List<Prenotazione> prenotazioni) throws PersistenzaException {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            conn.setAutoCommit(false);
            riscriviTabelle(conn, prenotazioni);
        } catch (SQLException e) {
            throw new PersistenzaException("Impossibile salvare le prenotazioni sul database: " + e.getMessage(), e);
        }
    }

    private void riscriviTabelle(Connection conn, List<Prenotazione> prenotazioni) throws SQLException {
        String sqlPrenotazione = "INSERT INTO prenotazioni (" + COLONNE_PRENOTAZIONE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlPartecipante = "INSERT INTO prenotazioni_partecipanti (" + COLONNE_PARTECIPANTE + ") VALUES (?, ?, ?, ?, ?, ?)";

        try (Statement pulizia = conn.createStatement();
                PreparedStatement stmtPrenotazione = conn.prepareStatement(sqlPrenotazione);
                PreparedStatement stmtPartecipante = conn.prepareStatement(sqlPartecipante)) {

            // I partecipanti dipendono dalla prenotazione (FK): vanno ripuliti prima.
            pulizia.executeUpdate("DELETE FROM prenotazioni_partecipanti");
            pulizia.executeUpdate("DELETE FROM prenotazioni");

            boolean almenoUnPartecipante = false;
            for (Prenotazione p : prenotazioni) {
                stmtPrenotazione.setInt(1, p.getId());
                stmtPrenotazione.setInt(2, p.getDettagliUtente().getId());
                stmtPrenotazione.setInt(3, p.getDettagliPacchetto().getId());
                stmtPrenotazione.setLong(4, p.getDataPartenzaViaggio());
                stmtPrenotazione.setLong(5, p.getDataRientroViaggio());
                stmtPrenotazione.setLong(6, p.getDataPrenotazione());
                stmtPrenotazione.setString(7, p.getStato().name());
                stmtPrenotazione.setString(8, p.getDettagliPagamento().descrizione());
                stmtPrenotazione.setFloat(9, p.getDettagliPagamento().costo());
                stmtPrenotazione.addBatch();

                for (Utente partecipante : p.getDettagliPartecipanti()) {
                    stmtPartecipante.setInt(1, partecipante.getId());
                    stmtPartecipante.setInt(2, p.getId());
                    stmtPartecipante.setString(3, partecipante.getName());
                    stmtPartecipante.setString(4, partecipante.getSurname());
                    stmtPartecipante.setLong(5, partecipante.getDataNascita());
                    stmtPartecipante.setString(6, partecipante.getCodiceFiscale());
                    stmtPartecipante.addBatch();
                    almenoUnPartecipante = true;
                }
            }
            stmtPrenotazione.executeBatch();
            if (almenoUnPartecipante) {
                stmtPartecipante.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    @Override
    public List<Prenotazione> carica() throws PersistenzaException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        Map<Integer, List<Utente>> partecipantiPerPrenotazione = new LinkedHashMap<>();

        String sqlPartecipanti = "SELECT " + COLONNE_PARTECIPANTE + " FROM prenotazioni_partecipanti";
        String sqlPrenotazioni = "SELECT " + COLONNE_PRENOTAZIONE + " FROM prenotazioni";

        try (Connection conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(sqlPartecipanti)) {
                while (rs.next()) {
                    int idPrenotazione = rs.getInt("id_prenotazione");
                    DatiAnagrafici anagrafica = new DatiAnagrafici(rs.getLong("data_nascita"), rs.getString("codice_fiscale"));
                    Utente partecipante = new Utente(rs.getInt("id_partecipante"), "", rs.getString("nome"),
                            rs.getString("cognome"), "", "", anagrafica);
                    partecipantiPerPrenotazione.computeIfAbsent(idPrenotazione, k -> new ArrayList<>()).add(partecipante);
                }
            }

            try (ResultSet rs = stmt.executeQuery(sqlPrenotazioni)) {
                while (rs.next()) {
                    aggiungiSeRisolvibile(rs, partecipantiPerPrenotazione, prenotazioni);
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            throw new PersistenzaException("Impossibile leggere le prenotazioni dal database: " + e.getMessage(), e);
        }
        return prenotazioni;
    }

    private void aggiungiSeRisolvibile(ResultSet rs, Map<Integer, List<Utente>> partecipantiPerPrenotazione,
            List<Prenotazione> prenotazioni) throws SQLException {
        int id = rs.getInt("id");
        Utente utente = gestoreUtenti.getUtenteById(rs.getInt("id_utente"));
        Pacchetto pacchetto = Catalogo.getInstance().getPacchettoById(rs.getInt("id_pacchetto"));
        if (utente == null || pacchetto == null) {
            return;
        }

        Pagamento pagamento = new PagamentoRegistrato(rs.getString("descrizione_pagamento"),
                rs.getFloat("costo_pagamento"));
        List<Utente> partecipanti = partecipantiPerPrenotazione.getOrDefault(id, new ArrayList<>());

        DettagliRicostruzionePrenotazione dettagli = new DettagliRicostruzionePrenotazione(
                rs.getLong("data_partenza_viaggio"), rs.getLong("data_rientro_viaggio"),
                rs.getLong("data_prenotazione"), StatoPrenotazione.valueOf(rs.getString("stato")));
        prenotazioni.add(new Prenotazione(id, utente, pacchetto, pagamento, dettagli, partecipanti));
    }
}

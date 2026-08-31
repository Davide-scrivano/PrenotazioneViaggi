package dao.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

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

public class PrenotazioneDAOFileSystem implements PrenotazioneDAO {

    private final FileDati file;
    private final UtenteDAO utenteDAO;
    private final PacchettoDAO pacchettoDAO;
    private final PagamentoDAO pagamentoDAO;
    private final PartecipanteDAO partecipanteDAO;

    private int ultimoIdAssegnato = 0;
    private boolean contatoreInizializzato = false;

    public PrenotazioneDAOFileSystem(String percorso, UtenteDAO utenteDAO, PacchettoDAO pacchettoDAO,
            PagamentoDAO pagamentoDAO, PartecipanteDAO partecipanteDAO) {
        this.file = new FileDati(percorso);
        this.utenteDAO = utenteDAO;
        this.pacchettoDAO = pacchettoDAO;
        this.pagamentoDAO = pagamentoDAO;
        this.partecipanteDAO = partecipanteDAO;
    }

    @Override
    public synchronized int prossimoId() throws PersistenzaException {
        if (!contatoreInizializzato) {
            ultimoIdAssegnato = leggiIdMassimo();
            contatoreInizializzato = true;
        }
        return ++ultimoIdAssegnato;
    }

    @Override
    public void inserisci(Prenotazione prenotazione) throws PersistenzaException {
        pagamentoDAO.inserisci(prenotazione.getPagamento());
        for (Partecipante partecipante : prenotazione.getPartecipanti()) {
            partecipanteDAO.inserisci(partecipante, prenotazione.getId());
        }
        scriviRiga(prenotazione);
    }

    private void scriviRiga(Prenotazione prenotazione) throws PersistenzaException {
        try (DataOutputStream out = file.apriScrittura(true)) {
            out.writeInt(prenotazione.getId());
            out.writeInt(prenotazione.getCliente().getId());
            out.writeInt(prenotazione.getPacchetto().getId());
            out.writeInt(prenotazione.getPagamento().getId());
            out.writeLong(prenotazione.getPeriodo().getDataPartenza());
            out.writeLong(prenotazione.getPeriodo().getDataRientro());
            out.writeLong(prenotazione.getDataPrenotazione());
        } catch (IOException e) {
            throw errore(e);
        }
    }

    @Override
    public Prenotazione trovaPerId(int id) throws PersistenzaException {
        RigaPrenotazione riga = cercaRiga(id);
        return riga == null ? null : ricostruisci(riga);
    }

    private RigaPrenotazione cercaRiga(int id) throws PersistenzaException {
        if (!file.esiste()) {
            return null;
        }
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                RigaPrenotazione riga = leggiRiga(in);
                if (riga.id == id) {
                    return riga;
                }
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return null;
    }

    private Prenotazione ricostruisci(RigaPrenotazione riga) throws PersistenzaException {
        Utente cliente = utenteDAO.trovaPerId(riga.idUtente);
        Pacchetto pacchetto = pacchettoDAO.trovaPerId(riga.idPacchetto);
        Pagamento pagamento = pagamentoDAO.trovaPerId(riga.idPagamento);
        if (cliente == null || pacchetto == null || pagamento == null) {
            throw new PersistenzaException("La prenotazione " + riga.id + " rimanda a dati non piu' presenti.");
        }

        List<Partecipante> partecipanti = partecipanteDAO.trovaPerPrenotazione(riga.id);
        PeriodoViaggio periodo = new PeriodoViaggio(riga.dataPartenza, riga.dataRientro);
        return Prenotazione.ricostruisci(riga.id, cliente, pacchetto, pagamento, periodo, partecipanti,
                riga.dataPrenotazione);
    }

    private RigaPrenotazione leggiRiga(DataInputStream in) throws IOException {
        RigaPrenotazione riga = new RigaPrenotazione();
        riga.id = in.readInt();
        riga.idUtente = in.readInt();
        riga.idPacchetto = in.readInt();
        riga.idPagamento = in.readInt();
        riga.dataPartenza = in.readLong();
        riga.dataRientro = in.readLong();
        riga.dataPrenotazione = in.readLong();
        return riga;
    }

    private int leggiIdMassimo() throws PersistenzaException {
        if (!file.esiste()) {
            return 0;
        }
        int massimo = 0;
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                massimo = Math.max(massimo, leggiRiga(in).id);
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return massimo;
    }

    private PersistenzaException errore(IOException causa) {
        return new PersistenzaException("Archivio prenotazioni non accessibile (" + file.percorso() + ").", causa);
    }

    private static final class RigaPrenotazione {
        private int id;
        private int idUtente;
        private int idPacchetto;
        private int idPagamento;
        private long dataPartenza;
        private long dataRientro;
        private long dataPrenotazione;
    }
}

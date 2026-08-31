package dao.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dao.PartecipanteDAO;
import exceptions.PersistenzaException;
import model.valori.DatiAnagrafici;
import model.Partecipante;

public class PartecipanteDAOFileSystem implements PartecipanteDAO {

    private final FileDati file;
    private int ultimoIdAssegnato = 0;
    private boolean contatoreInizializzato = false;

    public PartecipanteDAOFileSystem(String percorso) {
        this.file = new FileDati(percorso);
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
    public void inserisci(Partecipante partecipante, int idPrenotazione) throws PersistenzaException {
        try (DataOutputStream out = file.apriScrittura(true)) {
            out.writeInt(partecipante.getId());
            out.writeInt(idPrenotazione);
            out.writeUTF(partecipante.getNome());
            out.writeUTF(partecipante.getCognome());
            out.writeLong(partecipante.getDataNascita());
            out.writeUTF(partecipante.getCodiceFiscale());
        } catch (IOException e) {
            throw errore(e);
        }
    }

    @Override
    public List<Partecipante> trovaPerPrenotazione(int idPrenotazione) throws PersistenzaException {
        List<Partecipante> partecipanti = new ArrayList<>();
        if (!file.esiste()) {
            return partecipanti;
        }
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                int id = in.readInt();
                int idPrenotazioneLetto = in.readInt();
                Partecipante partecipante = leggiDatiPersonali(in, id);
                if (idPrenotazioneLetto == idPrenotazione) {
                    partecipanti.add(partecipante);
                }
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return partecipanti;
    }

    private Partecipante leggiDatiPersonali(DataInputStream in, int id) throws IOException {
        String nome = in.readUTF();
        String cognome = in.readUTF();
        DatiAnagrafici anagrafica = new DatiAnagrafici(in.readLong(), in.readUTF());
        return new Partecipante(id, nome, cognome, anagrafica);
    }

    private int leggiIdMassimo() throws PersistenzaException {
        if (!file.esiste()) {
            return 0;
        }
        int massimo = 0;
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                int id = in.readInt();
                in.readInt();
                leggiDatiPersonali(in, id);
                massimo = Math.max(massimo, id);
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return massimo;
    }

    private PersistenzaException errore(IOException causa) {
        return new PersistenzaException("Archivio partecipanti non accessibile (" + file.percorso() + ").", causa);
    }
}

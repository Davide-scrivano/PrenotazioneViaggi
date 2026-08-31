package dao.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import dao.DatiIniziali;
import dao.UtenteDAO;
import exceptions.PersistenzaException;
import model.valori.TipoUtente;
import model.Utente;

public class UtenteDAOFileSystem implements UtenteDAO {

    private final FileDati file;

    public UtenteDAOFileSystem(String percorso) {
        this.file = new FileDati(percorso);
    }

    @Override
    public Utente trovaPerId(int id) throws PersistenzaException {
        creaArchivioSeAssente();
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                Utente utente = leggi(in);
                if (utente.getId() == id) {
                    return utente;
                }
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return null;
    }

    @Override
    public Utente trovaPerNickname(String nickname) throws PersistenzaException {
        creaArchivioSeAssente();
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                Utente utente = leggi(in);
                if (utente.getNickname().equals(nickname)) {
                    return utente;
                }
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return null;
    }

    private Utente leggi(DataInputStream in) throws IOException {
        return new Utente(in.readInt(), in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF(),
                TipoUtente.daCodice(in.readUTF()));
    }

    private void scrivi(DataOutputStream out, Utente utente) throws IOException {
        out.writeInt(utente.getId());
        out.writeUTF(utente.getNickname());
        out.writeUTF(utente.getNome());
        out.writeUTF(utente.getCognome());
        out.writeUTF(utente.getEmail());
        out.writeUTF(utente.getPassword());
        out.writeUTF(utente.getTipo().name());
    }

    private void creaArchivioSeAssente() throws PersistenzaException {
        if (file.esiste()) {
            return;
        }
        List<Utente> iniziali = DatiIniziali.utenti();
        try (DataOutputStream out = file.apriScrittura(false)) {
            for (Utente utente : iniziali) {
                scrivi(out, utente);
            }
        } catch (IOException e) {
            throw errore(e);
        }
    }

    private PersistenzaException errore(IOException causa) {
        return new PersistenzaException("Archivio utenti non accessibile (" + file.percorso() + ").", causa);
    }
}

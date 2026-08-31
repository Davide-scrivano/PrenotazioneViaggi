package dao.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import dao.CatalogoDAO;
import dao.DatiIniziali;
import dao.PacchettoDAO;
import exceptions.PersistenzaException;
import model.Catalogo;

public class CatalogoDAOFileSystem implements CatalogoDAO {

    private final FileDati file;
    private final PacchettoDAO pacchettoDAO;

    public CatalogoDAOFileSystem(String percorso, PacchettoDAO pacchettoDAO) {
        this.file = new FileDati(percorso);
        this.pacchettoDAO = pacchettoDAO;
    }

    @Override
    public Catalogo carica() throws PersistenzaException {
        creaArchivioSeAssente();
        try (DataInputStream in = file.apriLettura()) {
            int id = in.readInt();
            String titolo = in.readUTF();
            return new Catalogo(id, titolo, pacchettoDAO.trovaTutti());
        } catch (IOException e) {
            throw errore(e);
        }
    }

    private void creaArchivioSeAssente() throws PersistenzaException {
        if (file.esiste()) {
            return;
        }
        try (DataOutputStream out = file.apriScrittura(false)) {
            out.writeInt(DatiIniziali.ID_CATALOGO);
            out.writeUTF(DatiIniziali.TITOLO_CATALOGO);
        } catch (IOException e) {
            throw errore(e);
        }
    }

    private PersistenzaException errore(IOException causa) {
        return new PersistenzaException("Archivio catalogo non accessibile (" + file.percorso() + ").", causa);
    }
}

package dao.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import dao.PagamentoDAO;
import exceptions.PersistenzaException;
import model.Pagamento;

public class PagamentoDAOFileSystem implements PagamentoDAO {

    private final FileDati file;
    private int ultimoIdAssegnato = 0;
    private boolean contatoreInizializzato = false;

    public PagamentoDAOFileSystem(String percorso) {
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
    public void inserisci(Pagamento pagamento) throws PersistenzaException {
        try (DataOutputStream out = file.apriScrittura(true)) {
            out.writeInt(pagamento.getId());
            out.writeUTF(pagamento.getMetodo());
            out.writeFloat(pagamento.getImporto());
            out.writeUTF(pagamento.getCodiceAutorizzazione());
            out.writeLong(pagamento.getDataEsecuzione());
        } catch (IOException e) {
            throw errore(e);
        }
    }

    @Override
    public Pagamento trovaPerId(int id) throws PersistenzaException {
        if (!file.esiste()) {
            return null;
        }
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                Pagamento pagamento = leggi(in);
                if (pagamento.getId() == id) {
                    return pagamento;
                }
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return null;
    }

    private Pagamento leggi(DataInputStream in) throws IOException {
        return new Pagamento(in.readInt(), in.readUTF(), in.readFloat(), in.readUTF(), in.readLong());
    }

    private int leggiIdMassimo() throws PersistenzaException {
        if (!file.esiste()) {
            return 0;
        }
        int massimo = 0;
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                massimo = Math.max(massimo, leggi(in).getId());
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return massimo;
    }

    private PersistenzaException errore(IOException causa) {
        return new PersistenzaException("Archivio pagamenti non accessibile (" + file.percorso() + ").", causa);
    }
}

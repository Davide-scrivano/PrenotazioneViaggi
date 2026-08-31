package dao.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dao.DatiIniziali;
import dao.PacchettoDAO;
import exceptions.PersistenzaException;
import model.valori.DettagliOfferta;
import model.Pacchetto;
import model.valori.PeriodoViaggio;
import model.valori.TipoVolo;

public class PacchettoDAOFileSystem implements PacchettoDAO {

    private final FileDati file;

    public PacchettoDAOFileSystem(String percorso) {
        this.file = new FileDati(percorso);
    }

    @Override
    public List<Pacchetto> trovaTutti() throws PersistenzaException {
        creaArchivioSeAssente();
        List<Pacchetto> pacchetti = new ArrayList<>();
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                pacchetti.add(leggi(in));
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return pacchetti;
    }

    @Override
    public Pacchetto trovaPerId(int id) throws PersistenzaException {
        creaArchivioSeAssente();
        try (DataInputStream in = file.apriLettura()) {
            while (in.available() > 0) {
                Pacchetto pacchetto = leggi(in);
                if (pacchetto.getId() == id) {
                    return pacchetto;
                }
            }
        } catch (IOException e) {
            throw errore(e);
        }
        return null;
    }

    @Override
    public void aggiorna(Pacchetto pacchetto) throws PersistenzaException {
        List<Pacchetto> pacchetti = trovaTutti();
        try (DataOutputStream out = file.apriScrittura(false)) {
            for (Pacchetto salvato : pacchetti) {
                scrivi(out, salvato.getId() == pacchetto.getId() ? pacchetto : salvato);
            }
        } catch (IOException e) {
            throw errore(e);
        }
    }

    private Pacchetto leggi(DataInputStream in) throws IOException {
        int id = in.readInt();
        String destinazione = in.readUTF();
        PeriodoViaggio disponibilita = new PeriodoViaggio(in.readLong(), in.readLong());
        float prezzo = in.readFloat();
        int posti = in.readInt();
        DettagliOfferta offerta = new DettagliOfferta(in.readInt(), TipoVolo.daCodice(in.readUTF()));
        return new Pacchetto(id, destinazione, disponibilita, prezzo, posti, offerta);
    }

    private void scrivi(DataOutputStream out, Pacchetto pacchetto) throws IOException {
        out.writeInt(pacchetto.getId());
        out.writeUTF(pacchetto.getDestinazione());
        out.writeLong(pacchetto.getDisponibilita().getDataPartenza());
        out.writeLong(pacchetto.getDisponibilita().getDataRientro());
        out.writeFloat(pacchetto.getPrezzoSettimanale());
        out.writeInt(pacchetto.getPostiDisponibili());
        out.writeInt(pacchetto.getStelleHotel());
        out.writeUTF(pacchetto.getTipoVolo().name());
    }

    private void creaArchivioSeAssente() throws PersistenzaException {
        if (file.esiste()) {
            return;
        }
        List<Pacchetto> iniziali = DatiIniziali.pacchetti();
        try (DataOutputStream out = file.apriScrittura(false)) {
            for (Pacchetto pacchetto : iniziali) {
                scrivi(out, pacchetto);
            }
        } catch (IOException e) {
            throw errore(e);
        }
    }

    private PersistenzaException errore(IOException causa) {
        return new PersistenzaException("Archivio pacchetti non accessibile (" + file.percorso() + ").", causa);
    }
}

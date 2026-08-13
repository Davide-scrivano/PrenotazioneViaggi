package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import exceptions.PersistenzaException;
import model.DettagliOfferta;
import model.Pacchetto;
import model.TipoVolo;

public class PacchettoDAOFileSystem implements PacchettoDAO {

    private static final int NUMERO_CAMPI = 8;
    private static final String SEPARATORE = ";";

    private final String percorsoFile;

    public PacchettoDAOFileSystem(String percorsoFile) {
        this.percorsoFile = percorsoFile;
    }

    @Override
    public void salva(List<Pacchetto> pacchetti) throws PersistenzaException {
        File file = new File(percorsoFile);
        preparaCartella(file);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Pacchetto p : pacchetti) {
                writer.write(p.getId() + SEPARATORE + p.getDestinazione() + SEPARATORE
                        + p.getDataPartenza() + SEPARATORE + p.getDataRientro() + SEPARATORE
                        + p.getPrezzo() + SEPARATORE + p.getPostiDisponibili() + SEPARATORE
                        + p.getStelleHotel() + SEPARATORE + p.getTipoVolo().name());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile salvare i pacchetti su file: " + e.getMessage(), e);
        }
    }

    private void preparaCartella(File file) throws PersistenzaException {
        File cartella = file.getParentFile();
        if (cartella != null && !cartella.exists() && !cartella.mkdirs()) {
            throw new PersistenzaException("Impossibile creare la cartella " + cartella.getPath());
        }
    }

    @Override
    public List<Pacchetto> carica() throws PersistenzaException {
        List<Pacchetto> pacchetti = new ArrayList<>();
        File file = new File(percorsoFile);

        if (!file.exists()) {
            // Primo avvio: nessun file salvato non e' un errore.
            return pacchetti;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                Pacchetto pacchetto = leggiRiga(riga);
                if (pacchetto != null) {
                    pacchetti.add(pacchetto);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new PersistenzaException("Impossibile leggere i pacchetti dal file: " + e.getMessage(), e);
        }
        return pacchetti;
    }

    private Pacchetto leggiRiga(String riga) {
        if (riga.isEmpty()) {
            return null;
        }
        String[] campi = riga.split(SEPARATORE, -1);
        if (campi.length != NUMERO_CAMPI) {
            return null;
        }
        try {
            return new Pacchetto(Integer.parseInt(campi[0]), campi[1],
                    Long.parseLong(campi[2]), Long.parseLong(campi[3]),
                    Float.parseFloat(campi[4]), Integer.parseInt(campi[5]),
                    new DettagliOfferta(Integer.parseInt(campi[6]), TipoVolo.valueOf(campi[7])));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

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
import model.TipoUtente;
import model.Utente;

public class UtenteDAOFileSystem implements UtenteDAO {

    private static final int NUMERO_CAMPI = 7;
    private static final String SEPARATORE = ";";

    private final String percorsoFile;

    public UtenteDAOFileSystem(String percorsoFile) {
        this.percorsoFile = percorsoFile;
    }

    @Override
    public void salva(List<Utente> utenti) throws PersistenzaException {
        File file = new File(percorsoFile);
        preparaCartella(file);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Utente u : utenti) {
                writer.write(u.getId() + SEPARATORE + u.getNickname() + SEPARATORE + u.getName() + SEPARATORE
                        + u.getSurname() + SEPARATORE + u.getEmail() + SEPARATORE + u.getPassword() + SEPARATORE
                        + u.getTipo().name());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile salvare gli utenti su file: " + e.getMessage(), e);
        }
    }

    private void preparaCartella(File file) throws PersistenzaException {
        File cartella = file.getParentFile();
        if (cartella != null && !cartella.exists() && !cartella.mkdirs()) {
            throw new PersistenzaException("Impossibile creare la cartella " + cartella.getPath());
        }
    }

    @Override
    public List<Utente> carica() throws PersistenzaException {
        List<Utente> utenti = new ArrayList<>();
        File file = new File(percorsoFile);

        if (!file.exists()) {
            return utenti;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                Utente utente = leggiRiga(riga);
                if (utente != null) {
                    utenti.add(utente);
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new PersistenzaException("Impossibile leggere gli utenti dal file: " + e.getMessage(), e);
        }
        return utenti;
    }

    private Utente leggiRiga(String riga) {
        if (riga.isEmpty()) {
            return null;
        }
        String[] campi = riga.split(SEPARATORE, -1);
        if (campi.length != NUMERO_CAMPI) {
            return null;
        }
        try {
            return new Utente(Integer.parseInt(campi[0]), campi[1], campi[2], campi[3], campi[4], campi[5],
                    TipoUtente.daCodice(campi[6]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

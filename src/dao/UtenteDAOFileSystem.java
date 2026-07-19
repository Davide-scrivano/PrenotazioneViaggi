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
import model.Utente;

/**
 * Versione "file system" del DAO: salva gli utenti in un semplice file
 * di testo (un utente per riga, campi separati da ';').
 * Usata nella modalita' full-version quando non si vuole/puo' usare un DBMS.
 */
public class UtenteDAOFileSystem implements UtenteDAO {

    private String percorsoFile;

    public UtenteDAOFileSystem(String percorsoFile) {
        this.percorsoFile = percorsoFile;
    }

    @Override
    public void salva(List<Utente> utenti) throws PersistenzaException {
        File file = new File(percorsoFile);
        File cartella = file.getParentFile();
        if (cartella != null && !cartella.exists()) {
            cartella.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Utente u : utenti) {
                writer.write(u.getId() + ";" + u.getNickname() + ";" + u.getName() + ";"
                        + u.getSurname() + ";" + u.getEmail() + ";" + u.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile salvare gli utenti su file: " + e.getMessage());
        }
    }

    @Override
    public List<Utente> carica() throws PersistenzaException {
        List<Utente> utenti = new ArrayList<>();
        File file = new File(percorsoFile);

        if (!file.exists()) {
            // Nessun file precedente: non e' un errore, semplicemente non ci sono ancora dati.
            return utenti;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                if (riga.isEmpty()) {
                    continue;
                }
                String[] campi = riga.split(";", -1);
                if (campi.length == 6) {
                    int id = Integer.parseInt(campi[0]);
                    utenti.add(new Utente(id, campi[1], campi[2], campi[3], campi[4], campi[5]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new PersistenzaException("Impossibile leggere gli utenti dal file: " + e.getMessage());
        }
        return utenti;
    }
}

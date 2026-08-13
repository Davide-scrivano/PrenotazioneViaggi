package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class PrenotazioneDAOFileSystem implements PrenotazioneDAO {

    private static final int NUMERO_CAMPI = 10;
    private static final int NUMERO_CAMPI_PARTECIPANTE = 5;
    private static final String SEPARATORE = ";";
    private static final String SEPARATORE_PARTECIPANTI = ",";
    private static final String SEPARATORE_CAMPI_PARTECIPANTE = "#";

    private final String percorsoFile;
    private final GestoreUtenti gestoreUtenti;

    public PrenotazioneDAOFileSystem(String percorsoFile, GestoreUtenti gestoreUtenti) {
        this.percorsoFile = percorsoFile;
        this.gestoreUtenti = gestoreUtenti;
    }

    @Override
    public void salva(List<Prenotazione> prenotazioni) throws PersistenzaException {
        File file = new File(percorsoFile);
        preparaCartella(file);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Prenotazione p : prenotazioni) {
                writer.write(formattaRiga(p));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile salvare le prenotazioni su file: " + e.getMessage(), e);
        }
    }

    private String formattaRiga(Prenotazione p) {
        return p.getId() + SEPARATORE + p.getDettagliUtente().getId() + SEPARATORE
                + p.getDettagliPacchetto().getId() + SEPARATORE + p.getDataPartenzaViaggio() + SEPARATORE
                + p.getDataRientroViaggio() + SEPARATORE + p.getDataPrenotazione() + SEPARATORE
                + p.getStato().name() + SEPARATORE + p.getDettagliPagamento().descrizione() + SEPARATORE
                + p.getDettagliPagamento().costo() + SEPARATORE + formattaPartecipanti(p);
    }

    private String formattaPartecipanti(Prenotazione p) {
        StringBuilder sb = new StringBuilder();
        for (Utente partecipante : p.getDettagliPartecipanti()) {
            if (sb.length() > 0) {
                sb.append(SEPARATORE_PARTECIPANTI);
            }
            sb.append(partecipante.getId()).append(SEPARATORE_CAMPI_PARTECIPANTE)
                    .append(partecipante.getName()).append(SEPARATORE_CAMPI_PARTECIPANTE)
                    .append(partecipante.getSurname()).append(SEPARATORE_CAMPI_PARTECIPANTE)
                    .append(partecipante.getDataNascita()).append(SEPARATORE_CAMPI_PARTECIPANTE)
                    .append(partecipante.getCodiceFiscale());
        }
        return sb.toString();
    }

    private void preparaCartella(File file) throws PersistenzaException {
        File cartella = file.getParentFile();
        if (cartella != null && !cartella.exists() && !cartella.mkdirs()) {
            throw new PersistenzaException("Impossibile creare la cartella " + cartella.getPath());
        }
    }

    @Override
    public List<Prenotazione> carica() throws PersistenzaException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        File file = new File(percorsoFile);

        if (!file.exists()) {
            // Primo avvio: nessun file salvato non e' un errore.
            return prenotazioni;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                Prenotazione prenotazione = leggiRiga(riga);
                if (prenotazione != null) {
                    prenotazioni.add(prenotazione);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new PersistenzaException("Impossibile leggere le prenotazioni dal file: " + e.getMessage(), e);
        }
        return prenotazioni;
    }

    private Prenotazione leggiRiga(String riga) {
        if (riga.isEmpty()) {
            return null;
        }
        String[] campi = riga.split(SEPARATORE, -1);
        if (campi.length != NUMERO_CAMPI) {
            return null;
        }
        try {
            Utente utente = gestoreUtenti.getUtenteById(Integer.parseInt(campi[1]));
            Pacchetto pacchetto = Catalogo.getInstance().getPacchettoById(Integer.parseInt(campi[2]));
            if (utente == null || pacchetto == null) {
                return null;
            }

            Pagamento pagamento = new PagamentoRegistrato(campi[7], Float.parseFloat(campi[8]));
            List<Utente> partecipanti = leggiPartecipanti(campi[9]);

            DettagliRicostruzionePrenotazione dettagli = new DettagliRicostruzionePrenotazione(
                    Long.parseLong(campi[3]), Long.parseLong(campi[4]), Long.parseLong(campi[5]),
                    StatoPrenotazione.valueOf(campi[6]));
            return new Prenotazione(Integer.parseInt(campi[0]), utente, pacchetto, pagamento, dettagli,
                    partecipanti);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private List<Utente> leggiPartecipanti(String testo) {
        List<Utente> partecipanti = new ArrayList<>();
        if (testo.isEmpty()) {
            return partecipanti;
        }
        for (String voce : testo.split(SEPARATORE_PARTECIPANTI, -1)) {
            String[] campi = voce.split(SEPARATORE_CAMPI_PARTECIPANTE, -1);
            if (campi.length != NUMERO_CAMPI_PARTECIPANTE) {
                throw new IllegalArgumentException("Partecipante malformato: " + voce);
            }
            DatiAnagrafici anagrafica = new DatiAnagrafici(Long.parseLong(campi[3]), campi[4]);
            partecipanti.add(new Utente(Integer.parseInt(campi[0]), "", campi[1], campi[2], "", "", anagrafica));
        }
        return partecipanti;
    }
}

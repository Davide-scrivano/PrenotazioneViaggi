package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import bean.PacchettoBean;
import dao.PacchettoDAO;
import exceptions.PersistenzaException;
import model.DettagliOfferta;
import model.Pacchetto;
import model.TipoVolo;

public class Catalogo {

    private static final Logger LOGGER = Logger.getLogger(Catalogo.class.getName());

    private static final int PRIMO_ID_PACCHETTO_AGENZIA = 1000;

    private final List<Pacchetto> pacchetti = new ArrayList<>();
    private int prossimoIdNuovoPacchetto = PRIMO_ID_PACCHETTO_AGENZIA;
    private PacchettoDAO dao;

    private Catalogo() {
        // Singleton: si accede solo tramite getInstance().
    }

    private static class Holder {
        private static final Catalogo ISTANZA = new Catalogo();
    }

    public static Catalogo getInstance() {
        return Holder.ISTANZA;
    }

    public void attivaPersistenza(PacchettoDAO dao) {
        this.dao = dao;
        try {
            List<Pacchetto> salvati = dao.carica();
            this.pacchetti.addAll(salvati);
            for (Pacchetto p : salvati) {
                if (p.getId() >= prossimoIdNuovoPacchetto) {
                    prossimoIdNuovoPacchetto = p.getId() + 1;
                }
            }
        } catch (PersistenzaException e) {
            LOGGER.log(Level.WARNING, "Impossibile caricare il catalogo salvato, si riparte da zero: {0}", e.getMessage());
        }
    }

    private void salvaSeNecessario() {
        if (dao == null) {
            return;
        }
        try {
            dao.salva(pacchetti);
        } catch (PersistenzaException e) {
            LOGGER.log(Level.WARNING, "Impossibile salvare il catalogo: {0}", e.getMessage());
        }
    }

    public List<Pacchetto> pacchettiDisponibili() {
        return Collections.unmodifiableList(pacchetti);
    }

    public int aggiungiPacchetto(PacchettoBean dati) {
        int id = prossimoIdNuovoPacchetto++;
        aggiungiPacchetto(new Pacchetto(id, dati.getDestinazione(), dati.getDataPartenza(), dati.getDataRientro(),
                dati.getPrezzo(), dati.getPosti(),
                new DettagliOfferta(dati.getStelleHotel(), TipoVolo.daCodice(dati.getTipoVolo()))));
        return id;
    }

    public void aggiungiPacchetto(Pacchetto pacchetto) {
        this.pacchetti.add(pacchetto);
        if (pacchetto.getId() >= prossimoIdNuovoPacchetto) {
            prossimoIdNuovoPacchetto = pacchetto.getId() + 1;
        }
        salvaSeNecessario();
    }

    public boolean rimuoviPacchetto(int id) {
        boolean rimosso = pacchetti.removeIf(p -> p.getId() == id);
        if (rimosso) {
            salvaSeNecessario();
        }
        return rimosso;
    }

    public boolean modificaPacchetto(int id, PacchettoBean dati) {
        Pacchetto pacchetto = getPacchettoById(id);
        if (pacchetto == null) {
            return false;
        }
        pacchetto.aggiorna(dati.getDestinazione(), dati.getDataPartenza(), dati.getDataRientro(),
                dati.getPrezzo(), dati.getPosti(),
                new DettagliOfferta(dati.getStelleHotel(), TipoVolo.daCodice(dati.getTipoVolo())));
        salvaSeNecessario();
        return true;
    }

    public Pacchetto getPacchettoById(int id) {
        for (Pacchetto p : pacchetti) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Pacchetto> ricercaPerData(long data) {
        List<Pacchetto> risultato = new ArrayList<>();
        for (Pacchetto p : pacchetti) {
            if (p.getDataPartenza() <= data && p.getDataRientro() >= data) {
                risultato.add(p);
            }
        }
        return risultato;
    }

    public List<Pacchetto> ricercaPerDestinazione(String testo) {
        if (testo == null || testo.isBlank()) {
            return pacchettiDisponibili();
        }

        String testoNormalizzato = testo.trim().toLowerCase();
        List<Pacchetto> risultato = new ArrayList<>();
        for (Pacchetto p : pacchetti) {
            if (p.getDestinazione().toLowerCase().contains(testoNormalizzato)) {
                risultato.add(p);
            }
        }
        return risultato;
    }
}

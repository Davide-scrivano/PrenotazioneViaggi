package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.PagamentoRifiutatoException;
import payment.Pagamento;

/**
 * Coordina Catalogo, Utente e Pagamento per realizzare i casi d'uso
 * "make a reservation", "cancel a reservation", "modify a reservation"
 * e "view reservations" del diagramma.
 *
 * Pattern Singleton: unico punto di accesso a tutte le prenotazioni del sistema.
 */
public class GestorePrenotazioni {

    private static GestorePrenotazioni instance;

    private List<Prenotazione> prenotazioni;
    private int prossimoId;

    private GestorePrenotazioni() {
        this.prenotazioni = new ArrayList<>();
        this.prossimoId = 1;
    }

    public static GestorePrenotazioni getInstance() {
        if (instance == null) {
            instance = new GestorePrenotazioni();
        }
        return instance;
    }

    /**
     * Crea la prenotazione solo se il pagamento va a buon fine.
     * Se il gateway rifiuta il pagamento, la prenotazione NON viene creata
     * e il pacchetto resta libero: l'eccezione non viene semplicemente
     * rilanciata, ma usata per evitare di lasciare il sistema in uno
     * stato incoerente (nessuna prenotazione "fantasma" senza pagamento).
     */
    public Prenotazione creaPrenotazione(Utente utente, Pacchetto pacchetto, Pagamento pagamento, long dataViaggio)
            throws PagamentoRifiutatoException {

        boolean pagamentoOk = pagamento.metodoPagamento();
        if (!pagamentoOk) {
            System.out.println("Pagamento rifiutato per l'utente " + utente.getNickname()
                    + " sul pacchetto " + pacchetto.getDestinazione());
            throw new PagamentoRifiutatoException("Il pagamento non e' stato autorizzato. Prenotazione annullata.");
        }

        Prenotazione prenotazione = new Prenotazione(prossimoId++, utente, pacchetto, pagamento, dataViaggio);
        prenotazioni.add(prenotazione);
        utente.aggiungiPrenotazione(prenotazione);
        return prenotazione;
    }

    public boolean annullaPrenotazione(int idPrenotazione) {
        Prenotazione p = getPrenotazioneById(idPrenotazione);
        if (p == null) {
            return false;
        }
        p.annulla();
        return true;
    }

    public boolean modificaPrenotazione(int idPrenotazione, Pacchetto nuovoPacchetto) {
        Prenotazione p = getPrenotazioneById(idPrenotazione);
        if (p == null || p.getStato() == StatoPrenotazione.ANNULLATA) {
            return false;
        }
        p.modificaPacchetto(nuovoPacchetto);
        return true;
    }

    public List<Prenotazione> getPrenotazioniUtente(Utente utente) {
        return utente.getPrenotazioniEffettuate();
    }

    public Prenotazione getPrenotazioneById(int idPrenotazione) {
        for (Prenotazione p : prenotazioni) {
            if (p.getId() == idPrenotazione) {
                return p;
            }
        }
        return null;
    }

    public List<Prenotazione> getTutteLePrenotazioni() {
        return prenotazioni;
    }
}

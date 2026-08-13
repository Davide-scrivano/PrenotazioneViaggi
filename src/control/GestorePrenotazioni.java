package control;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import bean.PartecipanteBean;
import bean.PrenotazioneBean;
import dao.PrenotazioneDAO;
import exceptions.OperazioneNonConsentitaException;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PagamentoRifiutatoException;
import exceptions.PersistenzaException;
import model.DatiAnagrafici;
import model.DurataViaggio;
import model.Pacchetto;
import model.Prenotazione;
import model.Utente;
import payment.AdapterPagamento;
import payment.DatiPagamento;
import payment.MetodoPagamento;
import payment.Pagamento;
import payment.PagamentoEsternoGateway;
import payment.PagamentoFactory;

public class GestorePrenotazioni {

    private static final Logger LOGGER = Logger.getLogger(GestorePrenotazioni.class.getName());
    private static final long MILLISECONDI_GIORNO = 24L * 60 * 60 * 1000;

    // Apertura comune dei messaggi che citano un pacchetto per nome.
    private static final String PREFISSO_PACCHETTO = "Il pacchetto \"";
    private static final String CHIUSURA_VIRGOLETTE = "\" ";

    private static final DateTimeFormatter FORMATO_DATA_NASCITA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final List<Prenotazione> prenotazioni = new ArrayList<>();
    private final List<OsservatorePosti> osservatoriPosti = new ArrayList<>();
    private final PagamentoFactory pagamentoFactory = new PagamentoFactory();
    private PrenotazioneDAO dao;
    private int prossimoId = 1;

    // Id negativi per i partecipanti (non sono utenti registrati): cosi' non
    // collidono mai fra loro ne' con gli id reali assegnati da GestoreUtenti,
    // che partono da 1. Utente.equals() si basa solo sull'id.
    private int prossimoIdPartecipante = -1;

    public GestorePrenotazioni(GestoreListaAttesa gestoreListaAttesa) {
        registraOsservatorePosti(gestoreListaAttesa);
    }

    public void registraOsservatorePosti(OsservatorePosti osservatore) {
        osservatoriPosti.add(osservatore);
    }

    public void attivaPersistenza(PrenotazioneDAO dao) {
        this.dao = dao;
        try {
            for (Prenotazione salvata : dao.carica()) {
                prenotazioni.add(salvata);
                salvata.getDettagliUtente().aggiungiPrenotazione(salvata);
                if (salvata.getId() >= prossimoId) {
                    prossimoId = salvata.getId() + 1;
                }
                for (Utente partecipante : salvata.getDettagliPartecipanti()) {
                    if (partecipante.getId() <= prossimoIdPartecipante) {
                        prossimoIdPartecipante = partecipante.getId() - 1;
                    }
                }
            }
        } catch (PersistenzaException e) {
            LOGGER.log(Level.WARNING, "Impossibile caricare le prenotazioni salvate, si riparte da zero: {0}", e.getMessage());
        }
    }

    private void salvaSeNecessario() {
        if (dao == null) {
            return;
        }
        try {
            dao.salva(prenotazioni);
        } catch (PersistenzaException e) {
            LOGGER.log(Level.WARNING, "Impossibile salvare le prenotazioni: {0}", e.getMessage());
        }
    }

    private void notificaPostiLiberati(Pacchetto pacchetto, int numeroPosti) {
        for (OsservatorePosti osservatore : osservatoriPosti) {
            osservatore.postiLiberati(pacchetto, numeroPosti);
        }
    }

    public Prenotazione compilaPrenotazione(Utente utente, PrenotazioneBean dati)
            throws PagamentoRifiutatoException, PacchettoNonDisponibileException {

        Pacchetto pacchetto = risolviPacchetto(dati.getIdPacchetto());
        DurataViaggio durata = durataDaSettimane(dati.getSettimaneSoggiorno());
        List<Utente> partecipanti = costruisciPartecipanti(dati.getPartecipanti());

        long dataPartenzaViaggio = dati.getDataPartenzaViaggio();
        long dataRientroViaggio = calcolaDataRientro(dataPartenzaViaggio, durata);
        verificaDisponibilita(pacchetto, partecipanti.size(), dataPartenzaViaggio, dataRientroViaggio);

        float prezzoTotale = pacchetto.calcolaPrezzoTotale(partecipanti.size(), durata);
        Pagamento pagamento = creaPagamento(dati, prezzoTotale);
        eseguiPagamento(utente, pacchetto, pagamento);

        pacchetto.occupaPosti(partecipanti.size());
        Prenotazione prenotazione = salvaPrenotazione(utente, pacchetto, partecipanti, pagamento,
                dataPartenzaViaggio, dataRientroViaggio);

        CompletableFuture.runAsync(() -> notificaPrenotazione(utente, prenotazione));

        return prenotazione;
    }

    public float calcolaPreventivo(PrenotazioneBean dati) throws PacchettoNonDisponibileException {
        Pacchetto pacchetto = risolviPacchetto(dati.getIdPacchetto());
        DurataViaggio durata = durataDaSettimane(dati.getSettimaneSoggiorno());
        int numeroPartecipanti = dati.getPartecipanti().size();

        long dataPartenzaViaggio = dati.getDataPartenzaViaggio();
        verificaDisponibilita(pacchetto, numeroPartecipanti, dataPartenzaViaggio,
                calcolaDataRientro(dataPartenzaViaggio, durata));

        return pacchetto.calcolaPrezzoTotale(numeroPartecipanti, durata);
    }

    private Pacchetto risolviPacchetto(int idPacchetto) throws PacchettoNonDisponibileException {
        Pacchetto pacchetto = Catalogo.getInstance().getPacchettoById(idPacchetto);
        if (pacchetto == null) {
            throw new PacchettoNonDisponibileException("Il pacchetto scelto non e' piu' disponibile a catalogo.");
        }
        return pacchetto;
    }

    private long calcolaDataRientro(long dataPartenzaViaggio, DurataViaggio durata) {
        return dataPartenzaViaggio + durata.getGiorni() * MILLISECONDI_GIORNO;
    }

    private DurataViaggio durataDaSettimane(int settimane) {
        DurataViaggio durata = DurataViaggio.daSettimane(settimane);
        return durata != null ? durata : DurataViaggio.UNA_SETTIMANA;
    }

    private List<Utente> costruisciPartecipanti(List<PartecipanteBean> datiPartecipanti) {
        List<Utente> partecipanti = new ArrayList<>();
        for (PartecipanteBean p : datiPartecipanti) {
            DatiAnagrafici anagrafica = new DatiAnagrafici(leggiDataNascita(p.getDataNascita()), p.getCodiceFiscale());
            partecipanti.add(new Utente(prossimoIdPartecipante--, "", p.getNome(), p.getCognome(), "", "", anagrafica));
        }
        return partecipanti;
    }

    private long leggiDataNascita(String testo) {
        if (testo == null || testo.isBlank()) {
            return 0L;
        }
        try {
            return LocalDate.parse(testo.trim(), FORMATO_DATA_NASCITA)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            return 0L;
        }
    }

    private Pagamento creaPagamento(PrenotazioneBean dati, float prezzoTotale)
            throws PagamentoRifiutatoException {
        MetodoPagamento metodo = MetodoPagamento.daCodice(dati.getMetodoPagamento());
        if (metodo == null) {
            throw new PagamentoRifiutatoException(
                    "Metodo di pagamento non riconosciuto: scegli carta di credito o PayPal.");
        }
        DatiPagamento datiPagamento = metodo == MetodoPagamento.PAYPAL
                ? DatiPagamento.perPaypal(dati.getEmailPaypal(), dati.getPasswordPaypal())
                : DatiPagamento.perCarta(dati.getNumeroCarta(), dati.getTitolare(), dati.getScadenza(), dati.getCvv());
        return pagamentoFactory.crea(metodo, datiPagamento, prezzoTotale);
    }

    private void verificaDisponibilita(Pacchetto pacchetto, int numeroPartecipanti, long dataPartenzaViaggio,
            long dataRientroViaggio) throws PacchettoNonDisponibileException {
        if (!pacchetto.isDisponibile(numeroPartecipanti)) {
            throw new PacchettoNonDisponibileException(PREFISSO_PACCHETTO + pacchetto.getDestinazione()
                    + CHIUSURA_VIRGOLETTE + "non ha abbastanza posti disponibili per "
                    + numeroPartecipanti + " partecipanti.");
        }
        if (dataPartenzaViaggio < pacchetto.getDataPartenza() || dataRientroViaggio > pacchetto.getDataRientro()) {
            throw new PacchettoNonDisponibileException("Le date scelte sono fuori dal periodo disponibile per il pacchetto \""
                    + pacchetto.getDestinazione() + "\".");
        }
    }

    private void eseguiPagamento(Utente utente, Pacchetto pacchetto, Pagamento pagamento)
            throws PagamentoRifiutatoException {
        if (!pagamento.elaboraPagamento()) {
            LOGGER.log(Level.WARNING, "Dati di pagamento non validi per l''utente {0} sul pacchetto {1}",
                    new Object[] { utente.getNickname(), pacchetto.getDestinazione() });
            throw new PagamentoRifiutatoException("I dati di pagamento non sono validi. Prenotazione annullata.");
        }

        Pagamento addebitoSuGateway = new AdapterPagamento(new PagamentoEsternoGateway(), pagamento.costo());
        if (!addebitoSuGateway.elaboraPagamento()) {
            LOGGER.log(Level.WARNING, "Autorizzazione negata dal gateway per l''utente {0} sul pacchetto {1}",
                    new Object[] { utente.getNickname(), pacchetto.getDestinazione() });
            throw new PagamentoRifiutatoException("Il pagamento non e' stato autorizzato. Prenotazione annullata.");
        }
    }

    private Prenotazione salvaPrenotazione(Utente utente, Pacchetto pacchetto, List<Utente> partecipanti,
            Pagamento pagamento, long dataPartenzaViaggio, long dataRientroViaggio) {
        Prenotazione prenotazione = new Prenotazione(prossimoId++, utente, pacchetto, pagamento, dataPartenzaViaggio,
                dataRientroViaggio, partecipanti);
        prenotazioni.add(prenotazione);
        utente.aggiungiPrenotazione(prenotazione);
        salvaSeNecessario();
        return prenotazione;
    }

    private void notificaPrenotazione(Utente utente, Prenotazione prenotazione) {
        LOGGER.log(Level.INFO, "Notifica di conferma inviata a {0}: prenotazione #{1} per {2} confermata.",
                new Object[] { utente.getEmail(), prenotazione.getId(),
                        prenotazione.getDettagliPacchetto().getDestinazione() });
    }

    public boolean annullaPrenotazione(Utente utente, int idPrenotazione) throws OperazioneNonConsentitaException {
        Prenotazione p = getPrenotazioneById(idPrenotazione);
        if (p == null || p.isAnnullata()) {
            return false;
        }
        verificaProprietario(utente, p);
        verificaFinestraModificabile(p);

        p.annulla();
        Pacchetto pacchetto = p.getDettagliPacchetto();
        int postiLiberati = p.getNumeroPartecipanti();
        pacchetto.liberaPosti(postiLiberati);
        notificaPostiLiberati(pacchetto, postiLiberati);
        salvaSeNecessario();
        return true;
    }

    private void verificaFinestraModificabile(Prenotazione p) throws OperazioneNonConsentitaException {
        if (!p.isModificabile()) {
            throw new OperazioneNonConsentitaException("Non e' piu' possibile annullare o modificare la prenotazione a meno di "
                    + Prenotazione.getGiorniPreavvisoMinimo() + " giorni dalla partenza.");
        }
    }

    private void verificaProprietario(Utente utente, Prenotazione p) throws OperazioneNonConsentitaException {
        if (utente == null || !p.getDettagliUtente().equals(utente)) {
            throw new OperazioneNonConsentitaException("La prenotazione non appartiene all'utente indicato.");
        }
    }

    public void modificaPrenotazione(Utente utente, int idPrenotazione, int idNuovoPacchetto)
            throws PacchettoNonDisponibileException, OperazioneNonConsentitaException {

        Prenotazione p = getPrenotazioneById(idPrenotazione);
        if (p == null || p.isAnnullata()) {
            throw new PacchettoNonDisponibileException("Prenotazione non trovata o gia' annullata.");
        }
        verificaProprietario(utente, p);
        verificaFinestraModificabile(p);

        Pacchetto nuovoPacchetto = risolviPacchetto(idNuovoPacchetto);
        Pacchetto vecchioPacchetto = p.getDettagliPacchetto();
        if (nuovoPacchetto.equals(vecchioPacchetto)) {
            throw new PacchettoNonDisponibileException("La prenotazione e' gia' su questo pacchetto.");
        }

        int numeroPartecipanti = p.getNumeroPartecipanti();
        verificaCambioPacchetto(p, vecchioPacchetto, nuovoPacchetto, numeroPartecipanti);

        vecchioPacchetto.liberaPosti(numeroPartecipanti);
        notificaPostiLiberati(vecchioPacchetto, numeroPartecipanti);
        nuovoPacchetto.occupaPosti(numeroPartecipanti);
        p.modificaPacchetto(nuovoPacchetto);
        salvaSeNecessario();
    }

    private void verificaCambioPacchetto(Prenotazione p, Pacchetto vecchioPacchetto, Pacchetto nuovoPacchetto,
            int numeroPartecipanti) throws PacchettoNonDisponibileException {

        String nome = PREFISSO_PACCHETTO + nuovoPacchetto.getDestinazione() + CHIUSURA_VIRGOLETTE;

        if (!nuovoPacchetto.isDisponibile(numeroPartecipanti)) {
            throw new PacchettoNonDisponibileException(nome + "non ha abbastanza posti per i "
                    + numeroPartecipanti + " partecipanti di questa prenotazione.");
        }
        if (p.getDataPartenzaViaggio() < nuovoPacchetto.getDataPartenza()
                || p.getDataRientroViaggio() > nuovoPacchetto.getDataRientro()) {
            throw new PacchettoNonDisponibileException(nome
                    + "non copre le date di viaggio gia' scelte per questa prenotazione.");
        }
        if (nuovoPacchetto.getPrezzo() > vecchioPacchetto.getPrezzo()) {
            throw new PacchettoNonDisponibileException(nome
                    + "costa piu' di quello prenotato: il cambio e' possibile solo verso un pacchetto"
                    + " di pari prezzo o piu' economico.");
        }
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
        return Collections.unmodifiableList(prenotazioni);
    }

    public List<Prenotazione> getPrenotazioniPacchetto(int idPacchetto) {
        List<Prenotazione> attive = new ArrayList<>();
        for (Prenotazione p : prenotazioni) {
            if (p.getDettagliPacchetto().getId() == idPacchetto && !p.isAnnullata()) {
                attive.add(p);
            }
        }
        return attive;
    }
}

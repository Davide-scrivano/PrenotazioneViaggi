package control;

import java.util.ArrayList;
import java.util.List;

import bean.DatiPagamentoBean;
import bean.PartecipanteBean;
import bean.PrenotazioneBean;
import dao.DAOFactory;
import dao.PacchettoDAO;
import dao.PagamentoDAO;
import dao.PartecipanteDAO;
import dao.PrenotazioneDAO;
import dao.UtenteDAO;
import dao.cache.MemoriaCentrale;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PagamentoRifiutatoException;
import exceptions.PersistenzaException;
import model.Catalogo;
import model.valori.DatiAnagrafici;
import model.valori.DurataViaggio;
import model.Pacchetto;
import model.Pagamento;
import model.Partecipante;
import model.valori.PeriodoViaggio;
import model.Prenotazione;
import model.Utente;
import notifica.NotificatorePrenotazioni;
import payment.FacadePagamento;

public class GestorePrenotazioni {

    private final DAOFactory daoFactory;
    private final GestoreCatalogo gestoreCatalogo;
    private final FacadePagamento facadePagamento;
    private final NotificatorePrenotazioni notificatore;
    private final MemoriaCentrale memoriaCentrale = MemoriaCentrale.getSingletonInstance();

    public GestorePrenotazioni(DAOFactory daoFactory, FacadePagamento facadePagamento,
            NotificatorePrenotazioni notificatore) {
        this.daoFactory = daoFactory;
        this.gestoreCatalogo = new GestoreCatalogo(daoFactory);
        this.facadePagamento = facadePagamento;
        this.notificatore = notificatore;
    }

    public Pacchetto selezionaPacchetto(int idPacchetto)
            throws PacchettoNonDisponibileException, PersistenzaException {
        Catalogo catalogo = gestoreCatalogo.consultaCatalogo();
        Pacchetto pacchetto = catalogo.trovaPacchetto(idPacchetto);
        if (pacchetto == null) {
            throw new PacchettoNonDisponibileException("Il pacchetto scelto non e' piu' disponibile a catalogo.",
                    PacchettoNonDisponibileException.Motivo.NON_A_CATALOGO);
        }
        return pacchetto;
    }

    public void verificaDisponibilita(PrenotazioneBean dati)
            throws PacchettoNonDisponibileException, PersistenzaException {

        Pacchetto pacchetto = selezionaPacchetto(dati.getIdPacchetto());
        pacchetto.verificaPrenotabilita(dati.getPartecipanti().size(), periodoRichiesto(dati));
    }

    public float calcolaPreventivo(PrenotazioneBean dati)
            throws PacchettoNonDisponibileException, PersistenzaException {

        Pacchetto pacchetto = selezionaPacchetto(dati.getIdPacchetto());
        int numeroPartecipanti = dati.getPartecipanti().size();
        pacchetto.verificaPrenotabilita(numeroPartecipanti, periodoRichiesto(dati));
        return pacchetto.calcolaPrezzoTotale(numeroPartecipanti, durataRichiesta(dati));
    }

    public Prenotazione compilaPrenotazione(PrenotazioneBean dati)
            throws PacchettoNonDisponibileException, PagamentoRifiutatoException, PersistenzaException {

        Utente cliente = risolviCliente(dati.getIdUtente());
        Pacchetto pacchetto = selezionaPacchetto(dati.getIdPacchetto());
        DurataViaggio durata = durataRichiesta(dati);
        PeriodoViaggio periodo = periodoRichiesto(dati);
        int numeroPartecipanti = dati.getPartecipanti().size();

        pacchetto.verificaPrenotabilita(numeroPartecipanti, periodo);
        float importoTotale = pacchetto.calcolaPrezzoTotale(numeroPartecipanti, durata);
        Pagamento pagamento = eseguiPagamento(dati.getDatiPagamento(), importoTotale);

        return registraPrenotazione(cliente, pacchetto, pagamento, periodo, dati.getPartecipanti());
    }

    private Pagamento eseguiPagamento(DatiPagamentoBean datiPagamento, float importoTotale)
            throws PagamentoRifiutatoException, PersistenzaException {
        PagamentoDAO pagamentoDAO = daoFactory.creaPagamentoDAO();
        return facadePagamento.incassa(pagamentoDAO.prossimoId(), datiPagamento, importoTotale);
    }

    private Prenotazione registraPrenotazione(Utente cliente, Pacchetto pacchetto, Pagamento pagamento,
            PeriodoViaggio periodo, List<PartecipanteBean> datiPartecipanti) throws PersistenzaException {

        PacchettoDAO pacchettoDAO = daoFactory.creaPacchettoDAO();
        PrenotazioneDAO prenotazioneDAO = daoFactory.creaPrenotazioneDAO();

        Prenotazione prenotazione = cliente.prenota(prenotazioneDAO.prossimoId(), pacchetto, pagamento,
                periodo, costruisciPartecipanti(datiPartecipanti));
        prenotazioneDAO.inserisci(prenotazione);

        pacchetto.occupaPosti(datiPartecipanti.size());
        pacchettoDAO.aggiorna(pacchetto);

        notificatore.confermaPrenotazione(prenotazione);
        return prenotazione;
    }

    private Utente risolviCliente(int idUtente) throws PersistenzaException {
        Utente cliente = memoriaCentrale.getUtente(idUtente);
        if (cliente == null) {
            UtenteDAO utenteDAO = daoFactory.creaUtenteDAO();
            cliente = utenteDAO.trovaPerId(idUtente);
            memoriaCentrale.memorizzaUtente(cliente);
        }
        if (cliente == null) {
            throw new PersistenzaException("L'utente della sessione non risulta piu' registrato.");
        }
        return cliente;
    }

    private DurataViaggio durataRichiesta(PrenotazioneBean dati) {
        return DurataViaggio.daSettimaneOPredefinita(dati.getSettimaneSoggiorno());
    }

    private PeriodoViaggio periodoRichiesto(PrenotazioneBean dati) {
        return PeriodoViaggio.daPartenzaEDurata(dati.getDataPartenzaViaggio(), durataRichiesta(dati));
    }

    private List<Partecipante> costruisciPartecipanti(List<PartecipanteBean> dati) throws PersistenzaException {
        PartecipanteDAO partecipanteDAO = daoFactory.creaPartecipanteDAO();
        int prossimoId = partecipanteDAO.prossimoId();

        List<Partecipante> partecipanti = new ArrayList<>();
        for (PartecipanteBean datiPartecipante : dati) {
            partecipanti.add(costruisciPartecipante(datiPartecipante, prossimoId));
            prossimoId++;
        }
        return partecipanti;
    }

    private Partecipante costruisciPartecipante(PartecipanteBean dati, int id) {
        DatiAnagrafici anagrafica = new DatiAnagrafici(dati.getDataNascitaInMillis(), dati.getCodiceFiscale());
        return new Partecipante(id, dati.getNome(), dati.getCognome(), anagrafica);
    }
}

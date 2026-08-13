package app;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;

import bean.PartecipanteBean;
import bean.PrenotazioneBean;
import bean.RecensioneBean;
import bean.RegistrazioneBean;
import cli.InterfacciaCLI;
import config.ConfigurazioneGlobale;
import config.Modalita;
import config.TipoGui;
import control.Catalogo;
import control.GestoreAssistenza;
import control.GestoreListaAttesa;
import control.GestorePrenotazioni;
import control.GestoreRecensioni;
import control.GestoreUtenti;
import dao.DAOFactory;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PagamentoRifiutatoException;
import exceptions.RecensioneNonConsentitaException;
import exceptions.RegistrazioneNonConsentitaException;
import gui.GuiJavaFXApp;
import model.DettagliOfferta;
import model.Pacchetto;
import model.TipoVolo;
import model.Utente;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String NICKNAME_AGENZIA_DEMO = "agenzia";
    private static final String NICKNAME_CONSUMER_DEMO = "mariorossi";
    private static final long MILLISECONDI_GIORNO = 24L * 60 * 60 * 1000;
    private static final int POSTI_DEFAULT_DEMO = 10;

    public static void main(String[] args) {
        ConfigurazioneGlobale config = ConfigurazioneGlobale.getInstance();

        LOGGER.log(Level.INFO, "Avvio in modalita'' {0}, persistenza {1}, gui {2}",
                new Object[] { config.getModalita(), config.getPersistenza(), config.getGui() });

        Catalogo catalogo = Catalogo.getInstance();
        GestoreUtenti gestoreUtenti = new GestoreUtenti();
        GestoreListaAttesa gestoreListaAttesa = new GestoreListaAttesa();
        GestorePrenotazioni gestorePrenotazioni = new GestorePrenotazioni(gestoreListaAttesa);
        GestoreRecensioni gestoreRecensioni = new GestoreRecensioni();
        GestoreAssistenza gestoreAssistenza = new GestoreAssistenza();

        if (config.getModalita() == Modalita.FULL) {
            DAOFactory daoFactory = new DAOFactory(config);
            gestoreUtenti.attivaPersistenza(daoFactory.creaUtenteDAO());
            catalogo.attivaPersistenza(daoFactory.creaPacchettoDAO());
            gestorePrenotazioni.attivaPersistenza(daoFactory.creaPrenotazioneDAO(gestoreUtenti));
        }

        popolaCatalogoSeVuoto(catalogo);
        popolaAgenziaSeAssente(gestoreUtenti);
        popolaRecensioniSeVuoto(catalogo, gestoreUtenti, gestorePrenotazioni, gestoreRecensioni);

        if (config.getGui() == TipoGui.JAVAFX) {
            GuiJavaFXApp.avvia(args, gestoreUtenti, gestorePrenotazioni, gestoreRecensioni,
                    gestoreAssistenza, gestoreListaAttesa);
        } else {
            new InterfacciaCLI(gestoreUtenti, gestorePrenotazioni, gestoreRecensioni, gestoreAssistenza,
                    gestoreListaAttesa).avvia();
        }
    }

    private static void popolaCatalogoSeVuoto(Catalogo catalogo) {
        if (!catalogo.pacchettiDisponibili().isEmpty()) {
            return;
        }

        long adesso = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        PacchettoDemo[] catalogoDemo = {
                new PacchettoDemo(1, "Roma", 10, 130, 350f, 4, TipoVolo.DIRETTO),
                new PacchettoDemo(2, "Parigi", 20, 170, 480f, 5, TipoVolo.DIRETTO),
                new PacchettoDemo(3, "Barcellona", 15, 150, 400f, 3, TipoVolo.CON_SCALO),
                new PacchettoDemo(4, "Londra", 30, 210, 520f, 4, TipoVolo.DIRETTO),
                new PacchettoDemo(5, "New York", 45, 240, 950f, 5, TipoVolo.CON_SCALO),
                new PacchettoDemo(6, "Pechino", 60, 260, 1100f, 4, TipoVolo.CON_SCALO),
                new PacchettoDemo(7, "Melbourne", 75, 300, 1450f, 4, TipoVolo.CON_SCALO),
                new PacchettoDemo(8, "Rio de Janeiro", 50, 270, 1200f, 5, TipoVolo.CON_SCALO) };

        for (PacchettoDemo demo : catalogoDemo) {
            aggiungiPacchettoDemo(catalogo, adesso, demo);
        }
    }

    private record PacchettoDemo(int id, String destinazione, int giorniAPartenza, int giorniAlRientro,
            float prezzo, int stelle, TipoVolo tipoVolo) {
    }

    private static void aggiungiPacchettoDemo(Catalogo catalogo, long adesso, PacchettoDemo demo) {
        catalogo.aggiungiPacchetto(new Pacchetto(demo.id(), demo.destinazione(),
                adesso + demo.giorniAPartenza() * MILLISECONDI_GIORNO,
                adesso + demo.giorniAlRientro() * MILLISECONDI_GIORNO,
                demo.prezzo(), POSTI_DEFAULT_DEMO, new DettagliOfferta(demo.stelle(), demo.tipoVolo())));
    }

    private static void popolaAgenziaSeAssente(GestoreUtenti gestoreUtenti) {
        if (gestoreUtenti.cercaPerNickname(NICKNAME_AGENZIA_DEMO) != null) {
            return;
        }

        try {
            gestoreUtenti.registraUtenteAgenzia(creaBeanRegistrazione(NICKNAME_AGENZIA_DEMO, "Agenzia", "ViaggiFacili",
                    "agenzia@prenotazioneviaggi.it", "agenzia123"));
            LOGGER.log(Level.INFO, "Account agenzia demo pronto: nickname ''{0}'', password ''agenzia123''.",
                    NICKNAME_AGENZIA_DEMO);
        } catch (RegistrazioneNonConsentitaException e) {
            LOGGER.log(Level.WARNING, "Impossibile creare l''account agenzia demo: {0}", e.getMessage());
        }
    }

    private static RegistrazioneBean creaBeanRegistrazione(String nickname, String nome, String cognome,
            String email, String password) {
        RegistrazioneBean dati = new RegistrazioneBean();
        dati.setNickname(nickname);
        dati.setNome(nome);
        dati.setCognome(cognome);
        dati.setEmail(email);
        dati.setPassword(password);
        return dati;
    }

    private static void popolaRecensioniSeVuoto(Catalogo catalogo, GestoreUtenti gestoreUtenti,
            GestorePrenotazioni gestorePrenotazioni, GestoreRecensioni gestoreRecensioni) {
        Pacchetto roma = trovaPacchettoPerDestinazione(catalogo, "Roma");
        if (roma == null || !gestoreRecensioni.getRecensioniPacchetto(roma.getId()).isEmpty()) {
            return;
        }

        Utente clienteDemo = creaClienteDemoSeAssente(gestoreUtenti);
        if (clienteDemo == null) {
            return;
        }

        prenotaERecensisci(gestorePrenotazioni, gestoreRecensioni, clienteDemo, roma, 5,
                "Viaggio fantastico, itinerario perfetto!");

        Pacchetto parigi = trovaPacchettoPerDestinazione(catalogo, "Parigi");
        if (parigi != null) {
            prenotaERecensisci(gestorePrenotazioni, gestoreRecensioni, clienteDemo, parigi, 4,
                    "Bella esperienza, qualche disagio con i trasporti locali.");
        }
    }

    private static Pacchetto trovaPacchettoPerDestinazione(Catalogo catalogo, String destinazione) {
        for (Pacchetto p : catalogo.pacchettiDisponibili()) {
            if (p.getDestinazione().equals(destinazione)) {
                return p;
            }
        }
        return null;
    }

    private static Utente creaClienteDemoSeAssente(GestoreUtenti gestoreUtenti) {
        Utente esistente = gestoreUtenti.cercaPerNickname(NICKNAME_CONSUMER_DEMO);
        if (esistente != null) {
            return esistente;
        }

        try {
            return gestoreUtenti.registraUtente(creaBeanRegistrazione(NICKNAME_CONSUMER_DEMO, "Mario", "Rossi",
                    "mario.rossi@prenotazioneviaggi.it", "cliente123"));
        } catch (RegistrazioneNonConsentitaException e) {
            LOGGER.log(Level.WARNING, "Impossibile creare il cliente demo: {0}", e.getMessage());
            return null;
        }
    }

    private static void prenotaERecensisci(GestorePrenotazioni gestorePrenotazioni,
            GestoreRecensioni gestoreRecensioni, Utente cliente, Pacchetto pacchetto, int voto, String commento) {
        try {
            gestorePrenotazioni.compilaPrenotazione(cliente, creaBeanPrenotazione(cliente, pacchetto));

            RecensioneBean datiRecensione = new RecensioneBean();
            datiRecensione.setIdPacchetto(pacchetto.getId());
            datiRecensione.setVoto(voto);
            datiRecensione.setCommento(commento);
            gestoreRecensioni.aggiungiRecensione(cliente, datiRecensione);
        } catch (PagamentoRifiutatoException | PacchettoNonDisponibileException | RecensioneNonConsentitaException e) {
            LOGGER.log(Level.WARNING, "Impossibile popolare la recensione demo per \"{0}\": {1}",
                    new Object[] { pacchetto.getDestinazione(), e.getMessage() });
        }
    }

    private static PrenotazioneBean creaBeanPrenotazione(Utente cliente, Pacchetto pacchetto) {
        PrenotazioneBean dati = new PrenotazioneBean();
        dati.setIdPacchetto(pacchetto.getId());
        dati.setDataPartenzaViaggio(pacchetto.getDataPartenza() + MILLISECONDI_GIORNO);
        dati.setSettimaneSoggiorno(1);

        PartecipanteBean partecipante = new PartecipanteBean();
        partecipante.setNome(cliente.getName());
        partecipante.setCognome(cliente.getSurname());
        dati.aggiungiPartecipante(partecipante);

        dati.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_CARTA);
        dati.setNumeroCarta("4111111111111111");
        dati.setTitolare(cliente.getName() + " " + cliente.getSurname());
        dati.setScadenza("12/30");
        dati.setCvv("123");
        return dati;
    }
}

package cli;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import bean.EsitoLogin;
import bean.EsitoOperazione;
import bean.EsitoPrenotazione;
import bean.EsitoPreventivo;
import bean.EsitoRecuperaPassword;
import bean.EsitoRegistrazione;
import bean.PacchettoBean;
import bean.PacchettoVistaBean;
import bean.PartecipanteBean;
import bean.PartecipanteVistaBean;
import bean.PrenotazioneBean;
import bean.PrenotazioneVistaBean;
import bean.RecensioneVistaBean;
import bean.UtenteVistaBean;
import controller.grafico.AssistenzaControllerGraficoCLI;
import controller.grafico.CatalogoControllerGraficoCLI;
import controller.grafico.ListaAttesaControllerGraficoCLI;
import controller.grafico.LoginControllerGraficoCLI;
import controller.grafico.PacchettoControllerGraficoCLI;
import controller.grafico.PrenotazioneControllerGraficoCLI;
import controller.grafico.PrenotazioniPacchettoControllerGraficoCLI;
import controller.grafico.RecensioneControllerGraficoCLI;
import controller.grafico.RegistrazioneControllerGraficoCLI;
import control.GestoreAssistenza;
import control.GestoreListaAttesa;
import control.GestorePrenotazioni;
import control.GestoreRecensioni;
import control.GestoreUtenti;
import util.Formattatore;

public class InterfacciaCLI {

    private static final String PROMPT_SCELTA = "Scelta: ";
    private static final String MESSAGGIO_SCELTA_NON_VALIDA = "Scelta non valida.";
    private static final String PROMPT_EMAIL = "Email: ";
    private static final String SUFFISSO_EURO = " euro";
    private static final String MESSAGGIO_ID_NON_VALIDO = "ID non valido.";
    private static final String TORNA_INDIETRO = " (0 per tornare indietro): ";

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner;

    private final LoginControllerGraficoCLI loginControllerGrafico;
    private final RegistrazioneControllerGraficoCLI registrazioneControllerGrafico;
    private final CatalogoControllerGraficoCLI catalogoControllerGrafico;
    private final PrenotazioneControllerGraficoCLI prenotazioneControllerGrafico;
    private final RecensioneControllerGraficoCLI recensioneControllerGrafico;
    private final AssistenzaControllerGraficoCLI assistenzaControllerGrafico;
    private final PacchettoControllerGraficoCLI pacchettoControllerGrafico;
    private final ListaAttesaControllerGraficoCLI listaAttesaControllerGrafico;
    private final PrenotazioniPacchettoControllerGraficoCLI prenotazioniPacchettoControllerGrafico;

    public InterfacciaCLI(GestoreUtenti gestoreUtenti, GestorePrenotazioni gestorePrenotazioni,
            GestoreRecensioni gestoreRecensioni, GestoreAssistenza gestoreAssistenza,
            GestoreListaAttesa gestoreListaAttesa) {
        this.scanner = new Scanner(System.in);
        this.loginControllerGrafico = new LoginControllerGraficoCLI(gestoreUtenti);
        this.registrazioneControllerGrafico = new RegistrazioneControllerGraficoCLI(gestoreUtenti);
        this.catalogoControllerGrafico = new CatalogoControllerGraficoCLI(gestoreRecensioni);
        this.prenotazioneControllerGrafico = new PrenotazioneControllerGraficoCLI(gestorePrenotazioni, gestoreUtenti);
        this.recensioneControllerGrafico = new RecensioneControllerGraficoCLI(gestoreRecensioni, gestoreUtenti);
        this.assistenzaControllerGrafico = new AssistenzaControllerGraficoCLI(gestoreAssistenza, gestoreUtenti);
        this.pacchettoControllerGrafico = new PacchettoControllerGraficoCLI();
        this.listaAttesaControllerGrafico = new ListaAttesaControllerGraficoCLI(gestoreListaAttesa, gestoreUtenti);
        this.prenotazioniPacchettoControllerGrafico =
                new PrenotazioniPacchettoControllerGraficoCLI(gestorePrenotazioni);
    }

    public void avvia() {
        System.out.println("=== PrenotazioneViaggi (interfaccia CLI) ===");

        boolean continua = true;
        while (continua) {
            stampaMenuIniziale();
            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1":
                    login();
                    break;
                case "2":
                    registrazione();
                    break;
                case "3":
                    consultaCatalogo(null);
                    break;
                case "4":
                    recuperaPassword();
                    break;
                case "5":
                    contattaAssistenza();
                    break;
                case "0":
                    continua = false;
                    break;
                default:
                    System.out.println(MESSAGGIO_SCELTA_NON_VALIDA);
            }
        }
        System.out.println("Uscita dall'applicazione.");
    }

    private void stampaMenuIniziale() {
        System.out.println();
        System.out.println("1) Login");
        System.out.println("2) Registrati");
        System.out.println("3) Consulta il catalogo senza registrarti");
        System.out.println("4) Password dimenticata");
        System.out.println("5) Contatta assistenza");
        System.out.println("0) Esci");
        System.out.print(PROMPT_SCELTA);
    }

    private void contattaAssistenza() {
        String nome = "";
        String email = "";
        if (!assistenzaControllerGrafico.datiContattoGiaNoti()) {
            System.out.print("Nome: ");
            nome = scanner.nextLine().trim();
            System.out.print(PROMPT_EMAIL);
            email = scanner.nextLine().trim();
        }

        System.out.print("Messaggio: ");
        String messaggio = scanner.nextLine().trim();

        EsitoOperazione esito = assistenzaControllerGrafico.gestisciRichiesta(nome, email, messaggio);
        System.out.println(esito.getMessaggio());
    }

    private void recuperaPassword() {
        System.out.print(PROMPT_EMAIL);
        String email = scanner.nextLine().trim();

        EsitoRecuperaPassword esito = loginControllerGrafico.recuperaPassword(email);
        if (esito.isSuccesso()) {
            System.out.println("La tua password e': " + esito.getPassword());
        } else {
            System.out.println(esito.getMessaggioErrore());
        }
    }

    private void login() {
        System.out.print("Nickname: ");
        String nickname = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        EsitoLogin esito = loginControllerGrafico.gestisciLogin(nickname, password);
        if (esito.isSuccesso()) {
            UtenteVistaBean utente = esito.getUtente();
            System.out.println("Login effettuato. Benvenuto " + utente.getNome() + "!");
            menuUtenteLoggato(utente);
        } else {
            System.out.println("Login fallito: " + esito.getMessaggioErrore());
        }
    }

    private void registrazione() {
        System.out.print("Nickname: ");
        String nickname = scanner.nextLine().trim();
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine().trim();
        System.out.print(PROMPT_EMAIL);
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        EsitoRegistrazione esito = registrazioneControllerGrafico.gestisciRegistrazione(nickname, nome, cognome,
                email, password);
        if (esito.isSuccesso()) {
            System.out.println("Registrazione completata, ora puoi effettuare il login.");
        } else {
            System.out.println(esito.getMessaggioErrore());
        }
    }

    private void menuUtenteLoggato(UtenteVistaBean utente) {
        if (utente.isAgenzia()) {
            menuAgenzia(utente);
        } else {
            menuConsumer(utente);
        }
    }

    private void menuConsumer(UtenteVistaBean utente) {
        boolean loggato = true;
        while (loggato) {
            System.out.println();
            System.out.println("--- Menu di " + utente.getNome() + " ---");
            System.out.println("1) Consulta catalogo e prenota");
            System.out.println("2) Le mie prenotazioni");
            System.out.println("3) Lascia una recensione");
            System.out.println("4) Contatta assistenza");
            System.out.println("5) Iscriviti a lista d'attesa");
            System.out.println("0) Logout");
            System.out.print(PROMPT_SCELTA);
            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1":
                    consultaCatalogo(utente);
                    break;
                case "2":
                    mostraMiePrenotazioni();
                    break;
                case "3":
                    lasciaRecensione();
                    break;
                case "4":
                    contattaAssistenza();
                    break;
                case "5":
                    iscrivitiListaAttesa();
                    break;
                case "0":
                    loginControllerGrafico.logout();
                    loggato = false;
                    break;
                default:
                    System.out.println(MESSAGGIO_SCELTA_NON_VALIDA);
            }
        }
    }

    private void menuAgenzia(UtenteVistaBean utente) {
        boolean loggato = true;
        while (loggato) {
            System.out.println();
            System.out.println("--- Menu agenzia: " + utente.getNome() + " ---");
            System.out.println("1) Consulta catalogo (vedi recensioni)");
            System.out.println("2) Gestisci pacchetti");
            System.out.println("0) Logout");
            System.out.print(PROMPT_SCELTA);
            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1":
                    consultaCatalogo(utente);
                    break;
                case "2":
                    gestionePacchetti();
                    break;
                case "0":
                    loginControllerGrafico.logout();
                    loggato = false;
                    break;
                default:
                    System.out.println(MESSAGGIO_SCELTA_NON_VALIDA);
            }
        }
    }

    private void gestionePacchetti() {
        List<PacchettoVistaBean> pacchetti = catalogoControllerGrafico.catalogoCompleto();

        System.out.println();
        System.out.println("--- Gestione pacchetti ---");
        for (PacchettoVistaBean p : pacchetti) {
            System.out.println("#" + p.getId() + " - " + p.getDestinazione() + " - "
                    + p.getPrezzoPerPersonaSettimana() + SUFFISSO_EURO);
        }
        System.out.println("1) Aggiungi pacchetto");
        System.out.println("2) Modifica pacchetto");
        System.out.println("3) Rimuovi pacchetto");
        System.out.println("4) Vedi le prenotazioni di un pacchetto");
        System.out.println("0) Torna indietro");
        System.out.print(PROMPT_SCELTA);
        String scelta = scanner.nextLine().trim();

        switch (scelta) {
            case "1":
                aggiungiPacchetto();
                break;
            case "2":
                modificaPacchetto(pacchetti);
                break;
            case "3":
                rimuoviPacchetto(pacchetti);
                break;
            case "4":
                prenotazioniPacchetto(pacchetti);
                break;
            default:
                break;
        }
    }

    private void aggiungiPacchetto() {
        PacchettoBean dati = leggiDatiPacchetto();
        if (dati == null) {
            return;
        }
        EsitoOperazione esito = pacchettoControllerGrafico.aggiungiPacchetto(dati);
        System.out.println(esito.getMessaggio());
    }

    private void modificaPacchetto(List<PacchettoVistaBean> pacchetti) {
        System.out.print("ID del pacchetto da modificare: ");
        PacchettoVistaBean pacchetto = trovaPacchettoPerId(pacchetti, scanner.nextLine().trim());
        if (pacchetto == null) {
            System.out.println(MESSAGGIO_ID_NON_VALIDO);
            return;
        }

        int prenotazioniAttive = prenotazioniPacchettoControllerGrafico
                .prenotazioniDelPacchetto(pacchetto.getId()).size();
        System.out.println("Attenzione: il pacchetto ha " + prenotazioniAttive + " prenotazioni attive.");
        System.out.println("Inserisci i nuovi dati (i valori attuali verranno sostituiti):");

        PacchettoBean dati = leggiDatiPacchetto();
        if (dati == null) {
            return;
        }
        EsitoOperazione esito = pacchettoControllerGrafico.modificaPacchetto(pacchetto.getId(), dati);
        System.out.println(esito.getMessaggio());
    }

    private void prenotazioniPacchetto(List<PacchettoVistaBean> pacchetti) {
        System.out.print("ID del pacchetto: ");
        PacchettoVistaBean pacchetto = trovaPacchettoPerId(pacchetti, scanner.nextLine().trim());
        if (pacchetto == null) {
            System.out.println(MESSAGGIO_ID_NON_VALIDO);
            return;
        }

        List<PrenotazioneVistaBean> prenotazioni =
                prenotazioniPacchettoControllerGrafico.prenotazioniDelPacchetto(pacchetto.getId());
        System.out.println();
        System.out.println("--- Prenotazioni del pacchetto #" + pacchetto.getId()
                + " - " + pacchetto.getDestinazione() + " ---");
        if (prenotazioni.isEmpty()) {
            System.out.println("Nessuna prenotazione attiva per questo pacchetto.");
            return;
        }

        for (PrenotazioneVistaBean prenotazione : prenotazioni) {
            System.out.println("#" + prenotazione.getId() + " - " + prenotazione.getNomeCliente() + " "
                    + prenotazione.getCognomeCliente() + " (" + prenotazione.getEmailCliente() + ") - "
                    + prenotazione.getNumeroPartecipanti() + " partecipanti - dal "
                    + formattaData(prenotazione.getDataPartenzaViaggio()) + " al "
                    + formattaData(prenotazione.getDataRientroViaggio()) + " - "
                    + prenotazione.getStato());
        }
        System.out.println("Totale: " + prenotazioni.size() + " prenotazioni, "
                + prenotazioniPacchettoControllerGrafico.postiVenduti(pacchetto.getId())
                + " posti venduti. Posti ancora disponibili: " + pacchetto.getPostiDisponibili() + ".");
    }

    private PacchettoBean leggiDatiPacchetto() {
        System.out.print("Destinazione: ");
        String destinazione = scanner.nextLine().trim();

        Long dataPartenza = leggiDataObbligatoria("Data di partenza (gg/mm/aaaa): ");
        if (dataPartenza == null) {
            return null;
        }
        Long dataRientro = leggiDataObbligatoria("Data di rientro (gg/mm/aaaa): ");
        if (dataRientro == null) {
            return null;
        }

        System.out.print("Prezzo a persona per una settimana: ");
        String testoPrezzo = scanner.nextLine().trim();
        System.out.print("Posti disponibili: ");
        String testoPosti = scanner.nextLine().trim();
        System.out.print("Stelle hotel (1-5): ");
        String testoStelle = scanner.nextLine().trim();

        try {
            PacchettoBean dati = new PacchettoBean();
            dati.setDestinazione(destinazione);
            dati.setDataPartenza(dataPartenza);
            dati.setDataRientro(dataRientro);
            dati.setPrezzo(Float.parseFloat(testoPrezzo));
            dati.setPosti(Integer.parseInt(testoPosti));
            dati.setStelleHotel(Integer.parseInt(testoStelle));
            dati.setTipoVolo(chiediTipoVolo());
            return dati;
        } catch (NumberFormatException e) {
            System.out.println("Prezzo, posti o stelle non validi: operazione annullata.");
            return null;
        }
    }

    private String chiediTipoVolo() {
        System.out.println("Tipo di volo:");
        System.out.println("1) Diretto");
        System.out.println("2) Con scalo");
        System.out.print(PROMPT_SCELTA);
        String scelta = scanner.nextLine().trim();
        return "2".equals(scelta) ? PacchettoBean.VOLO_CON_SCALO : PacchettoBean.VOLO_DIRETTO;
    }

    private Long leggiDataObbligatoria(String messaggio) {
        return leggiDataObbligatoria(messaggio, "Data non valida: operazione annullata.");
    }

    private Long leggiDataObbligatoria(String messaggio, String messaggioErrore) {
        System.out.print(messaggio);
        try {
            return millisDaTesto(scanner.nextLine());
        } catch (DateTimeParseException e) {
            System.out.println(messaggioErrore);
            return null;
        }
    }

    private void rimuoviPacchetto(List<PacchettoVistaBean> pacchetti) {
        System.out.print("ID del pacchetto da rimuovere: ");
        PacchettoVistaBean pacchetto = trovaPacchettoPerId(pacchetti, scanner.nextLine().trim());
        if (pacchetto == null) {
            System.out.println(MESSAGGIO_ID_NON_VALIDO);
            return;
        }

        EsitoOperazione esito = pacchettoControllerGrafico.rimuoviPacchetto(pacchetto.getId());
        System.out.println(esito.getMessaggio());
    }

    private void consultaCatalogo(UtenteVistaBean utenteOpzionale) {
        System.out.print("Cerca per destinazione (invio per vedere tutti): ");
        String testoRicerca = scanner.nextLine().trim();
        List<PacchettoVistaBean> pacchetti = catalogoControllerGrafico.cercaPerDestinazione(testoRicerca);

        System.out.println();
        System.out.println("--- Catalogo pacchetti disponibili ---");
        if (pacchetti.isEmpty()) {
            System.out.println("Nessun risultato disponibile.");
        } else {
            for (PacchettoVistaBean p : pacchetti) {
                System.out.println("#" + p.getId() + " - " + p.getDestinazione()
                        + " - " + p.getPrezzoPerPersonaSettimana() + SUFFISSO_EURO
                        + " - partenza " + formattaData(p.getDataPartenza())
                        + " - posti: " + p.getPostiDisponibili()
                        + " - hotel " + p.getStelleHotel() + " stelle"
                        + " - voto medio: " + descriviVotoMedio(p));
            }
        }

        System.out.print("Inserisci l'ID del pacchetto per vedere i dettagli" + TORNA_INDIETRO);
        String scelta = scanner.nextLine().trim();
        if ("0".equals(scelta)) {
            return;
        }

        PacchettoVistaBean scelto = trovaPacchettoPerId(pacchetti, scelta);
        if (scelto == null) {
            System.out.println(MESSAGGIO_ID_NON_VALIDO);
            return;
        }

        mostraDettaglioPacchetto(utenteOpzionale, scelto);
    }

    private void mostraDettaglioPacchetto(UtenteVistaBean utenteOpzionale, PacchettoVistaBean pacchetto) {
        System.out.println();
        System.out.println("--- " + pacchetto.getDestinazione() + " ---");
        System.out.println("Prenotabile dal " + formattaData(pacchetto.getDataPartenza())
                + " al " + formattaData(pacchetto.getDataRientro()));
        System.out.println("Durata del soggiorno: 1 o 2 settimane, a scelta");
        System.out.println("Prezzo: " + pacchetto.getPrezzoPerPersonaSettimana()
                + " euro a persona per una settimana");
        System.out.println("Posti disponibili: " + pacchetto.getPostiDisponibili());
        System.out.println("Hotel: " + pacchetto.getStelleHotel() + " stelle");
        System.out.println("Volo: " + pacchetto.getDescrizioneVolo());

        List<RecensioneVistaBean> recensioni = catalogoControllerGrafico.recensioniDelPacchetto(pacchetto.getId());
        System.out.println("Recensioni:");
        if (recensioni.isEmpty()) {
            System.out.println("  Ancora nessuna recensione.");
        } else {
            for (RecensioneVistaBean recensione : recensioni) {
                System.out.println("  " + recensione.getNomeAutore() + " - " + recensione.getVoto() + "/5: "
                        + recensione.getCommento());
            }
        }

        if (utenteOpzionale == null) {
            System.out.println("Effettua il login per poter prenotare.");
            return;
        }
        if (utenteOpzionale.isAgenzia()) {
            return;
        }

        System.out.print("Vuoi prenotare questo pacchetto? (s/n): ");
        if ("s".equalsIgnoreCase(scanner.nextLine().trim())) {
            effettuaPagamentoEPrenotazione(pacchetto);
        }
    }

    private String descriviVotoMedio(PacchettoVistaBean pacchetto) {
        if (pacchetto.getNumeroRecensioni() == 0) {
            return "nessuna recensione";
        }
        return String.format("%.1f/5 (%d recensioni)", pacchetto.getVotoMedio(), pacchetto.getNumeroRecensioni());
    }

    private void lasciaRecensione() {
        List<PacchettoVistaBean> pacchetti = catalogoControllerGrafico.catalogoCompleto();

        System.out.println();
        System.out.println("--- Lascia una recensione ---");
        for (PacchettoVistaBean p : pacchetti) {
            System.out.println("#" + p.getId() + " - " + p.getDestinazione());
        }
        System.out.print("ID del pacchetto da recensire" + TORNA_INDIETRO);
        String scelta = scanner.nextLine().trim();
        if ("0".equals(scelta)) {
            return;
        }

        PacchettoVistaBean pacchetto = trovaPacchettoPerId(pacchetti, scelta);
        if (pacchetto == null) {
            System.out.println(MESSAGGIO_ID_NON_VALIDO);
            return;
        }

        System.out.print("Voto (1-5): ");
        Integer voto = leggiIntero("Voto non valido.");
        if (voto == null) {
            return;
        }

        System.out.print("Commento: ");
        String commento = scanner.nextLine().trim();

        EsitoOperazione esito = recensioneControllerGrafico.gestisciRecensione(pacchetto.getId(), voto, commento);
        System.out.println(esito.getMessaggio());
    }

    private void iscrivitiListaAttesa() {
        List<PacchettoVistaBean> pacchetti = catalogoControllerGrafico.catalogoCompleto();

        System.out.println();
        System.out.println("--- Iscriviti a lista d'attesa ---");
        for (PacchettoVistaBean p : pacchetti) {
            System.out.println("#" + p.getId() + " - " + p.getDestinazione() + " (posti disponibili: "
                    + p.getPostiDisponibili() + ")");
        }
        System.out.print("ID del pacchetto" + TORNA_INDIETRO);
        String scelta = scanner.nextLine().trim();
        if ("0".equals(scelta)) {
            return;
        }

        PacchettoVistaBean pacchetto = trovaPacchettoPerId(pacchetti, scelta);
        if (pacchetto == null) {
            System.out.println(MESSAGGIO_ID_NON_VALIDO);
            return;
        }

        System.out.print("Per quante persone ti servono i posti? ");
        Integer numeroPosti = leggiIntero("Numero non valido.");
        if (numeroPosti == null) {
            return;
        }

        EsitoOperazione esito = listaAttesaControllerGrafico.iscriviListaAttesa(pacchetto.getId(), numeroPosti);
        System.out.println(esito.getMessaggio());
    }

    private PacchettoVistaBean trovaPacchettoPerId(List<PacchettoVistaBean> pacchetti, String idTestuale) {
        try {
            int id = Integer.parseInt(idTestuale);
            for (PacchettoVistaBean p : pacchetti) {
                if (p.getId() == id) {
                    return p;
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    private void effettuaPagamentoEPrenotazione(PacchettoVistaBean pacchetto) {
        System.out.println("Il pacchetto \"" + pacchetto.getDestinazione() + "\" e' prenotabile dal "
                + formattaData(pacchetto.getDataPartenza()) + " al "
                + formattaData(pacchetto.getDataRientro()) + ".");
        Long dataPartenzaViaggio = leggiDataObbligatoria("Data di partenza desiderata (gg/mm/aaaa): ",
                "Data non valida: prenotazione annullata.");
        if (dataPartenzaViaggio == null) {
            return;
        }

        Integer settimane = chiediSettimane();
        if (settimane == null) {
            return;
        }

        List<PartecipanteBean> partecipanti = chiediPartecipanti();
        if (partecipanti.isEmpty()) {
            return;
        }

        PrenotazioneBean datiPrenotazione = new PrenotazioneBean();
        datiPrenotazione.setIdPacchetto(pacchetto.getId());
        datiPrenotazione.setDataPartenzaViaggio(dataPartenzaViaggio);
        datiPrenotazione.setSettimaneSoggiorno(settimane);
        datiPrenotazione.setPartecipanti(partecipanti);
        chiediMetodoPagamento(datiPrenotazione);

        if (!mostraPreventivoEConferma(datiPrenotazione)) {
            return;
        }

        EsitoPrenotazione esito = prenotazioneControllerGrafico.creaPrenotazione(datiPrenotazione);
        if (esito.isSuccesso()) {
            System.out.println("Prenotazione confermata! Riceverai una notifica di conferma.");
        } else {
            System.out.println("Prenotazione non effettuata: " + esito.getMessaggioErrore());
        }
    }

    private boolean mostraPreventivoEConferma(PrenotazioneBean datiPrenotazione) {
        EsitoPreventivo preventivo = prenotazioneControllerGrafico.calcolaPreventivo(datiPrenotazione);
        if (!preventivo.isSuccesso()) {
            System.out.println("Prenotazione non effettuata: " + preventivo.getMessaggioErrore());
            return false;
        }

        System.out.println("Importo totale da pagare: " + preventivo.getImportoTotale() + SUFFISSO_EURO);
        System.out.print("Confermi il pagamento? (s/n): ");
        return "s".equalsIgnoreCase(scanner.nextLine().trim());
    }

    private Integer chiediSettimane() {
        System.out.println("Durata del soggiorno:");
        System.out.println("1) 1 settimana");
        System.out.println("2) 2 settimane");
        System.out.print(PROMPT_SCELTA);
        String scelta = scanner.nextLine().trim();

        if ("1".equals(scelta) || "2".equals(scelta)) {
            return Integer.valueOf(scelta);
        }
        System.out.println("Scelta non valida: prenotazione annullata.");
        return null;
    }

    private void chiediMetodoPagamento(PrenotazioneBean datiPrenotazione) {
        System.out.println("Metodo di pagamento:");
        System.out.println("1) Carta di credito");
        System.out.println("2) PayPal");
        System.out.print(PROMPT_SCELTA);
        String scelta = scanner.nextLine().trim();

        if ("2".equals(scelta)) {
            datiPrenotazione.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_PAYPAL);
            System.out.print("Email PayPal: ");
            datiPrenotazione.setEmailPaypal(scanner.nextLine().trim());
            System.out.print("Password PayPal: ");
            datiPrenotazione.setPasswordPaypal(scanner.nextLine().trim());
            return;
        }

        datiPrenotazione.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_CARTA);
        System.out.print("Numero carta: ");
        datiPrenotazione.setNumeroCarta(scanner.nextLine().trim());
        System.out.print("Titolare: ");
        datiPrenotazione.setTitolare(scanner.nextLine().trim());
        System.out.print("Scadenza (MM/AA): ");
        datiPrenotazione.setScadenza(scanner.nextLine().trim());
        System.out.print("CVV: ");
        datiPrenotazione.setCvv(scanner.nextLine().trim());
    }

    private List<PartecipanteBean> chiediPartecipanti() {
        System.out.print("Numero partecipanti: ");
        Integer numero = leggiIntero("Numero non valido.");
        if (numero == null || numero < 1) {
            System.out.println("Numero di partecipanti non valido.");
            return List.of();
        }

        List<PartecipanteBean> partecipanti = new ArrayList<>();
        for (int i = 1; i <= numero; i++) {
            System.out.println("Dati partecipante " + i + ":");
            PartecipanteBean partecipante = new PartecipanteBean();
            System.out.print("  Nome: ");
            partecipante.setNome(scanner.nextLine().trim());
            System.out.print("  Cognome: ");
            partecipante.setCognome(scanner.nextLine().trim());
            System.out.print("  Data di nascita (gg/mm/aaaa): ");
            partecipante.setDataNascita(scanner.nextLine().trim());
            System.out.print("  Codice fiscale: ");
            partecipante.setCodiceFiscale(scanner.nextLine().trim());
            partecipanti.add(partecipante);
        }

        return partecipanti;
    }

    private Integer leggiIntero(String messaggioErrore) {
        try {
            return Integer.valueOf(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(messaggioErrore);
            return null;
        }
    }

    private String formattaData(long millis) {
        return Formattatore.formattaData(millis, formatoData);
    }

    private long millisDaTesto(String testo) {
        return LocalDate.parse(testo.trim(), formatoData).atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }

    private void mostraMiePrenotazioni() {
        List<PrenotazioneVistaBean> prenotazioni = prenotazioneControllerGrafico.miePrenotazioni();

        System.out.println();
        System.out.println("--- Le mie prenotazioni ---");
        if (prenotazioni.isEmpty()) {
            System.out.println("Nessuna prenotazione effettuata.");
            return;
        }

        for (PrenotazioneVistaBean p : prenotazioni) {
            System.out.println("#" + p.getId() + " - " + p.getDestinazione() + " - " + p.getStato());
        }

        System.out.print("Inserisci l'ID della prenotazione per vedere i dettagli" + TORNA_INDIETRO);
        String scelta = scanner.nextLine().trim();
        if ("0".equals(scelta)) {
            return;
        }

        PrenotazioneVistaBean trovata = trovaPrenotazionePerId(prenotazioni, scelta);
        if (trovata == null) {
            System.out.println(MESSAGGIO_ID_NON_VALIDO);
            return;
        }

        gestisciPrenotazione(trovata);
    }

    private PrenotazioneVistaBean trovaPrenotazionePerId(List<PrenotazioneVistaBean> prenotazioni, String idTestuale) {
        try {
            int id = Integer.parseInt(idTestuale);
            for (PrenotazioneVistaBean p : prenotazioni) {
                if (p.getId() == id) {
                    return p;
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    private void gestisciPrenotazione(PrenotazioneVistaBean prenotazione) {
        System.out.println();
        System.out.println("--- Dettaglio prenotazione #" + prenotazione.getId() + " ---");
        System.out.println("Destinazione: " + prenotazione.getDestinazione());
        System.out.println("Partenza: " + formattaData(prenotazione.getDataPartenzaViaggio()));
        System.out.println("Rientro: " + formattaData(prenotazione.getDataRientroViaggio()));
        System.out.println("Prenotata il: " + formattaData(prenotazione.getDataPrenotazione()));
        System.out.println("Stato: " + prenotazione.getStato());
        System.out.println("Pagamento: " + Formattatore.descriviPagamento(prenotazione));
        System.out.println("Partecipanti:");
        for (PartecipanteVistaBean partecipante : prenotazione.getPartecipanti()) {
            System.out.println("  - " + Formattatore.descriviPartecipante(partecipante, formatoData));
        }

        if (!prenotazione.isModificabile()) {
            return;
        }

        System.out.println();
        System.out.println("1) Annulla prenotazione");
        System.out.println("2) Cambia pacchetto");
        System.out.println("0) Torna indietro");
        System.out.print(PROMPT_SCELTA);
        String scelta = scanner.nextLine().trim();

        if ("1".equals(scelta)) {
            EsitoOperazione esito = prenotazioneControllerGrafico.annullaPrenotazione(prenotazione.getId());
            System.out.println(esito.getMessaggio());
        } else if ("2".equals(scelta)) {
            cambiaPacchetto(prenotazione);
        }
    }

    private void cambiaPacchetto(PrenotazioneVistaBean prenotazione) {
        List<PacchettoVistaBean> pacchetti = catalogoControllerGrafico.catalogoCompleto();

        System.out.println();
        System.out.println("--- Scegli il nuovo pacchetto ---");
        for (PacchettoVistaBean p : pacchetti) {
            System.out.println("#" + p.getId() + " - " + p.getDestinazione() + " - "
                    + p.getPrezzoPerPersonaSettimana() + " euro a persona a settimana");
        }
        System.out.print("ID del nuovo pacchetto (0 per annullare): ");
        String scelta = scanner.nextLine().trim();
        if ("0".equals(scelta)) {
            return;
        }

        PacchettoVistaBean nuovoPacchetto = trovaPacchettoPerId(pacchetti, scelta);
        if (nuovoPacchetto == null) {
            System.out.println(MESSAGGIO_ID_NON_VALIDO);
            return;
        }

        EsitoOperazione esito = prenotazioneControllerGrafico.modificaPacchetto(prenotazione.getId(),
                nuovoPacchetto.getId());
        System.out.println(esito.getMessaggio());
    }
}

package cli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import bean.DatiPagamentoBean;
import bean.EsitoCatalogoBean;
import bean.EsitoLoginBean;
import bean.EsitoOperazioneBean;
import bean.EsitoPacchettoBean;
import bean.EsitoPrenotazioneBean;
import bean.EsitoPreventivoBean;
import bean.LoginBean;
import bean.PacchettoVistaBean;
import bean.PartecipanteBean;
import bean.PartecipanteVistaBean;
import bean.PrenotazioneBean;
import bean.PrenotazioneVistaBean;
import bean.UtenteVistaBean;
import controller.grafico.LoginControllerGrafico;
import controller.grafico.PrenotazioneControllerGrafico;
import util.Formattatore;

public class InterfacciaCLI {

    private static final String TESTO_NON_IMPLEMENTATO =
            "Funzionalita' non implementata: questa release realizza il solo caso d'uso \"Compila prenotazione\".";
    private static final PrintStream USCITA = System.out;
    private static final String RICHIESTA_SCELTA = "Scelta: ";
    private static final String SEPARATORE = "------------------------------------------------------------";
    private static final String TESTO_LOGIN_PER_PRENOTARE =
            "Il catalogo e' consultabile liberamente, ma per prenotare devi effettuare il login.";

    private final LoginControllerGrafico loginControllerGrafico;
    private final PrenotazioneControllerGrafico prenotazioneControllerGrafico;
    private final Scanner ingresso = new Scanner(System.in);

    public InterfacciaCLI(LoginControllerGrafico loginControllerGrafico,
            PrenotazioneControllerGrafico prenotazioneControllerGrafico) {
        this.loginControllerGrafico = loginControllerGrafico;
        this.prenotazioneControllerGrafico = prenotazioneControllerGrafico;
    }

    public void avvia() {
        USCITA.println("=== PrenotazioneViaggi ===");
        UtenteVistaBean utente = eseguiLogin();
        if (utente != null) {
            menuPrincipale(utente);
        }
        USCITA.println("Arrivederci.");
    }

    private UtenteVistaBean eseguiLogin() {
        while (true) {
            USCITA.println("\n1) Login   2) Consulta il catalogo (ospite)   3) Registrati"
                    + "   4) Recupera password   5) Visualizza recensioni   6) Contatta assistenza   0) Esci");
            String scelta = leggiTesto(RICHIESTA_SCELTA);
            if ("0".equals(scelta)) {
                return null;
            }
            if ("1".equals(scelta)) {
                UtenteVistaBean utente = tentaLogin();
                if (utente != null) {
                    return utente;
                }
            } else if ("2".equals(scelta)) {
                mostraCatalogo(null);
            } else {
                USCITA.println(TESTO_NON_IMPLEMENTATO);
            }
        }
    }

    private UtenteVistaBean tentaLogin() {
        LoginBean credenziali = new LoginBean();
        credenziali.setNickname(leggiTesto("Nickname: "));
        credenziali.setPassword(leggiTesto("Password: "));
        EsitoLoginBean esito = loginControllerGrafico.effettuaLogin(credenziali);
        if (esito.isSuccesso()) {
            return esito.getUtente();
        }
        USCITA.println(esito.getMessaggio());
        return null;
    }

    private void menuPrincipale(UtenteVistaBean utente) {
        boolean continua = true;
        while (continua) {
            if (utente.isAgenzia()) {
                stampaMenuAgenzia(utente);
            } else {
                stampaMenuCliente(utente);
            }

            String scelta = leggiTesto(RICHIESTA_SCELTA);
            if ("0".equals(scelta)) {
                continua = false;
            } else if ("1".equals(scelta)) {
                mostraCatalogo(utente);
            } else {
                USCITA.println(TESTO_NON_IMPLEMENTATO);
            }
        }
    }

    private void stampaMenuCliente(UtenteVistaBean utente) {
        USCITA.println("\nBenvenuto, " + utente.getNome() + "!");
        USCITA.println("1) Consulta il catalogo e prenota");
        USCITA.println("2) Modifica una prenotazione");
        USCITA.println("3) Annulla una prenotazione");
        USCITA.println("4) Visualizza recensioni");
        USCITA.println("5) Contatta assistenza");
        USCITA.println("0) Logout");
    }

    private void stampaMenuAgenzia(UtenteVistaBean utente) {
        USCITA.println("\nBenvenuta, " + utente.getNome() + "! (agenzia)");
        USCITA.println("1) Consulta il catalogo");
        USCITA.println("2) Gestisci pacchetti");
        USCITA.println("3) Visualizza prenotazioni di un pacchetto");
        USCITA.println("4) Visualizza richieste di assistenza");
        USCITA.println("5) Visualizza recensioni");
        USCITA.println("0) Logout");
    }

    private void mostraCatalogo(UtenteVistaBean utenteOspite) {
        String destinazione = leggiTesto("Filtra per destinazione (invio per vedere tutto): ");
        EsitoCatalogoBean esitoCatalogo = prenotazioneControllerGrafico.cercaNelCatalogo(destinazione);
        if (!esitoCatalogo.isSuccesso()) {
            USCITA.println(esitoCatalogo.getMessaggio());
            return;
        }

        USCITA.println("\n" + SEPARATORE);
        USCITA.println(esitoCatalogo.getCatalogo().getTitolo());
        for (PacchettoVistaBean pacchetto : esitoCatalogo.getCatalogo().getPacchetti()) {
            USCITA.println("[" + pacchetto.getId() + "] " + pacchetto.getDestinazione() + " - "
                    + pacchetto.getPrezzoSettimanale() + " euro a persona a settimana - hotel "
                    + pacchetto.getStelleHotel() + " stelle - " + pacchetto.getPostiDisponibili() + " posti"
                    + (pacchetto.isEsaurito() ? " - ESAURITO" : ""));
        }
        USCITA.println(SEPARATORE);

        int idScelto = leggiIntero("Numero del pacchetto (0 per tornare indietro): ");
        if (idScelto > 0) {
            mostraDettaglioPacchetto(utenteOspite, idScelto);
        }
    }

    private void mostraDettaglioPacchetto(UtenteVistaBean utenteOspite, int idPacchetto) {
        EsitoPacchettoBean esito = prenotazioneControllerGrafico.dettaglioPacchetto(idPacchetto);
        if (!esito.isSuccesso()) {
            USCITA.println(esito.getMessaggio());
            return;
        }
        PacchettoVistaBean pacchetto = esito.getPacchetto();

        USCITA.println("\n" + pacchetto.getDestinazione());
        USCITA.println("Prenotabile dal " + Formattatore.testoDaMillis(pacchetto.getDataPartenza())
                + " al " + Formattatore.testoDaMillis(pacchetto.getDataRientro()));
        USCITA.println("Prezzo: " + pacchetto.getPrezzoSettimanale() + " euro a persona per una settimana");
        USCITA.println("Posti disponibili: " + pacchetto.getPostiDisponibili());
        USCITA.println("Hotel: " + pacchetto.getStelleHotel() + " stelle - volo " + pacchetto.getDescrizioneVolo());

        if (utenteOspite != null && utenteOspite.isAgenzia()) {
            return;
        }
        if (!"s".equalsIgnoreCase(leggiTesto("Vuoi prenotare questo pacchetto? (s/n): "))) {
            return;
        }
        if (utenteOspite == null) {
            USCITA.println(TESTO_LOGIN_PER_PRENOTARE);
            return;
        }
        compilaPrenotazione(utenteOspite, pacchetto);
    }

    private void compilaPrenotazione(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        PrenotazioneBean dati = leggiDatiViaggio(utente, pacchetto);

        EsitoOperazioneBean esitoDati = prenotazioneControllerGrafico.verificaDatiViaggio(dati);
        if (!esitoDati.isSuccesso()) {
            USCITA.println(esitoDati.getMessaggio());
            if (esitoDati.isPostiInsufficienti()) {
                USCITA.println("Puoi iscriverti alla lista d'attesa. " + TESTO_NON_IMPLEMENTATO);
            }
            return;
        }

        EsitoPreventivoBean esitoPreventivo = prenotazioneControllerGrafico.calcolaPreventivo(dati);
        if (!esitoPreventivo.isSuccesso()) {
            USCITA.println(esitoPreventivo.getMessaggio());
            return;
        }
        USCITA.println("Prezzo totale: " + esitoPreventivo.getImportoTotale() + " euro");

        if (!"s".equalsIgnoreCase(leggiTesto("Confermi e procedi al pagamento? (s/n): "))) {
            return;
        }

        dati.setDatiPagamento(leggiDatiPagamento());

        EsitoPrenotazioneBean esito = prenotazioneControllerGrafico.compilaPrenotazione(dati);
        if (esito.isSuccesso()) {
            mostraConferma(utente, esito.getPrenotazione());
        } else {
            USCITA.println("Prenotazione non riuscita: " + esito.getMessaggio());
        }
    }

    private PrenotazioneBean leggiDatiViaggio(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        PrenotazioneBean dati = new PrenotazioneBean();
        dati.setIdUtente(utente.getId());
        dati.setIdPacchetto(pacchetto.getId());
        dati.setDataPartenzaViaggio(Formattatore.millisDaTesto(
                leggiTesto("Data di partenza (" + Formattatore.FORMATO_LEGGIBILE + "): ")));
        dati.setSettimaneSoggiorno(leggiIntero("Durata del soggiorno in settimane (1 o 2): "));
        dati.setPartecipanti(leggiPartecipanti(leggiIntero("Numero di partecipanti: ")));
        return dati;
    }

    private List<PartecipanteBean> leggiPartecipanti(int numero) {
        List<PartecipanteBean> partecipanti = new ArrayList<>();
        for (int posizione = 1; posizione <= numero; posizione++) {
            USCITA.println("Partecipante " + posizione + ":");
            PartecipanteBean partecipante = new PartecipanteBean();
            partecipante.setNome(leggiTesto("  Nome: "));
            partecipante.setCognome(leggiTesto("  Cognome: "));
            partecipante.setDataNascita(
                    leggiTesto("  Data di nascita (" + Formattatore.FORMATO_LEGGIBILE + ", facoltativa): "));
            partecipante.setCodiceFiscale(leggiTesto("  Codice fiscale (facoltativo): "));
            partecipanti.add(partecipante);
        }
        return partecipanti;
    }

    private DatiPagamentoBean leggiDatiPagamento() {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        USCITA.println("Metodo di pagamento: 1) Carta di credito   2) PayPal");

        if ("2".equals(leggiTesto(RICHIESTA_SCELTA))) {
            dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_PAYPAL);
            dati.setEmailPaypal(leggiTesto("Email PayPal: "));
            dati.setPasswordPaypal(leggiTesto("Password PayPal: "));
            return dati;
        }

        dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_CARTA);
        dati.setNumeroCarta(leggiTesto("Numero carta: "));
        dati.setTitolare(leggiTesto("Titolare: "));
        dati.setScadenza(leggiTesto("Scadenza (MM/AA): "));
        dati.setCvv(leggiTesto("CVV: "));
        return dati;
    }

    private void mostraConferma(UtenteVistaBean utente, PrenotazioneVistaBean prenotazione) {
        USCITA.println("\n" + SEPARATORE);
        USCITA.println("Prenotazione confermata, numero " + prenotazione.getId());
        USCITA.println("Destinazione: " + prenotazione.getDestinazione());
        USCITA.println("Partenza: " + Formattatore.testoDaMillis(prenotazione.getDataPartenzaViaggio())
                + " - rientro: " + Formattatore.testoDaMillis(prenotazione.getDataRientroViaggio()));
        USCITA.println("Pagamento: " + prenotazione.getDescrizionePagamento());
        USCITA.println("Partecipanti:");
        for (PartecipanteVistaBean partecipante : prenotazione.getPartecipanti()) {
            USCITA.println("  - " + Formattatore.descriviPartecipante(partecipante));
        }
        USCITA.println("Riceverai una notifica di conferma all'indirizzo " + utente.getEmail() + ".");
        USCITA.println(SEPARATORE);
    }

    private String leggiTesto(String richiesta) {
        USCITA.print(richiesta);
        return ingresso.hasNextLine() ? ingresso.nextLine().trim() : "";
    }

    private int leggiIntero(String richiesta) {
        String testo = leggiTesto(richiesta);
        try {
            return Integer.parseInt(testo);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

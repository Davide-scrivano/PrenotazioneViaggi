package gui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

public class GuiJavaFXApp extends Application {

    private static final String FOGLIO_STILE = "/gui/stile.css";
    private static final String STILE_TITOLO = "titolo";
    private static final String STILE_SOTTOTITOLO = "sottotitolo";
    private static final String STILE_SECONDARIO = "button-secondario";
    private static final String STILE_PANNELLO = "pannello";
    private static final String STILE_SFONDO = "sfondo";
    private static final String STILE_RIGA = "riga-elemento";
    private static final String COLORE_SUCCESSO = "-fx-text-fill: #2e7d32;";
    private static final String COLORE_ERRORE = "-fx-text-fill: #c62828;";
    private static final String TESTO_NON_IMPLEMENTATO =
            "Funzionalita' non implementata: questa release realizza il solo caso d'uso \"Compila prenotazione\".";
    private static final String TESTO_TORNA_AL_MENU = "Torna al menu";
    private static final String TESTO_TORNA_AL_CATALOGO = "Torna al catalogo";
    private static final String TESTO_LOGIN_PER_PRENOTARE =
            "Il catalogo e' consultabile liberamente, ma per prenotare devi effettuare il login.";
    private static final String TESTO_LISTA_ATTESA = "Iscriviti alla lista d'attesa";
    private static final int MASSIMO_PARTECIPANTI = 20;
    private static final double LARGHEZZA_CONTENUTO = 820;

    private static LoginControllerGrafico loginControllerGrafico;
    private static PrenotazioneControllerGrafico prenotazioneControllerGrafico;

    private Scene scenaPrincipale;

    public static void avvia(String[] argomenti, LoginControllerGrafico controllerLogin,
            PrenotazioneControllerGrafico controllerPrenotazione) {
        loginControllerGrafico = controllerLogin;
        prenotazioneControllerGrafico = controllerPrenotazione;
        launch(argomenti);
    }

    @Override
    public void start(Stage palcoscenico) {
        palcoscenico.setTitle("PrenotazioneViaggi");
        scenaPrincipale = new Scene(new VBox());
        scenaPrincipale.getStylesheets().add(getClass().getResource(FOGLIO_STILE).toExternalForm());
        palcoscenico.setScene(scenaPrincipale);
        palcoscenico.setMaximized(true);

        mostraLogin(null);
        palcoscenico.show();
    }

    private void impostaScena(Parent radice) {
        radice.getStyleClass().add(STILE_PANNELLO);
        if (radice instanceof Region) {
            Region contenuto = (Region) radice;
            contenuto.setMaxWidth(LARGHEZZA_CONTENUTO);
            contenuto.setMaxHeight(Region.USE_PREF_SIZE);
        }

        StackPane sfondo = new StackPane(radice);
        sfondo.getStyleClass().add(STILE_SFONDO);
        StackPane.setAlignment(radice, Pos.TOP_CENTER);
        StackPane.setMargin(radice, new Insets(30));

        scenaPrincipale.setRoot(sfondo);
    }

    private void impostaScenaScorrevole(VBox contenuto) {
        ScrollPane scorrimento = new ScrollPane(contenuto);
        scorrimento.setFitToWidth(true);
        impostaScena(scorrimento);
    }

    private void mostraLogin(String messaggioIniziale) {
        Label titolo = new Label("PrenotazioneViaggi");
        titolo.getStyleClass().add(STILE_TITOLO);

        TextField campoNickname = new TextField();
        campoNickname.setPromptText("Nickname");
        PasswordField campoPassword = new PasswordField();
        campoPassword.setPromptText("Password");

        Label messaggio = creaEtichettaMessaggio();
        if (messaggioIniziale != null) {
            segnalaErrore(messaggio, messaggioIniziale);
        }

        Button bottoneLogin = new Button("Login");
        bottoneLogin.setOnAction(evento -> eseguiLogin(campoNickname.getText(), campoPassword.getText(), messaggio));

        Button bottoneCatalogoOspite = new Button("Consulta il catalogo senza registrarti");
        bottoneCatalogoOspite.getStyleClass().add(STILE_SECONDARIO);
        bottoneCatalogoOspite.setOnAction(evento -> mostraCatalogo(null));

        VBox layout = new VBox(12, titolo, campoNickname, campoPassword, bottoneLogin, bottoneCatalogoOspite,
                bottoneNonImplementato("Registrati", messaggio),
                bottoneNonImplementato("Recupera password", messaggio),
                bottoneNonImplementato("Visualizza recensioni", messaggio),
                bottoneNonImplementato("Contatta assistenza", messaggio),
                messaggio);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private void eseguiLogin(String nickname, String password, Label messaggio) {
        LoginBean credenziali = new LoginBean();
        credenziali.setNickname(nickname);
        credenziali.setPassword(password);

        EsitoLoginBean esito = loginControllerGrafico.effettuaLogin(credenziali);
        if (esito.isSuccesso()) {
            mostraMenuPrincipale(esito.getUtente());
        } else {
            segnalaErrore(messaggio, esito.getMessaggio());
        }
    }

    private void mostraMenuPrincipale(UtenteVistaBean utente) {
        if (utente.isAgenzia()) {
            mostraMenuAgenzia(utente);
        } else {
            mostraMenuCliente(utente);
        }
    }

    private void mostraMenuCliente(UtenteVistaBean utente) {
        Label titolo = new Label("Benvenuto, " + utente.getNome() + "!");
        titolo.getStyleClass().add(STILE_TITOLO);

        Label messaggio = creaEtichettaMessaggio();

        Button bottonePrenota = new Button("Consulta il catalogo e prenota");
        bottonePrenota.setOnAction(evento -> mostraCatalogo(utente));

        VBox layout = new VBox(12, titolo, bottonePrenota,
                bottoneNonImplementato("Modifica una prenotazione", messaggio),
                bottoneNonImplementato("Annulla una prenotazione", messaggio),
                bottoneNonImplementato("Visualizza recensioni", messaggio),
                bottoneNonImplementato("Contatta assistenza", messaggio),
                creaBottoneLogout(), messaggio);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private void mostraMenuAgenzia(UtenteVistaBean utente) {
        Label titolo = new Label("Benvenuta, " + utente.getNome() + "! (agenzia)");
        titolo.getStyleClass().add(STILE_TITOLO);

        Label messaggio = creaEtichettaMessaggio();

        Button bottoneCatalogo = new Button("Consulta il catalogo");
        bottoneCatalogo.setOnAction(evento -> mostraCatalogo(utente));

        VBox layout = new VBox(12, titolo, bottoneCatalogo,
                bottoneNonImplementato("Gestisci pacchetti", messaggio),
                bottoneNonImplementato("Visualizza prenotazioni di un pacchetto", messaggio),
                bottoneNonImplementato("Visualizza richieste di assistenza", messaggio),
                bottoneNonImplementato("Visualizza recensioni", messaggio),
                creaBottoneLogout(), messaggio);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private Button creaBottoneLogout() {
        Button bottoneLogout = new Button("Logout");
        bottoneLogout.getStyleClass().add(STILE_SECONDARIO);
        bottoneLogout.setOnAction(evento -> mostraLogin(null));
        return bottoneLogout;
    }

    private void mostraCatalogo(UtenteVistaBean utenteOspite) {
        mostraCatalogo(utenteOspite, null);
    }

    private void mostraCatalogo(UtenteVistaBean utenteOspite, String destinazioneCercata) {
        Label titolo = new Label("Catalogo pacchetti");
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        Label messaggio = creaEtichettaMessaggio();
        VBox lista = new VBox(8);

        EsitoCatalogoBean esito = prenotazioneControllerGrafico.cercaNelCatalogo(destinazioneCercata);
        if (esito.isSuccesso()) {
            titolo.setText(esito.getCatalogo().getTitolo());
            if (esito.getCatalogo().getPacchetti().isEmpty()) {
                lista.getChildren().add(new Label("Nessun pacchetto corrisponde alla ricerca."));
            }
            for (PacchettoVistaBean pacchetto : esito.getCatalogo().getPacchetti()) {
                lista.getChildren().add(rigaCatalogo(utenteOspite, pacchetto));
            }
        } else {
            segnalaErrore(messaggio, esito.getMessaggio());
        }

        TextField campoRicerca = creaCampo("Cerca per destinazione");
        campoRicerca.setText(destinazioneCercata == null ? "" : destinazioneCercata);
        Button bottoneCerca = new Button("Cerca");
        bottoneCerca.setOnAction(evento -> mostraCatalogo(utenteOspite, campoRicerca.getText()));
        HBox rigaRicerca = new HBox(8, campoRicerca, bottoneCerca);
        HBox.setHgrow(campoRicerca, Priority.ALWAYS);

        ScrollPane scorrimento = new ScrollPane(lista);
        scorrimento.setFitToWidth(true);
        scorrimento.setPrefHeight(360);

        Button bottoneIndietro = new Button(utenteOspite != null ? TESTO_TORNA_AL_MENU : "Torna al login");
        bottoneIndietro.getStyleClass().add(STILE_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> tornaIndietro(utenteOspite));

        VBox layout = new VBox(12, titolo, rigaRicerca, scorrimento, messaggio, bottoneIndietro);
        layout.setPadding(new Insets(25));

        impostaScena(layout);
    }

    private void tornaIndietro(UtenteVistaBean utenteOspite) {
        if (utenteOspite != null) {
            mostraMenuPrincipale(utenteOspite);
        } else {
            mostraLogin(null);
        }
    }

    private HBox rigaCatalogo(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        Label descrizione = new Label(pacchetto.getDestinazione() + " - " + pacchetto.getPrezzoSettimanale()
                + " euro a persona a settimana - hotel " + pacchetto.getStelleHotel() + " stelle - "
                + pacchetto.getPostiDisponibili() + " posti disponibili"
                + (pacchetto.isEsaurito() ? " - ESAURITO" : ""));
        descrizione.setMaxWidth(Double.MAX_VALUE);

        Button bottoneDettagli = new Button("Dettagli");
        bottoneDettagli.setOnAction(evento -> mostraDettaglioPacchetto(utente, pacchetto.getId()));

        HBox riga = new HBox(10, descrizione, bottoneDettagli);
        riga.getStyleClass().add(STILE_RIGA);
        riga.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(descrizione, Priority.ALWAYS);
        return riga;
    }

    private void mostraDettaglioPacchetto(UtenteVistaBean utente, int idPacchetto) {
        EsitoPacchettoBean esito = prenotazioneControllerGrafico.dettaglioPacchetto(idPacchetto);
        if (!esito.isSuccesso()) {
            mostraCatalogo(utente);
            return;
        }
        PacchettoVistaBean pacchetto = esito.getPacchetto();

        Label titolo = new Label(pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_TITOLO);

        Label dettagli = new Label("Prenotabile dal " + Formattatore.testoDaMillis(pacchetto.getDataPartenza())
                + " al " + Formattatore.testoDaMillis(pacchetto.getDataRientro()) + "\n"
                + "Durata del soggiorno: 1 o 2 settimane\n"
                + "Prezzo: " + pacchetto.getPrezzoSettimanale() + " euro a persona per una settimana\n"
                + "Posti disponibili: " + pacchetto.getPostiDisponibili() + "\n"
                + "Hotel: " + pacchetto.getStelleHotel() + " stelle\n"
                + "Volo: " + pacchetto.getDescrizioneVolo());
        dettagli.setWrapText(true);

        Button bottonePrenota = new Button("Prenota ora");
        bottonePrenota.setOnAction(evento -> {
            if (utente == null) {
                mostraLogin(TESTO_LOGIN_PER_PRENOTARE);
            } else {
                mostraDatiViaggio(utente, pacchetto);
            }
        });
        boolean prenotabile = utente == null || !utente.isAgenzia();
        bottonePrenota.setVisible(prenotabile);
        bottonePrenota.setManaged(prenotabile);

        Button bottoneIndietro = new Button(TESTO_TORNA_AL_CATALOGO);
        bottoneIndietro.getStyleClass().add(STILE_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraCatalogo(utente));

        VBox layout = new VBox(12, titolo, dettagli, bottonePrenota, bottoneIndietro);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private void mostraDatiViaggio(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        Label titolo = new Label("Dati del viaggio: " + pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        Label disponibilita = new Label(descriviDisponibilita(pacchetto));
        disponibilita.setWrapText(true);

        DatePicker selettorePartenza = new DatePicker(Formattatore.dataDaMillis(pacchetto.getDataPartenza()));
        HBox rigaPartenza = new HBox(10, new Label("Partenza:"), selettorePartenza);

        RadioButton radioUnaSettimana = new RadioButton("1 settimana");
        RadioButton radioDueSettimane = new RadioButton("2 settimane");
        ToggleGroup gruppoDurata = new ToggleGroup();
        radioUnaSettimana.setToggleGroup(gruppoDurata);
        radioDueSettimane.setToggleGroup(gruppoDurata);
        radioUnaSettimana.setSelected(true);
        HBox rigaDurata = new HBox(15, new Label("Durata:"), radioUnaSettimana, radioDueSettimane);

        Spinner<Integer> spinnerPartecipanti = new Spinner<>(1, MASSIMO_PARTECIPANTI, 1);
        spinnerPartecipanti.setEditable(true);
        HBox rigaNumero = new HBox(10, new Label("Partecipanti (incluso te):"), spinnerPartecipanti);

        VBox campiPartecipanti = new VBox(8);
        List<TextField[]> campiPerPartecipante = new ArrayList<>();
        Label preventivo = new Label();
        Label messaggio = creaEtichettaMessaggio();

        Button bottoneListaAttesa = bottoneNonImplementato(TESTO_LISTA_ATTESA, messaggio);
        mostraBlocco(bottoneListaAttesa, false);

        Runnable aggiornaPrezzo = () -> {
            aggiornaPreventivo(preventivo, costruisciBeanViaggio(utente, pacchetto, selettorePartenza.getValue(),
                    radioUnaSettimana.isSelected(), campiPerPartecipante));
            mostraBlocco(bottoneListaAttesa, pacchetto.postiInsufficientiPer(spinnerPartecipanti.getValue()));
        };

        spinnerPartecipanti.valueProperty().addListener((osservato, vecchio, nuovo) -> {
            ricostruisciCampiPartecipanti(campiPartecipanti, campiPerPartecipante, nuovo);
            aggiornaPrezzo.run();
        });
        selettorePartenza.valueProperty().addListener((osservato, vecchio, nuovo) -> aggiornaPrezzo.run());
        gruppoDurata.selectedToggleProperty().addListener((osservato, vecchio, nuovo) -> aggiornaPrezzo.run());

        ricostruisciCampiPartecipanti(campiPartecipanti, campiPerPartecipante, spinnerPartecipanti.getValue());
        aggiornaPrezzo.run();

        Button bottoneProcedi = new Button("Procedi al pagamento");
        bottoneProcedi.setOnAction(evento -> procediAlPagamento(utente, pacchetto,
                costruisciBeanViaggio(utente, pacchetto, selettorePartenza.getValue(),
                        radioUnaSettimana.isSelected(), campiPerPartecipante),
                messaggio));

        Button bottoneIndietro = new Button(TESTO_TORNA_AL_CATALOGO);
        bottoneIndietro.getStyleClass().add(STILE_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraCatalogo(utente));

        VBox layout = new VBox(10, titolo, disponibilita, rigaPartenza, rigaDurata, rigaNumero, campiPartecipanti,
                preventivo, bottoneListaAttesa, bottoneProcedi, messaggio, bottoneIndietro);
        layout.setPadding(new Insets(25));

        impostaScenaScorrevole(layout);
    }

    private void procediAlPagamento(UtenteVistaBean utente, PacchettoVistaBean pacchetto,
            PrenotazioneBean dati, Label messaggio) {
        EsitoOperazioneBean esito = prenotazioneControllerGrafico.verificaDatiViaggio(dati);
        if (esito.isSuccesso()) {
            mostraPagamento(utente, pacchetto, dati);
        } else {
            segnalaErrore(messaggio, esito.getMessaggio());
        }
    }

    private PrenotazioneBean costruisciBeanViaggio(UtenteVistaBean utente, PacchettoVistaBean pacchetto,
            LocalDate partenza, boolean unaSettimana, List<TextField[]> campiPerPartecipante) {

        PrenotazioneBean dati = new PrenotazioneBean();
        dati.setIdUtente(utente.getId());
        dati.setIdPacchetto(pacchetto.getId());
        dati.setDataPartenzaViaggio(partenza == null ? 0L : Formattatore.millisDaData(partenza));
        dati.setSettimaneSoggiorno(unaSettimana ? 1 : 2);
        dati.setPartecipanti(leggiPartecipanti(campiPerPartecipante));
        return dati;
    }

    private void aggiornaPreventivo(Label preventivo, PrenotazioneBean dati) {
        EsitoPreventivoBean esito = prenotazioneControllerGrafico.calcolaPreventivo(dati);
        if (esito.isSuccesso()) {
            preventivo.setStyle(COLORE_SUCCESSO);
            preventivo.setText("Prezzo totale: " + esito.getImportoTotale() + " euro");
        } else {
            preventivo.setStyle(COLORE_ERRORE);
            preventivo.setText(esito.getMessaggio());
        }
    }

    private void ricostruisciCampiPartecipanti(VBox contenitore, List<TextField[]> campiPerPartecipante, int numero) {
        contenitore.getChildren().clear();
        campiPerPartecipante.clear();

        for (int posizione = 1; posizione <= numero; posizione++) {
            Label etichetta = new Label("Partecipante " + posizione);
            etichetta.getStyleClass().add(STILE_SOTTOTITOLO);

            TextField campoNome = creaCampo("Nome");
            TextField campoCognome = creaCampo("Cognome");
            TextField campoDataNascita = creaCampo("Data di nascita (" + Formattatore.FORMATO_LEGGIBILE + ")");
            TextField campoCodiceFiscale = creaCampo("Codice fiscale");

            contenitore.getChildren().add(new VBox(4, etichetta,
                    new HBox(8, campoNome, campoCognome),
                    new HBox(8, campoDataNascita, campoCodiceFiscale)));
            campiPerPartecipante.add(new TextField[] { campoNome, campoCognome, campoDataNascita, campoCodiceFiscale });
        }
    }

    private List<PartecipanteBean> leggiPartecipanti(List<TextField[]> campiPerPartecipante) {
        List<PartecipanteBean> partecipanti = new ArrayList<>();
        for (TextField[] campi : campiPerPartecipante) {
            PartecipanteBean partecipante = new PartecipanteBean();
            partecipante.setNome(campi[0].getText());
            partecipante.setCognome(campi[1].getText());
            partecipante.setDataNascita(campi[2].getText());
            partecipante.setCodiceFiscale(campi[3].getText());
            partecipanti.add(partecipante);
        }
        return partecipanti;
    }

    private void mostraPagamento(UtenteVistaBean utente, PacchettoVistaBean pacchetto, PrenotazioneBean dati) {
        Label titolo = new Label("Pagamento: " + pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        Label preventivo = new Label();
        aggiornaPreventivo(preventivo, dati);

        RadioButton radioCarta = new RadioButton("Carta di credito");
        RadioButton radioPaypal = new RadioButton("PayPal");
        ToggleGroup gruppoMetodo = new ToggleGroup();
        radioCarta.setToggleGroup(gruppoMetodo);
        radioPaypal.setToggleGroup(gruppoMetodo);
        radioCarta.setSelected(true);

        TextField campoNumeroCarta = creaCampo("Numero carta");
        TextField campoTitolare = creaCampo("Titolare");
        TextField campoScadenza = creaCampo("Scadenza (MM/AA)");
        TextField campoCvv = creaCampo("CVV");
        VBox campiCarta = new VBox(8, campoNumeroCarta, campoTitolare, campoScadenza, campoCvv);

        TextField campoEmailPaypal = creaCampo("Email PayPal");
        PasswordField campoPasswordPaypal = new PasswordField();
        campoPasswordPaypal.setPromptText("Password PayPal");
        VBox campiPaypal = new VBox(8, campoEmailPaypal, campoPasswordPaypal);
        mostraBlocco(campiPaypal, false);

        gruppoMetodo.selectedToggleProperty().addListener((osservato, vecchio, nuovo) -> {
            mostraBlocco(campiCarta, nuovo == radioCarta);
            mostraBlocco(campiPaypal, nuovo != radioCarta);
        });

        Label messaggio = creaEtichettaMessaggio();

        Button bottoneConferma = new Button("Conferma prenotazione");
        bottoneConferma.setOnAction(evento -> {
            dati.setDatiPagamento(radioCarta.isSelected()
                    ? datiCarta(campoNumeroCarta.getText(), campoTitolare.getText(), campoScadenza.getText(),
                            campoCvv.getText())
                    : datiPaypal(campoEmailPaypal.getText(), campoPasswordPaypal.getText()));
            confermaPrenotazione(utente, dati, messaggio);
        });

        Button bottoneIndietro = new Button("Torna ai dati del viaggio");
        bottoneIndietro.getStyleClass().add(STILE_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraDatiViaggio(utente, pacchetto));

        VBox layout = new VBox(10, titolo, preventivo, new Label("Metodo di pagamento:"),
                new HBox(15, radioCarta, radioPaypal), campiCarta, campiPaypal, bottoneConferma, messaggio,
                bottoneIndietro);
        layout.setPadding(new Insets(25));

        impostaScenaScorrevole(layout);
    }

    private void confermaPrenotazione(UtenteVistaBean utente, PrenotazioneBean dati, Label messaggio) {
        EsitoPrenotazioneBean esito = prenotazioneControllerGrafico.compilaPrenotazione(dati);
        if (esito.isSuccesso()) {
            mostraConferma(utente, esito.getPrenotazione());
        } else {
            segnalaErrore(messaggio, esito.getMessaggio());
        }
    }

    private DatiPagamentoBean datiCarta(String numero, String titolare, String scadenza, String cvv) {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_CARTA);
        dati.setNumeroCarta(numero);
        dati.setTitolare(titolare);
        dati.setScadenza(scadenza);
        dati.setCvv(cvv);
        return dati;
    }

    private DatiPagamentoBean datiPaypal(String email, String password) {
        DatiPagamentoBean dati = new DatiPagamentoBean();
        dati.setMetodoPagamento(DatiPagamentoBean.PAGAMENTO_PAYPAL);
        dati.setEmailPaypal(email);
        dati.setPasswordPaypal(password);
        return dati;
    }

    private void mostraConferma(UtenteVistaBean utente, PrenotazioneVistaBean prenotazione) {
        Label titolo = new Label("Prenotazione confermata");
        titolo.getStyleClass().add(STILE_TITOLO);

        Label riepilogo = new Label("Numero prenotazione: " + prenotazione.getId() + "\n"
                + "Destinazione: " + prenotazione.getDestinazione() + "\n"
                + "Partenza: " + Formattatore.testoDaMillis(prenotazione.getDataPartenzaViaggio()) + "\n"
                + "Rientro: " + Formattatore.testoDaMillis(prenotazione.getDataRientroViaggio()) + "\n"
                + "Pagamento: " + prenotazione.getDescrizionePagamento());
        riepilogo.setWrapText(true);

        Label titoloPartecipanti = new Label("Partecipanti");
        titoloPartecipanti.getStyleClass().add(STILE_SOTTOTITOLO);

        VBox listaPartecipanti = new VBox(4);
        for (PartecipanteVistaBean partecipante : prenotazione.getPartecipanti()) {
            listaPartecipanti.getChildren().add(new Label(Formattatore.descriviPartecipante(partecipante)));
        }

        Label conferma = new Label("Riceverai una notifica di conferma all'indirizzo " + utente.getEmail() + ".");
        conferma.setStyle(COLORE_SUCCESSO);
        conferma.setWrapText(true);

        Button bottoneMenu = new Button(TESTO_TORNA_AL_MENU);
        bottoneMenu.setOnAction(evento -> mostraMenuPrincipale(utente));

        VBox layout = new VBox(12, titolo, riepilogo, titoloPartecipanti, listaPartecipanti, conferma, bottoneMenu);
        layout.setPadding(new Insets(30));

        impostaScenaScorrevole(layout);
    }

    private String descriviDisponibilita(PacchettoVistaBean pacchetto) {
        return "Prenotabile dal " + Formattatore.testoDaMillis(pacchetto.getDataPartenza())
                + " al " + Formattatore.testoDaMillis(pacchetto.getDataRientro())
                + " - posti ancora liberi: " + pacchetto.getPostiDisponibili();
    }

    private TextField creaCampo(String suggerimento) {
        TextField campo = new TextField();
        campo.setPromptText(suggerimento);
        return campo;
    }

    private Label creaEtichettaMessaggio() {
        Label messaggio = new Label();
        messaggio.setWrapText(true);
        return messaggio;
    }

    private void segnalaErrore(Label messaggio, String testo) {
        messaggio.setStyle(COLORE_ERRORE);
        messaggio.setText(testo);
    }

    private void mostraBlocco(Region blocco, boolean visibile) {
        blocco.setVisible(visibile);
        blocco.setManaged(visibile);
    }

    private Button bottoneNonImplementato(String testo, Label messaggio) {
        Button bottone = new Button(testo);
        bottone.getStyleClass().add(STILE_SECONDARIO);
        bottone.setOnAction(evento -> segnalaErrore(messaggio, TESTO_NON_IMPLEMENTATO));
        return bottone;
    }
}

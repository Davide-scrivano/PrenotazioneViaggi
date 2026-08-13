package gui;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

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
import controller.grafico.AssistenzaControllerGraficoGUI;
import controller.grafico.CatalogoControllerGraficoGUI;
import controller.grafico.ListaAttesaControllerGraficoGUI;
import controller.grafico.LoginControllerGraficoGUI;
import controller.grafico.PacchettoControllerGraficoGUI;
import controller.grafico.PrenotazioneControllerGraficoGUI;
import controller.grafico.PrenotazioniPacchettoControllerGraficoGUI;
import controller.grafico.RecensioneControllerGraficoGUI;
import controller.grafico.RegistrazioneControllerGraficoGUI;
import control.GestoreAssistenza;
import control.GestoreListaAttesa;
import control.GestorePrenotazioni;
import control.GestoreRecensioni;
import control.GestoreUtenti;
import util.Formattatore;

public class GuiJavaFXApp extends Application {

    private static final String FOGLIO_STILE = "/gui/stile.css";
    private static final String STILE_TITOLO = "titolo";
    private static final String STILE_SOTTOTITOLO = "sottotitolo";
    private static final String STILE_BUTTON_SECONDARIO = "button-secondario";
    private static final String STILE_BUTTON_PERICOLO = "button-pericolo";
    private static final String COLORE_SUCCESSO = "-fx-text-fill: #2e7d32;";
    private static final String COLORE_ERRORE = "-fx-text-fill: #c62828;";
    private static final String TESTO_TORNA_AL_LOGIN = "Torna al login";
    private static final String TESTO_TORNA_AL_MENU = "Torna al menu";
    private static final String TESTO_TORNA_GESTIONE = "Torna alla gestione pacchetti";
    private static final String TESTO_CONTATTA_ASSISTENZA = "Contatta assistenza";
    private static final String TESTO_EMAIL = "Email";
    private static final String TESTO_CHIUDI = "Chiudi";
    private static final double LARGHEZZA_CONTENUTO = 820;
    private static final String STILE_PANNELLO = "pannello";
    private static final String STILE_SFONDO = "sfondo";
    private static final String SUFFISSO_EURO_A_PERSONA = " euro a persona a settimana";
    private static final String STILE_RIGA_ELEMENTO = "riga-elemento";
    private static final String TESTO_DATA_NON_VALIDA = "Data non valida (formato gg/mm/aaaa).";
    private static final String TESTO_NUMERI_NON_VALIDI = "Prezzo o posti non validi.";
    private static final String FORMATO_DATA_TESTO = "dd/MM/yyyy";
    private static final String TESTO_NESSUNA_RECENSIONE = "nessuna recensione";
    private static final int STELLE_MINIME = 1;
    private static final int STELLE_MASSIME = 5;

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern(FORMATO_DATA_TESTO);

    private Stage stage;
    private Scene scenaPrincipale;

    private static GestoreUtenti gestoreUtentiCondiviso;
    private static GestorePrenotazioni gestorePrenotazioniCondiviso;
    private static GestoreRecensioni gestoreRecensioniCondiviso;
    private static GestoreAssistenza gestoreAssistenzaCondiviso;
    private static GestoreListaAttesa gestoreListaAttesaCondiviso;

    private final LoginControllerGraficoGUI loginControllerGrafico =
            new LoginControllerGraficoGUI(gestoreUtentiCondiviso);
    private final RegistrazioneControllerGraficoGUI registrazioneControllerGrafico =
            new RegistrazioneControllerGraficoGUI(gestoreUtentiCondiviso);
    private final CatalogoControllerGraficoGUI catalogoControllerGrafico =
            new CatalogoControllerGraficoGUI(gestoreRecensioniCondiviso);
    private final PrenotazioneControllerGraficoGUI prenotazioneControllerGrafico =
            new PrenotazioneControllerGraficoGUI(gestorePrenotazioniCondiviso, gestoreUtentiCondiviso);
    private final RecensioneControllerGraficoGUI recensioneControllerGrafico =
            new RecensioneControllerGraficoGUI(gestoreRecensioniCondiviso, gestoreUtentiCondiviso);
    private final AssistenzaControllerGraficoGUI assistenzaControllerGrafico =
            new AssistenzaControllerGraficoGUI(gestoreAssistenzaCondiviso, gestoreUtentiCondiviso);
    private final PacchettoControllerGraficoGUI pacchettoControllerGrafico =
            new PacchettoControllerGraficoGUI();
    private final ListaAttesaControllerGraficoGUI listaAttesaControllerGrafico =
            new ListaAttesaControllerGraficoGUI(gestoreListaAttesaCondiviso, gestoreUtentiCondiviso);
    private final PrenotazioniPacchettoControllerGraficoGUI prenotazioniPacchettoControllerGrafico =
            new PrenotazioniPacchettoControllerGraficoGUI(gestorePrenotazioniCondiviso);

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("PrenotazioneViaggi");

        scenaPrincipale = new Scene(new VBox());
        scenaPrincipale.getStylesheets().add(getClass().getResource(FOGLIO_STILE).toExternalForm());
        stage.setScene(scenaPrincipale);
        stage.setMaximized(true);

        mostraLogin(null);
        stage.show();
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

    // ---------- LOGIN ----------

    private void mostraLogin(String messaggioIniziale) {
        Label titolo = new Label("PrenotazioneViaggi");
        titolo.getStyleClass().add(STILE_TITOLO);

        TextField campoNickname = new TextField();
        campoNickname.setPromptText("Nickname");

        PasswordField campoPassword = new PasswordField();
        campoPassword.setPromptText("Password");

        Label messaggio = new Label(messaggioIniziale != null ? messaggioIniziale : "");
        messaggio.setStyle(COLORE_SUCCESSO);
        messaggio.setWrapText(true);

        Button bottoneLogin = new Button("Login");
        bottoneLogin.setOnAction(evento -> {
            EsitoLogin esito = loginControllerGrafico.gestisciLogin(campoNickname.getText(), campoPassword.getText());
            if (esito.isSuccesso()) {
                mostraMenuPrincipale(esito.getUtente());
            } else {
                messaggio.setStyle(COLORE_ERRORE);
                messaggio.setText(esito.getMessaggioErrore());
            }
        });

        Button bottoneRegistrati = new Button("Non hai un account? Registrati");
        bottoneRegistrati.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneRegistrati.setOnAction(evento -> mostraRegistrazione());

        Button bottoneCatalogoOspite = new Button("Consulta il catalogo senza registrarti");
        bottoneCatalogoOspite.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneCatalogoOspite.setOnAction(evento -> mostraCatalogo(null));

        Button bottoneRecuperaPassword = new Button("Password dimenticata?");
        bottoneRecuperaPassword.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneRecuperaPassword.setOnAction(evento -> mostraRecuperaPassword());

        // Use case diagram: anche "contact assistance" non richiede login.
        Button bottoneAssistenza = new Button(TESTO_CONTATTA_ASSISTENZA);
        bottoneAssistenza.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneAssistenza.setOnAction(evento -> mostraContattaAssistenza(null));

        VBox layout = new VBox(12, titolo, campoNickname, campoPassword, bottoneLogin,
                bottoneRegistrati, bottoneCatalogoOspite, bottoneRecuperaPassword, bottoneAssistenza, messaggio);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private void mostraContattaAssistenza(UtenteVistaBean utenteOpzionale) {
        Label titolo = new Label(TESTO_CONTATTA_ASSISTENZA);
        titolo.getStyleClass().add(STILE_TITOLO);

        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome");
        TextField campoEmail = new TextField();
        campoEmail.setPromptText(TESTO_EMAIL);
        if (utenteOpzionale != null) {
            campoNome.setText(assistenzaControllerGrafico.nomePrecompilato());
            campoEmail.setText(assistenzaControllerGrafico.emailPrecompilata());
            campoNome.setDisable(true);
            campoEmail.setDisable(true);
        }

        TextField campoMessaggio = new TextField();
        campoMessaggio.setPromptText("Messaggio");

        Label esito = new Label();
        esito.setWrapText(true);

        Button bottoneInvia = new Button("Invia richiesta");
        bottoneInvia.setOnAction(evento -> {
            EsitoOperazione esitoRichiesta = assistenzaControllerGrafico.gestisciRichiesta(campoNome.getText(),
                    campoEmail.getText(), campoMessaggio.getText());
            if (esitoRichiesta.isSuccesso()) {
                esito.setStyle(COLORE_SUCCESSO);
                campoMessaggio.clear();
            } else {
                esito.setStyle(COLORE_ERRORE);
            }
            esito.setText(esitoRichiesta.getMessaggio());
        });

        Button bottoneIndietro = new Button("Torna indietro");
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> tornaIndietro(utenteOpzionale));

        VBox layout = new VBox(12, titolo, campoNome, campoEmail, campoMessaggio, bottoneInvia, esito,
                bottoneIndietro);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private void tornaIndietro(UtenteVistaBean utenteOpzionale) {
        if (utenteOpzionale != null) {
            mostraMenuPrincipale(utenteOpzionale);
        } else {
            mostraLogin(null);
        }
    }

    private void mostraRecuperaPassword() {
        Label titolo = new Label("Recupera password");
        titolo.getStyleClass().add(STILE_TITOLO);

        TextField campoEmail = new TextField();
        campoEmail.setPromptText(TESTO_EMAIL);

        Label esito = new Label();
        esito.setWrapText(true);

        Button bottoneRecupera = new Button("Recupera password");
        bottoneRecupera.setOnAction(evento -> {
            EsitoRecuperaPassword esitoRecupero = loginControllerGrafico.recuperaPassword(campoEmail.getText());
            if (esitoRecupero.isSuccesso()) {
                esito.setStyle(COLORE_SUCCESSO);
                esito.setText("La tua password e': " + esitoRecupero.getPassword());
            } else {
                esito.setStyle(COLORE_ERRORE);
                esito.setText(esitoRecupero.getMessaggioErrore());
            }
        });

        Button bottoneIndietro = new Button(TESTO_TORNA_AL_LOGIN);
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraLogin(null));

        VBox layout = new VBox(12, titolo, campoEmail, bottoneRecupera, esito, bottoneIndietro);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    // ---------- REGISTRAZIONE ----------

    private void mostraRegistrazione() {
        Label titolo = new Label("Registrazione");
        titolo.getStyleClass().add(STILE_TITOLO);

        TextField campoNickname = new TextField();
        campoNickname.setPromptText("Nickname");
        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome");
        TextField campoCognome = new TextField();
        campoCognome.setPromptText("Cognome");
        TextField campoEmail = new TextField();
        campoEmail.setPromptText(TESTO_EMAIL);
        PasswordField campoPassword = new PasswordField();
        campoPassword.setPromptText("Password");

        Label messaggio = new Label();
        messaggio.setStyle(COLORE_ERRORE);
        messaggio.setWrapText(true);

        Button bottoneRegistrati = new Button("Registrati");
        bottoneRegistrati.setOnAction(evento -> {
            EsitoRegistrazione esito = registrazioneControllerGrafico.gestisciRegistrazione(campoNickname.getText(),
                    campoNome.getText(), campoCognome.getText(), campoEmail.getText(), campoPassword.getText());
            if (esito.isSuccesso()) {
                mostraLogin("Registrazione completata, effettua il login.");
            } else {
                messaggio.setText(esito.getMessaggioErrore());
            }
        });

        Button bottoneIndietro = new Button(TESTO_TORNA_AL_LOGIN);
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraLogin(null));

        VBox layout = new VBox(10, titolo, campoNickname, campoNome, campoCognome, campoEmail,
                campoPassword, bottoneRegistrati, bottoneIndietro, messaggio);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    // ---------- MENU PRINCIPALE ----------

    private void mostraMenuPrincipale(UtenteVistaBean utente) {
        if (utente.isAgenzia()) {
            mostraMenuAgenzia(utente);
        } else {
            mostraMenuConsumer(utente);
        }
    }

    private void mostraMenuConsumer(UtenteVistaBean utente) {
        Label titolo = new Label("Benvenuto, " + utente.getNome() + "!");
        titolo.getStyleClass().add(STILE_TITOLO);

        Button bottoneCatalogo = new Button("Consulta catalogo e prenota");
        bottoneCatalogo.setOnAction(evento -> mostraCatalogo(utente));

        Button bottonePrenotazioni = new Button("Le mie prenotazioni");
        bottonePrenotazioni.setOnAction(evento -> mostraMiePrenotazioni(utente));

        Button bottoneAssistenza = new Button(TESTO_CONTATTA_ASSISTENZA);
        bottoneAssistenza.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneAssistenza.setOnAction(evento -> mostraContattaAssistenza(utente));

        VBox layout = new VBox(15, titolo, bottoneCatalogo, bottonePrenotazioni, bottoneAssistenza,
                creaBottoneLogout());
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private void mostraMenuAgenzia(UtenteVistaBean utente) {
        Label titolo = new Label("Benvenuto, " + utente.getNome() + "! (Agenzia)");
        titolo.getStyleClass().add(STILE_TITOLO);

        Button bottoneCatalogo = new Button("Consulta catalogo (vedi recensioni)");
        bottoneCatalogo.setOnAction(evento -> mostraCatalogo(utente));

        Button bottoneGestionePacchetti = new Button("Gestisci pacchetti");
        bottoneGestionePacchetti.setOnAction(evento -> mostraGestionePacchetti(utente));

        VBox layout = new VBox(15, titolo, bottoneCatalogo, bottoneGestionePacchetti, creaBottoneLogout());
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private Button creaBottoneLogout() {
        Button bottoneLogout = new Button("Logout");
        bottoneLogout.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneLogout.setOnAction(evento -> {
            loginControllerGrafico.logout();
            mostraLogin(null);
        });
        return bottoneLogout;
    }

    // ---------- GESTIONE PACCHETTI (Agency) ----------

    private void mostraGestionePacchetti(UtenteVistaBean utente) {
        Label titolo = new Label("Gestisci pacchetti");
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        VBox listaPacchetti = new VBox(8);
        popolaListaGestionePacchetti(listaPacchetti, utente);

        ScrollPane scroll = new ScrollPane(listaPacchetti);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(240);

        Label titoloForm = new Label("Aggiungi nuovo pacchetto");
        titoloForm.getStyleClass().add(STILE_SOTTOTITOLO);

        FormPacchetto form = new FormPacchetto();

        Label esito = new Label();
        esito.setWrapText(true);

        Button bottoneAggiungi = new Button("Aggiungi pacchetto");
        bottoneAggiungi.setOnAction(evento -> {
            PacchettoBean dati = leggiFormPacchetto(form, esito);
            if (dati == null) {
                return;
            }
            EsitoOperazione esitoAggiunta = pacchettoControllerGrafico.aggiungiPacchetto(dati);
            esito.setStyle(esitoAggiunta.isSuccesso() ? COLORE_SUCCESSO : COLORE_ERRORE);
            esito.setText(esitoAggiunta.getMessaggio());
            popolaListaGestionePacchetti(listaPacchetti, utente);
        });

        Button bottoneIndietro = new Button(TESTO_TORNA_AL_MENU);
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraMenuPrincipale(utente));

        VBox layout = new VBox(10, titolo, scroll, titoloForm, form.getCampi(), bottoneAggiungi, esito,
                bottoneIndietro);
        layout.setPadding(new Insets(25));

        ScrollPane scrollGenerale = new ScrollPane(layout);
        scrollGenerale.setFitToWidth(true);

        impostaScena(scrollGenerale);
    }

    private PacchettoBean leggiFormPacchetto(FormPacchetto form, Label esito) {
        try {
            return form.leggiBean(formatoData);
        } catch (DateTimeParseException e) {
            esito.setStyle(COLORE_ERRORE);
            esito.setText(TESTO_DATA_NON_VALIDA);
            return null;
        } catch (NumberFormatException e) {
            esito.setStyle(COLORE_ERRORE);
            esito.setText(TESTO_NUMERI_NON_VALIDI);
            return null;
        }
    }

    private void mostraModificaPacchetto(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        Label titolo = new Label("Modifica pacchetto #" + pacchetto.getId());
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        FormPacchetto form = new FormPacchetto();
        form.precompila(pacchetto, formatoData);

        Label avviso = new Label("Attenzione: il pacchetto ha "
                + prenotazioniPacchettoControllerGrafico.prenotazioniDelPacchetto(pacchetto.getId()).size()
                + " prenotazioni attive.");
        avviso.setWrapText(true);

        Label esito = new Label();
        esito.setWrapText(true);

        Button bottoneSalva = new Button("Salva modifiche");
        bottoneSalva.setOnAction(evento -> {
            PacchettoBean dati = leggiFormPacchetto(form, esito);
            if (dati == null) {
                return;
            }
            EsitoOperazione esitoModifica = pacchettoControllerGrafico.modificaPacchetto(pacchetto.getId(), dati);
            esito.setStyle(esitoModifica.isSuccesso() ? COLORE_SUCCESSO : COLORE_ERRORE);
            esito.setText(esitoModifica.getMessaggio());
        });

        Button bottoneIndietro = new Button(TESTO_TORNA_GESTIONE);
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraGestionePacchetti(utente));

        VBox layout = new VBox(10, titolo, avviso, form.getCampi(), bottoneSalva, esito, bottoneIndietro);
        layout.setPadding(new Insets(25));

        ScrollPane scrollGenerale = new ScrollPane(layout);
        scrollGenerale.setFitToWidth(true);

        impostaScena(scrollGenerale);
    }

    private void mostraPrenotazioniPacchetto(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        Label titolo = new Label("Prenotazioni del pacchetto #" + pacchetto.getId()
                + " - " + pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        List<PrenotazioneVistaBean> prenotazioni =
                prenotazioniPacchettoControllerGrafico.prenotazioniDelPacchetto(pacchetto.getId());

        VBox lista = new VBox(8);
        if (prenotazioni.isEmpty()) {
            lista.getChildren().add(new Label("Nessuna prenotazione attiva per questo pacchetto."));
        } else {
            for (PrenotazioneVistaBean prenotazione : prenotazioni) {
                lista.getChildren().add(rigaPrenotazionePacchetto(prenotazione));
            }
            lista.getChildren().add(new Label("Totale: " + prenotazioni.size() + " prenotazioni, "
                    + prenotazioniPacchettoControllerGrafico.postiVenduti(pacchetto.getId())
                    + " posti venduti. Posti ancora disponibili: " + pacchetto.getPostiDisponibili() + "."));
        }

        ScrollPane scroll = new ScrollPane(lista);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(330);

        Button bottoneIndietro = new Button(TESTO_TORNA_GESTIONE);
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraGestionePacchetti(utente));

        VBox layout = new VBox(15, titolo, scroll, bottoneIndietro);
        layout.setPadding(new Insets(25));

        impostaScena(layout);
    }

    private HBox rigaPrenotazionePacchetto(PrenotazioneVistaBean prenotazione) {
        Label descrizione = new Label("#" + prenotazione.getId() + " - "
                + prenotazione.getNomeCliente() + " " + prenotazione.getCognomeCliente()
                + " (" + prenotazione.getEmailCliente() + ")\n"
                + prenotazione.getNumeroPartecipanti() + " partecipanti, dal "
                + formattaData(prenotazione.getDataPartenzaViaggio()) + " al "
                + formattaData(prenotazione.getDataRientroViaggio())
                + " - prenotata il " + formattaData(prenotazione.getDataPrenotazione()));
        descrizione.setWrapText(true);
        descrizione.setMaxWidth(Double.MAX_VALUE);

        Label stato = new Label(prenotazione.getStato());

        HBox riga = new HBox(10, descrizione, stato);
        riga.getStyleClass().add(STILE_RIGA_ELEMENTO);
        riga.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(descrizione, Priority.ALWAYS);
        return riga;
    }

    private void popolaListaGestionePacchetti(VBox listaPacchetti, UtenteVistaBean utente) {
        listaPacchetti.getChildren().clear();
        for (PacchettoVistaBean pacchetto : catalogoControllerGrafico.catalogoCompleto()) {
            listaPacchetti.getChildren().add(rigaGestionePacchetto(pacchetto, listaPacchetti, utente));
        }
    }

    private HBox rigaGestionePacchetto(PacchettoVistaBean pacchetto, VBox listaPacchetti, UtenteVistaBean utente) {
        Label descrizione = new Label("#" + pacchetto.getId() + " - " + pacchetto.getDestinazione() + " - "
                + pacchetto.getPrezzoPerPersonaSettimana() + SUFFISSO_EURO_A_PERSONA);

        Button bottonePrenotazioni = new Button("Prenotazioni");
        bottonePrenotazioni.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottonePrenotazioni.setOnAction(evento -> mostraPrenotazioniPacchetto(utente, pacchetto));

        Button bottoneModifica = new Button("Modifica");
        bottoneModifica.setOnAction(evento -> mostraModificaPacchetto(utente, pacchetto));

        Button bottoneRimuovi = new Button("Rimuovi");
        bottoneRimuovi.getStyleClass().add(STILE_BUTTON_PERICOLO);
        bottoneRimuovi.setOnAction(evento -> {
            pacchettoControllerGrafico.rimuoviPacchetto(pacchetto.getId());
            popolaListaGestionePacchetti(listaPacchetti, utente);
        });

        HBox riga = new HBox(10, descrizione, bottonePrenotazioni, bottoneModifica, bottoneRimuovi);
        riga.getStyleClass().add(STILE_RIGA_ELEMENTO);
        riga.setAlignment(Pos.CENTER_LEFT);
        descrizione.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(descrizione, Priority.ALWAYS);
        return riga;
    }

    private static final class FormPacchetto {

        private final TextField campoDestinazione = new TextField();
        private final TextField campoDataPartenza = new TextField();
        private final TextField campoDataRientro = new TextField();
        private final TextField campoPrezzo = new TextField();
        private final TextField campoPosti = new TextField();
        private final Spinner<Integer> spinnerStelle = new Spinner<>(STELLE_MINIME, STELLE_MASSIME, 3);
        private final RadioButton radioDiretto = new RadioButton("Diretto");
        private final RadioButton radioConScalo = new RadioButton("Con scalo");
        private final VBox campi;

        private FormPacchetto() {
            campoDestinazione.setPromptText("Destinazione");
            campoDataPartenza.setPromptText("Disponibile dal (gg/mm/aaaa)");
            campoDataRientro.setPromptText("Disponibile fino al (gg/mm/aaaa)");
            campoPrezzo.setPromptText("Prezzo a persona per una settimana");
            campoPosti.setPromptText("Posti disponibili");

            spinnerStelle.setEditable(true);
            HBox rigaStelle = new HBox(10, new Label("Hotel:"), spinnerStelle, new Label("stelle"));

            ToggleGroup gruppoVolo = new ToggleGroup();
            radioDiretto.setToggleGroup(gruppoVolo);
            radioConScalo.setToggleGroup(gruppoVolo);
            radioDiretto.setSelected(true);
            HBox rigaVolo = new HBox(10, new Label("Volo:"), radioDiretto, radioConScalo);

            campi = new VBox(10, campoDestinazione, campoDataPartenza, campoDataRientro, campoPrezzo,
                    campoPosti, rigaStelle, rigaVolo);
        }

        private VBox getCampi() {
            return campi;
        }

        private void precompila(PacchettoVistaBean pacchetto, DateTimeFormatter formato) {
            campoDestinazione.setText(pacchetto.getDestinazione());
            campoDataPartenza.setText(Formattatore.formattaData(pacchetto.getDataPartenza(), formato));
            campoDataRientro.setText(Formattatore.formattaData(pacchetto.getDataRientro(), formato));
            campoPrezzo.setText(String.valueOf(pacchetto.getPrezzoPerPersonaSettimana()));
            campoPosti.setText(String.valueOf(pacchetto.getPostiDisponibili()));
            spinnerStelle.getValueFactory().setValue(pacchetto.getStelleHotel());
            boolean diretto = PacchettoBean.VOLO_DIRETTO.equals(pacchetto.getCodiceVolo());
            radioDiretto.setSelected(diretto);
            radioConScalo.setSelected(!diretto);
        }

        private PacchettoBean leggiBean(DateTimeFormatter formato) {
            PacchettoBean dati = new PacchettoBean();
            dati.setDestinazione(campoDestinazione.getText());
            dati.setDataPartenza(millisDaTesto(campoDataPartenza.getText(), formato));
            dati.setDataRientro(millisDaTesto(campoDataRientro.getText(), formato));
            dati.setPrezzo(Float.parseFloat(campoPrezzo.getText()));
            dati.setPosti(Integer.parseInt(campoPosti.getText()));
            dati.setStelleHotel(spinnerStelle.getValue());
            dati.setTipoVolo(radioDiretto.isSelected() ? PacchettoBean.VOLO_DIRETTO : PacchettoBean.VOLO_CON_SCALO);
            return dati;
        }

        private static long millisDaTesto(String testo, DateTimeFormatter formato) {
            return LocalDate.parse(testo == null ? "" : testo.trim(), formato)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }

    // ---------- CATALOGO (consultabile anche senza login) ----------

    private void mostraCatalogo(UtenteVistaBean utenteOpzionale) {
        Label titolo = new Label("Catalogo pacchetti disponibili");
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        TextField campoRicerca = new TextField();
        campoRicerca.setPromptText("Cerca per destinazione...");

        VBox listaPacchetti = new VBox(8);
        Runnable aggiornaLista = () -> {
            listaPacchetti.getChildren().clear();
            List<PacchettoVistaBean> risultati = catalogoControllerGrafico.cercaPerDestinazione(campoRicerca.getText());
            if (risultati.isEmpty()) {
                listaPacchetti.getChildren().add(new Label("Nessun risultato disponibile."));
            } else {
                for (PacchettoVistaBean pacchetto : risultati) {
                    listaPacchetti.getChildren().add(rigaPacchetto(utenteOpzionale, pacchetto));
                }
            }
        };
        aggiornaLista.run();

        Button bottoneCerca = new Button("Cerca");
        bottoneCerca.setOnAction(evento -> aggiornaLista.run());

        HBox rigaRicerca = new HBox(8, campoRicerca, bottoneCerca);
        HBox.setHgrow(campoRicerca, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(listaPacchetti);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(330);

        Button bottoneIndietro = new Button(utenteOpzionale != null ? TESTO_TORNA_AL_MENU : TESTO_TORNA_AL_LOGIN);
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> tornaIndietro(utenteOpzionale));

        VBox layout = new VBox(12, titolo, rigaRicerca, scroll, bottoneIndietro);
        layout.setPadding(new Insets(25));

        impostaScena(layout);
    }

    private HBox rigaPacchetto(UtenteVistaBean utenteOpzionale, PacchettoVistaBean pacchetto) {
        String testo = pacchetto.getDestinazione() + " - " + pacchetto.getPrezzoPerPersonaSettimana()
                + SUFFISSO_EURO_A_PERSONA
                + " - hotel " + pacchetto.getStelleHotel() + " stelle"
                + (pacchetto.isEsaurito() ? " (esaurito)" : "")
                + " - voto medio: " + descriviVotoMedio(pacchetto);
        Label descrizione = new Label(testo);

        Button bottoneDettagli = new Button("Dettagli");
        bottoneDettagli.setOnAction(evento -> mostraDettaglioPacchetto(utenteOpzionale, pacchetto.getId()));

        HBox riga = new HBox(10, descrizione, bottoneDettagli);
        riga.getStyleClass().add(STILE_RIGA_ELEMENTO);
        riga.setAlignment(Pos.CENTER_LEFT);
        descrizione.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(descrizione, Priority.ALWAYS);
        return riga;
    }

    // ---------- DETTAGLIO PACCHETTO (trip overview) ----------

    private void mostraDettaglioPacchetto(UtenteVistaBean utenteOpzionale, int idPacchetto) {
        PacchettoVistaBean pacchetto = catalogoControllerGrafico.dettaglioPacchetto(idPacchetto);
        if (pacchetto == null) {
            mostraCatalogo(utenteOpzionale);
            return;
        }

        Label titolo = new Label(pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_TITOLO);

        Label dettagli = new Label(
                "Prenotabile dal " + formattaData(pacchetto.getDataPartenza())
                        + " al " + formattaData(pacchetto.getDataRientro()) + "\n"
                        + "Durata del soggiorno: 1 o 2 settimane, a scelta\n"
                        + "Prezzo: " + pacchetto.getPrezzoPerPersonaSettimana()
                        + " euro a persona per una settimana\n"
                        + "Posti disponibili: " + pacchetto.getPostiDisponibili() + "\n"
                        + "Hotel: " + pacchetto.getStelleHotel() + " stelle\n"
                        + "Volo: " + pacchetto.getDescrizioneVolo() + "\n"
                        + "Voto medio: " + descriviVotoMedio(pacchetto));
        dettagli.setWrapText(true);

        Label titoloRecensioni = new Label("Recensioni");
        titoloRecensioni.getStyleClass().add(STILE_SOTTOTITOLO);

        VBox listaRecensioni = new VBox(6);
        List<RecensioneVistaBean> recensioni = catalogoControllerGrafico.recensioniDelPacchetto(pacchetto.getId());
        if (recensioni.isEmpty()) {
            listaRecensioni.getChildren().add(new Label("Ancora nessuna recensione."));
        } else {
            for (RecensioneVistaBean recensione : recensioni) {
                Label riga = new Label(recensione.getNomeAutore() + " - " + recensione.getVoto() + "/5: "
                        + recensione.getCommento());
                riga.setWrapText(true);
                listaRecensioni.getChildren().add(riga);
            }
        }
        ScrollPane scrollRecensioni = new ScrollPane(listaRecensioni);
        scrollRecensioni.setFitToWidth(true);
        scrollRecensioni.setPrefHeight(100);

        VBox layout = new VBox(10, titolo, dettagli, titoloRecensioni, scrollRecensioni);
        aggiungiAzioniPacchetto(layout, utenteOpzionale, pacchetto);

        Button bottoneIndietro = new Button("Torna al catalogo");
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraCatalogo(utenteOpzionale));
        layout.getChildren().add(bottoneIndietro);

        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private void aggiungiAzioniPacchetto(VBox layout, UtenteVistaBean utenteOpzionale, PacchettoVistaBean pacchetto) {
        boolean isAgenzia = utenteOpzionale != null && utenteOpzionale.isAgenzia();
        if (isAgenzia) {
            return;
        }

        Button bottonePrenota = new Button("Prenota ora");
        bottonePrenota.setOnAction(evento -> {
            if (utenteOpzionale == null) {
                mostraLogin("Effettua il login per completare la prenotazione.");
            } else {
                mostraDialogoPrenotazione(utenteOpzionale, pacchetto);
            }
        });
        layout.getChildren().add(bottonePrenota);

        if (utenteOpzionale == null) {
            return;
        }

        Button bottoneRecensisci = new Button("Lascia una recensione");
        bottoneRecensisci.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneRecensisci.setOnAction(evento -> mostraDialogoRecensione(utenteOpzionale, pacchetto));
        layout.getChildren().add(bottoneRecensisci);

        if (pacchetto.isEsaurito()) {
            Button bottoneListaAttesa = new Button("Iscriviti a lista d'attesa");
            bottoneListaAttesa.getStyleClass().add(STILE_BUTTON_SECONDARIO);
            bottoneListaAttesa.setOnAction(evento -> mostraDialogoListaAttesa(utenteOpzionale, pacchetto));
            layout.getChildren().add(bottoneListaAttesa);
        }
    }

    private String descriviVotoMedio(PacchettoVistaBean pacchetto) {
        if (pacchetto.getNumeroRecensioni() == 0) {
            return TESTO_NESSUNA_RECENSIONE;
        }
        return String.format("%.1f/5 (%d recensioni)", pacchetto.getVotoMedio(), pacchetto.getNumeroRecensioni());
    }

    private void mostraDialogoRecensione(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        Stage dialogo = creaDialogo("Recensisci: " + pacchetto.getDestinazione());

        Label titolo = new Label("Recensisci: " + pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);
        titolo.setWrapText(true);

        Label labelVoto = new Label("Voto (1-5):");
        Spinner<Integer> spinnerVoto = new Spinner<>(STELLE_MINIME, STELLE_MASSIME, STELLE_MASSIME);
        spinnerVoto.setEditable(true);

        TextField campoCommento = new TextField();
        campoCommento.setPromptText("Commento");

        Label esito = new Label();
        esito.setWrapText(true);

        Button bottoneConferma = new Button("Invia recensione");
        bottoneConferma.setOnAction(evento -> {
            EsitoOperazione esitoRecensione = recensioneControllerGrafico.gestisciRecensione(pacchetto.getId(),
                    spinnerVoto.getValue(), campoCommento.getText());
            esito.setStyle(esitoRecensione.isSuccesso() ? COLORE_SUCCESSO : COLORE_ERRORE);
            esito.setText(esitoRecensione.getMessaggio());
        });

        Button bottoneChiudi = new Button(TESTO_CHIUDI);
        bottoneChiudi.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneChiudi.setOnAction(evento -> {
            dialogo.close();
            mostraDettaglioPacchetto(utente, pacchetto.getId());
        });

        VBox layout = new VBox(10, titolo, labelVoto, spinnerVoto, campoCommento, bottoneConferma, esito,
                bottoneChiudi);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        impostaSceneDialogo(dialogo, layout);
    }

    private void mostraDialogoListaAttesa(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        Stage dialogo = creaDialogo("Lista d'attesa: " + pacchetto.getDestinazione());

        Label titolo = new Label("Iscriviti alla lista d'attesa di: " + pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);
        titolo.setWrapText(true);

        Label labelPosti = new Label("Per quante persone ti servono i posti?");
        Spinner<Integer> spinnerPosti = new Spinner<>(1, 20, 1);
        spinnerPosti.setEditable(true);

        Label esito = new Label();
        esito.setWrapText(true);

        Button bottoneConferma = new Button("Iscriviti");
        bottoneConferma.setOnAction(evento -> {
            EsitoOperazione esitoIscrizione = listaAttesaControllerGrafico.iscriviListaAttesa(pacchetto.getId(),
                    spinnerPosti.getValue());
            esito.setStyle(esitoIscrizione.isSuccesso() ? COLORE_SUCCESSO : COLORE_ERRORE);
            esito.setText(esitoIscrizione.getMessaggio());
        });

        Button bottoneChiudi = new Button(TESTO_CHIUDI);
        bottoneChiudi.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneChiudi.setOnAction(evento -> {
            dialogo.close();
            mostraDettaglioPacchetto(utente, pacchetto.getId());
        });

        VBox layout = new VBox(10, titolo, labelPosti, spinnerPosti, bottoneConferma, esito, bottoneChiudi);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        impostaSceneDialogo(dialogo, layout);
    }

    // ---------- PRENOTAZIONE ----------

    private void mostraDialogoPrenotazione(UtenteVistaBean utente, PacchettoVistaBean pacchetto) {
        Label titolo = new Label("Prenotazione: " + pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);
        titolo.setWrapText(true);

        Label labelData = new Label("Scegli la data di partenza (il pacchetto e' prenotabile dal "
                + formattaData(pacchetto.getDataPartenza()) + " al "
                + formattaData(pacchetto.getDataRientro()) + "):");
        DatePicker selettorePartenza = new DatePicker(millisALocalDate(pacchetto.getDataPartenza()));
        HBox rigaDate = new HBox(10, new Label("Partenza:"), selettorePartenza);

        Label labelDurata = new Label("Durata del soggiorno:");
        RadioButton radioUnaSettimana = new RadioButton("1 settimana");
        RadioButton radioDueSettimane = new RadioButton("2 settimane");
        ToggleGroup gruppoDurata = new ToggleGroup();
        radioUnaSettimana.setToggleGroup(gruppoDurata);
        radioDueSettimane.setToggleGroup(gruppoDurata);
        radioUnaSettimana.setSelected(true);
        HBox rigaDurata = new HBox(15, radioUnaSettimana, radioDueSettimane);

        Label labelNumero = new Label("Numero partecipanti (incluso te stesso):");
        int massimoPartecipanti = Math.max(1, pacchetto.getPostiDisponibili());
        Spinner<Integer> spinnerPartecipanti = new Spinner<>(1, massimoPartecipanti, 1);
        spinnerPartecipanti.setEditable(true);

        Label labelPrezzoTotale = new Label();
        VBox campiAltriPartecipanti = new VBox(8);
        List<TextField[]> campiPerPartecipante = new ArrayList<>();

        Runnable aggiornaSchermata = () -> {
            int numero = spinnerPartecipanti.getValue();
            ricostruisciCampiPartecipanti(campiAltriPartecipanti, campiPerPartecipante, numero);
            aggiornaPreventivo(labelPrezzoTotale, pacchetto.getId(), selettorePartenza.getValue(),
                    radioUnaSettimana.isSelected() ? 1 : 2, numero);
        };
        spinnerPartecipanti.valueProperty().addListener((oss, vecchio, nuovo) -> aggiornaSchermata.run());
        selettorePartenza.valueProperty().addListener((oss, vecchio, nuovo) -> aggiornaSchermata.run());
        gruppoDurata.selectedToggleProperty().addListener((oss, vecchio, nuovo) -> aggiornaSchermata.run());
        aggiornaSchermata.run();

        Label esito = new Label();
        esito.setWrapText(true);

        Button bottoneProcedi = new Button("Procedi al pagamento");
        bottoneProcedi.setOnAction(evento -> {
            LocalDate partenzaScelta = selettorePartenza.getValue();
            if (partenzaScelta == null) {
                esito.setStyle(COLORE_ERRORE);
                esito.setText("Seleziona la data di partenza.");
                return;
            }

            PrenotazioneBean datiPrenotazione = new PrenotazioneBean();
            datiPrenotazione.setIdPacchetto(pacchetto.getId());
            datiPrenotazione.setDataPartenzaViaggio(localDateAMillis(partenzaScelta));
            datiPrenotazione.setSettimaneSoggiorno(radioUnaSettimana.isSelected() ? 1 : 2);
            datiPrenotazione.setPartecipanti(leggiPartecipanti(campiPerPartecipante));

            String erroreSintassi = datiPrenotazione.validaSintassiPartecipanti();
            if (erroreSintassi != null) {
                esito.setStyle(COLORE_ERRORE);
                esito.setText(erroreSintassi);
                return;
            }

            mostraDialogoPagamento(utente, pacchetto, datiPrenotazione);
        });

        Button bottoneChiudi = new Button(TESTO_CHIUDI);
        bottoneChiudi.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneChiudi.setOnAction(evento -> mostraCatalogo(utente));

        VBox layout = new VBox(10, titolo, labelData, rigaDate, labelDurata, rigaDurata,
                labelNumero, spinnerPartecipanti, campiAltriPartecipanti, labelPrezzoTotale,
                bottoneProcedi, esito, bottoneChiudi);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);

        impostaScena(scroll);
    }

    private void mostraDialogoPagamento(UtenteVistaBean utente, PacchettoVistaBean pacchetto,
            PrenotazioneBean datiPrenotazione) {
        Label titolo = new Label("Pagamento: " + pacchetto.getDestinazione());
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);
        titolo.setWrapText(true);

        Label labelPrezzoTotale = new Label();
        aggiornaPreventivo(labelPrezzoTotale, datiPrenotazione.getIdPacchetto(),
                millisALocalDate(datiPrenotazione.getDataPartenzaViaggio()), datiPrenotazione.getSettimaneSoggiorno(),
                datiPrenotazione.getPartecipanti().size());

        Label sottotitoloPagamento = new Label("Metodo di pagamento");
        sottotitoloPagamento.getStyleClass().add(STILE_SOTTOTITOLO);

        RadioButton radioCarta = new RadioButton("Carta di credito");
        RadioButton radioPaypal = new RadioButton("PayPal");
        ToggleGroup gruppoPagamento = new ToggleGroup();
        radioCarta.setToggleGroup(gruppoPagamento);
        radioPaypal.setToggleGroup(gruppoPagamento);
        radioCarta.setSelected(true);
        HBox rigaScelta = new HBox(15, radioCarta, radioPaypal);

        TextField campoNumeroCarta = new TextField();
        campoNumeroCarta.setPromptText("Numero carta");
        TextField campoTitolare = new TextField();
        campoTitolare.setPromptText("Titolare");
        TextField campoScadenza = new TextField();
        campoScadenza.setPromptText("Scadenza (MM/AA)");
        TextField campoCvv = new TextField();
        campoCvv.setPromptText("CVV");
        VBox campiCarta = new VBox(8, campoNumeroCarta, campoTitolare, campoScadenza, campoCvv);

        TextField campoEmailPaypal = new TextField();
        campoEmailPaypal.setPromptText("Email PayPal");
        PasswordField campoPasswordPaypal = new PasswordField();
        campoPasswordPaypal.setPromptText("Password PayPal");
        VBox campiPaypal = new VBox(8, campoEmailPaypal, campoPasswordPaypal);
        campiPaypal.setVisible(false);
        campiPaypal.setManaged(false);

        gruppoPagamento.selectedToggleProperty().addListener((oss, vecchio, nuovo) -> {
            boolean cartaSelezionata = nuovo == radioCarta;
            campiCarta.setVisible(cartaSelezionata);
            campiCarta.setManaged(cartaSelezionata);
            campiPaypal.setVisible(!cartaSelezionata);
            campiPaypal.setManaged(!cartaSelezionata);
        });

        Label esito = new Label();
        esito.setWrapText(true);

        Button bottoneIndietro = new Button("Torna ai dati del viaggio");
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraDialogoPrenotazione(utente, pacchetto));

        Button bottoneConferma = new Button("Conferma pagamento");
        bottoneConferma.setOnAction(evento -> {
            if (gruppoPagamento.getSelectedToggle() == radioCarta) {
                datiPrenotazione.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_CARTA);
                datiPrenotazione.setNumeroCarta(campoNumeroCarta.getText());
                datiPrenotazione.setTitolare(campoTitolare.getText());
                datiPrenotazione.setScadenza(campoScadenza.getText());
                datiPrenotazione.setCvv(campoCvv.getText());
            } else {
                datiPrenotazione.setMetodoPagamento(PrenotazioneBean.PAGAMENTO_PAYPAL);
                datiPrenotazione.setEmailPaypal(campoEmailPaypal.getText());
                datiPrenotazione.setPasswordPaypal(campoPasswordPaypal.getText());
            }

            EsitoPrenotazione esitoPrenotazione = prenotazioneControllerGrafico.creaPrenotazione(datiPrenotazione);
            if (esitoPrenotazione.isSuccesso()) {
                esito.setStyle(COLORE_SUCCESSO);
                esito.setText("Prenotazione confermata! Riceverai una notifica di conferma.");
                bottoneConferma.setDisable(true);
                bottoneIndietro.setDisable(true);
            } else {
                esito.setStyle(COLORE_ERRORE);
                esito.setText(esitoPrenotazione.getMessaggioErrore());
            }
        });

        Button bottoneChiudi = new Button(TESTO_CHIUDI);
        bottoneChiudi.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneChiudi.setOnAction(evento -> mostraCatalogo(utente));

        VBox layout = new VBox(10, titolo, labelPrezzoTotale, sottotitoloPagamento, rigaScelta, campiCarta,
                campiPaypal, bottoneConferma, bottoneIndietro, esito, bottoneChiudi);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);

        impostaScena(scroll);
    }

    private void aggiornaPreventivo(Label labelPrezzoTotale, int idPacchetto, LocalDate partenza,
            int settimane, int numeroPartecipanti) {
        if (partenza == null) {
            labelPrezzoTotale.setText("");
            return;
        }

        PrenotazioneBean richiesta = new PrenotazioneBean();
        richiesta.setIdPacchetto(idPacchetto);
        richiesta.setDataPartenzaViaggio(localDateAMillis(partenza));
        richiesta.setSettimaneSoggiorno(settimane);
        List<PartecipanteBean> segnaposto = new ArrayList<>();
        for (int i = 0; i < numeroPartecipanti; i++) {
            segnaposto.add(new PartecipanteBean());
        }
        richiesta.setPartecipanti(segnaposto);

        EsitoPreventivo preventivo = prenotazioneControllerGrafico.calcolaPreventivo(richiesta);
        if (preventivo.isSuccesso()) {
            labelPrezzoTotale.setStyle(COLORE_SUCCESSO);
            labelPrezzoTotale.setText("Prezzo totale: " + preventivo.getImportoTotale() + " euro");
        } else {
            labelPrezzoTotale.setStyle(COLORE_ERRORE);
            labelPrezzoTotale.setText(preventivo.getMessaggioErrore());
        }
    }

    private Stage creaDialogo(String titolo) {
        Stage dialogo = new Stage();
        dialogo.initOwner(stage);
        dialogo.initModality(Modality.WINDOW_MODAL);
        dialogo.setTitle(titolo);
        return dialogo;
    }

    private void impostaSceneDialogo(Stage dialogo, VBox layout) {
        layout.getStyleClass().add(STILE_PANNELLO);
        layout.setMaxWidth(LARGHEZZA_CONTENUTO);
        layout.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane sfondo = new StackPane(layout);
        sfondo.getStyleClass().add(STILE_SFONDO);
        StackPane.setAlignment(layout, Pos.TOP_CENTER);
        StackPane.setMargin(layout, new Insets(30));

        ScrollPane scroll = new ScrollPane(sfondo);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        Scene scena = new Scene(scroll);
        scena.getStylesheets().add(getClass().getResource(FOGLIO_STILE).toExternalForm());
        dialogo.setScene(scena);
        dialogo.setMaximized(true);
        dialogo.show();
    }

    private LocalDate millisALocalDate(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private long localDateAMillis(LocalDate data) {
        return data.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private String formattaData(long millis) {
        return Formattatore.formattaData(millis, formatoData);
    }

    private void ricostruisciCampiPartecipanti(VBox contenitore, List<TextField[]> campiPerPartecipante, int numero) {
        contenitore.getChildren().clear();
        campiPerPartecipante.clear();

        for (int i = 1; i <= numero; i++) {
            Label etichetta = new Label("Partecipante " + i);
            etichetta.getStyleClass().add(STILE_SOTTOTITOLO);

            TextField campoNome = new TextField();
            campoNome.setPromptText("Nome");
            TextField campoCognome = new TextField();
            campoCognome.setPromptText("Cognome");
            TextField campoDataNascita = new TextField();
            campoDataNascita.setPromptText("Data di nascita (gg/mm/aaaa)");
            TextField campoCodiceFiscale = new TextField();
            campoCodiceFiscale.setPromptText("Codice fiscale");

            HBox rigaNome = new HBox(8, campoNome, campoCognome);
            HBox rigaAltriDati = new HBox(8, campoDataNascita, campoCodiceFiscale);
            VBox bloccoPartecipante = new VBox(4, etichetta, rigaNome, rigaAltriDati);

            contenitore.getChildren().add(bloccoPartecipante);
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

    // ---------- LE MIE PRENOTAZIONI ----------

    private void mostraMiePrenotazioni(UtenteVistaBean utente) {
        Label titolo = new Label("Le mie prenotazioni");
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        VBox lista = new VBox(8);
        List<PrenotazioneVistaBean> prenotazioni = prenotazioneControllerGrafico.miePrenotazioni();
        if (prenotazioni.isEmpty()) {
            lista.getChildren().add(new Label("Nessuna prenotazione effettuata."));
        } else {
            for (PrenotazioneVistaBean prenotazione : prenotazioni) {
                lista.getChildren().add(rigaPrenotazione(utente, prenotazione));
            }
        }

        ScrollPane scroll = new ScrollPane(lista);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(280);

        Button bottoneIndietro = new Button(TESTO_TORNA_AL_MENU);
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraMenuPrincipale(utente));

        VBox layout = new VBox(12, titolo, scroll, bottoneIndietro);
        layout.setPadding(new Insets(25));

        impostaScena(layout);
    }

    private HBox rigaPrenotazione(UtenteVistaBean utente, PrenotazioneVistaBean prenotazione) {
        Label descrizione = new Label(prenotazione.getDestinazione() + " - "
                + prenotazione.getNumeroPartecipanti() + " partecipanti - " + prenotazione.getStato());

        HBox riga = new HBox(10, descrizione);
        riga.getStyleClass().add(STILE_RIGA_ELEMENTO);
        riga.setAlignment(Pos.CENTER_LEFT);
        descrizione.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(descrizione, Priority.ALWAYS);

        Button bottoneDettagli = new Button("Dettagli");
        bottoneDettagli.setOnAction(evento -> mostraDettaglioPrenotazione(utente, prenotazione));
        riga.getChildren().add(bottoneDettagli);

        return riga;
    }

    private void mostraDettaglioPrenotazione(UtenteVistaBean utente, PrenotazioneVistaBean prenotazione) {
        Label titolo = new Label(prenotazione.getDestinazione());
        titolo.getStyleClass().add(STILE_TITOLO);

        Label dettagli = new Label(
                "Partenza: " + formattaData(prenotazione.getDataPartenzaViaggio()) + "\n"
                        + "Rientro: " + formattaData(prenotazione.getDataRientroViaggio()) + "\n"
                        + "Prenotata il: " + formattaData(prenotazione.getDataPrenotazione()) + "\n"
                        + "Stato: " + prenotazione.getStato() + "\n"
                        + "Pagamento: " + Formattatore.descriviPagamento(prenotazione));
        dettagli.setWrapText(true);

        Label titoloPartecipanti = new Label("Partecipanti");
        titoloPartecipanti.getStyleClass().add(STILE_SOTTOTITOLO);

        VBox listaPartecipanti = new VBox(4);
        for (PartecipanteVistaBean partecipante : prenotazione.getPartecipanti()) {
            listaPartecipanti.getChildren()
                    .add(new Label(Formattatore.descriviPartecipante(partecipante, formatoData)));
        }

        VBox layout = new VBox(10, titolo, dettagli, titoloPartecipanti, listaPartecipanti);

        if (prenotazione.isModificabile()) {
            Button bottoneCambiaPacchetto = new Button("Cambia pacchetto");
            bottoneCambiaPacchetto.getStyleClass().add(STILE_BUTTON_SECONDARIO);
            bottoneCambiaPacchetto.setOnAction(evento -> mostraDialogoCambiaPacchetto(utente, prenotazione));
            layout.getChildren().add(bottoneCambiaPacchetto);

            Button bottoneAnnulla = new Button("Annulla prenotazione");
            bottoneAnnulla.getStyleClass().add(STILE_BUTTON_PERICOLO);
            bottoneAnnulla.setOnAction(evento -> {
                prenotazioneControllerGrafico.annullaPrenotazione(prenotazione.getId());
                mostraMiePrenotazioni(utente);
            });
            layout.getChildren().add(bottoneAnnulla);
        }

        Button bottoneIndietro = new Button("Torna alle prenotazioni");
        bottoneIndietro.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneIndietro.setOnAction(evento -> mostraMiePrenotazioni(utente));
        layout.getChildren().add(bottoneIndietro);

        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(30));

        impostaScena(layout);
    }

    private void mostraDialogoCambiaPacchetto(UtenteVistaBean utente, PrenotazioneVistaBean prenotazione) {
        Stage dialogo = creaDialogo("Cambia pacchetto");

        Label titolo = new Label("Scegli il nuovo pacchetto");
        titolo.getStyleClass().add(STILE_SOTTOTITOLO);

        Label esito = new Label();
        esito.setWrapText(true);

        VBox listaPacchetti = new VBox(8);
        for (PacchettoVistaBean pacchetto : catalogoControllerGrafico.catalogoCompleto()) {
            Label descrizione = new Label(pacchetto.getDestinazione() + " - "
                    + pacchetto.getPrezzoPerPersonaSettimana() + SUFFISSO_EURO_A_PERSONA);
            Button bottoneScegli = new Button("Scegli");
            bottoneScegli.setOnAction(evento -> {
                EsitoOperazione esitoModifica = prenotazioneControllerGrafico.modificaPacchetto(prenotazione.getId(),
                        pacchetto.getId());
                if (esitoModifica.isSuccesso()) {
                    dialogo.close();
                    mostraMiePrenotazioni(utente);
                } else {
                    esito.setStyle(COLORE_ERRORE);
                    esito.setText(esitoModifica.getMessaggio());
                }
            });

            HBox riga = new HBox(10, descrizione, bottoneScegli);
            riga.getStyleClass().add(STILE_RIGA_ELEMENTO);
            riga.setAlignment(Pos.CENTER_LEFT);
            descrizione.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(descrizione, Priority.ALWAYS);
            listaPacchetti.getChildren().add(riga);
        }

        ScrollPane scroll = new ScrollPane(listaPacchetti);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(240);

        Button bottoneChiudi = new Button("Annulla");
        bottoneChiudi.getStyleClass().add(STILE_BUTTON_SECONDARIO);
        bottoneChiudi.setOnAction(evento -> dialogo.close());

        VBox layout = new VBox(10, titolo, scroll, esito, bottoneChiudi);
        layout.setPadding(new Insets(25));

        impostaSceneDialogo(dialogo, layout);
    }

    public static void avvia(String[] args, GestoreUtenti gestoreUtenti,
            GestorePrenotazioni gestorePrenotazioni, GestoreRecensioni gestoreRecensioni,
            GestoreAssistenza gestoreAssistenza, GestoreListaAttesa gestoreListaAttesa) {
        gestoreUtentiCondiviso = gestoreUtenti;
        gestorePrenotazioniCondiviso = gestorePrenotazioni;
        gestoreRecensioniCondiviso = gestoreRecensioni;
        gestoreAssistenzaCondiviso = gestoreAssistenza;
        gestoreListaAttesaCondiviso = gestoreListaAttesa;
        launch(args);
    }
}

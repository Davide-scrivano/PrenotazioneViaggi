package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import exceptions.CredenzialiNonValideException;
import model.GestoreUtenti;
import model.Utente;

/**
 * Prima interfaccia dell'applicazione, in JavaFX (obbligatoria dal corso).
 * Per ora implementa la schermata di Login; le altre schermate
 * (catalogo, prenotazioni, ecc.) verranno aggiunte seguendo gli storyboard.
 */
public class GuiJavaFXApp extends Application {

    private GestoreUtenti gestoreUtenti = GestoreUtenti.getInstance();

    @Override
    public void start(Stage stage) {
        Label titolo = new Label("PrenotazioneViaggi - Login");
        titolo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField campoNickname = new TextField();
        campoNickname.setPromptText("Nickname");

        PasswordField campoPassword = new PasswordField();
        campoPassword.setPromptText("Password");

        Label messaggio = new Label();

        Button bottoneLogin = new Button("Login");
        bottoneLogin.setOnAction(evento -> {
            try {
                Utente utente = gestoreUtenti.login(campoNickname.getText(), campoPassword.getText());
                messaggio.setStyle("-fx-text-fill: green;");
                messaggio.setText("Benvenuto " + utente.getName() + "!");
            } catch (CredenzialiNonValideException e) {
                messaggio.setStyle("-fx-text-fill: red;");
                messaggio.setText(e.getMessage());
            }
        });

        VBox layout = new VBox(10, titolo, campoNickname, campoPassword, bottoneLogin, messaggio);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scena = new Scene(layout, 320, 260);
        stage.setTitle("PrenotazioneViaggi");
        stage.setScene(scena);
        stage.show();
    }

    public static void avvia(String[] args) {
        launch(args);
    }
}

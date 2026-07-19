package cli;

import java.util.Scanner;

import exceptions.CredenzialiNonValideException;
import model.Catalogo;
import model.GestoreUtenti;
import model.Pacchetto;
import model.Utente;

/**
 * Seconda interfaccia dell'applicazione (oltre a quella JavaFX),
 * come richiesto dal progetto: stessa funzionalita' (login e
 * consultazione catalogo), interazione testuale su console.
 */
public class InterfacciaCLI {

    private Scanner scanner;
    private GestoreUtenti gestoreUtenti;

    public InterfacciaCLI() {
        this.scanner = new Scanner(System.in);
        this.gestoreUtenti = GestoreUtenti.getInstance();
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
                case "0":
                    continua = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
        System.out.println("Uscita dall'applicazione.");
    }

    private void stampaMenuIniziale() {
        System.out.println();
        System.out.println("1) Login");
        System.out.println("2) Registrati");
        System.out.println("0) Esci");
        System.out.print("Scelta: ");
    }

    private void login() {
        System.out.print("Nickname: ");
        String nickname = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            Utente utente = gestoreUtenti.login(nickname, password);
            System.out.println("Login effettuato. Benvenuto " + utente.getName() + "!");
            menuUtenteLoggato(utente);
        } catch (CredenzialiNonValideException e) {
            System.out.println("Login fallito: " + e.getMessage());
        }
    }

    private void registrazione() {
        System.out.print("Nickname: ");
        String nickname = scanner.nextLine().trim();
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        gestoreUtenti.registraUtente(nickname, nome, cognome, email, password);
        System.out.println("Registrazione completata, ora puoi effettuare il login.");
    }

    private void menuUtenteLoggato(Utente utente) {
        System.out.println();
        System.out.println("--- Catalogo pacchetti disponibili ---");
        for (Pacchetto p : Catalogo.getInstance().pacchettiDisponibili()) {
            System.out.println("#" + p.getId() + " - " + p.getDestinazione() + " - " + p.getPrezzo() + " euro");
        }
        System.out.println("(funzionalita' di prenotazione da completare nella schermata dedicata)");
        gestoreUtenti.logout();
    }
}

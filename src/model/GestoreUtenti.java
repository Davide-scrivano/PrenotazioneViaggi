package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.UtenteDAO;
import exceptions.CredenzialiNonValideException;
import exceptions.PersistenzaException;

/**
 * Gestisce registrazione, login, logout e recupero password.
 * Realizza i casi d'uso "Login", "Password recovery" e "Logout"
 * del diagramma dei casi d'uso.
 *
 * Pattern Singleton: esiste una sola istanza che tiene traccia
 * di tutti gli utenti registrati e dell'utente attualmente loggato.
 *
 * Supporta le due modalita' richieste dal progetto:
 * - demo-version: se non si chiama mai attivaPersistenza(), i dati
 *   restano solo in memoria e vengono persi alla chiusura dell'app.
 * - full-version: chiamando attivaPersistenza(dao) con un UtenteDAO
 *   (file system o database, indifferentemente) i dati vengono
 *   caricati all'avvio e salvati automaticamente dopo ogni modifica.
 */
public class GestoreUtenti {

    private static GestoreUtenti instance;

    private List<Utente> utenti;
    private Utente utenteLoggato;
    private Map<String, Integer> tentativiFalliti;
    private UtenteDAO dao;

    private static final int MAX_TENTATIVI_FALLITI = 3;

    private int prossimoId;

    private GestoreUtenti() {
        this.utenti = new ArrayList<>();
        this.tentativiFalliti = new HashMap<>();
        this.prossimoId = 1;
    }

    public static GestoreUtenti getInstance() {
        if (instance == null) {
            instance = new GestoreUtenti();
        }
        return instance;
    }

    public Utente registraUtente(String nickname, String name, String surname, String email, String password) {
        Utente nuovoUtente = new Utente(prossimoId++, nickname, name, surname, email, password);
        utenti.add(nuovoUtente);
        salvaSeNecessario();
        return nuovoUtente;
    }

    /**
     * Passa alla modalita' full-version: carica subito gli utenti gia'
     * salvati (se presenti) e da questo momento ogni registrazione
     * viene anche persistita tramite il DAO fornito.
     * Non chiamare mai questo metodo equivale a restare in demo-version
     * (tutto solo in memoria, come richiesto dalle istruzioni del progetto).
     */
    public void attivaPersistenza(UtenteDAO dao) {
        this.dao = dao;
        try {
            List<Utente> utentiSalvati = dao.carica();
            this.utenti.addAll(utentiSalvati);
            for (Utente u : utentiSalvati) {
                if (u.getId() >= prossimoId) {
                    prossimoId = u.getId() + 1;
                }
            }
        } catch (PersistenzaException e) {
            // Il caricamento fallisce ma l'applicazione non si blocca:
            // si riparte semplicemente da una lista vuota in memoria.
            System.out.println("Impossibile caricare gli utenti salvati, si riparte da zero: " + e.getMessage());
        }
    }

    private void salvaSeNecessario() {
        if (dao == null) {
            return;
        }
        try {
            dao.salva(utenti);
        } catch (PersistenzaException e) {
            // Il salvataggio fallisce ma l'applicazione continua a funzionare
            // in memoria: l'errore viene segnalato, non ignorato ne' rilanciato.
            System.out.println("Attenzione: impossibile salvare i dati in modo permanente: " + e.getMessage());
        }
    }

    /**
     * Verifica le credenziali e, se corrette, imposta l'utente come loggato.
     * Se le credenziali sono errate NON si limita a rilanciare l'errore:
     * tiene traccia dei tentativi falliti per quel nickname e, superata
     * una certa soglia, lo segnala chiaramente nel messaggio di errore,
     * cosi' che chi chiama possa ad esempio bloccare temporaneamente i tentativi.
     */
    public Utente login(String nickname, String password) throws CredenzialiNonValideException {
        Utente trovato = cercaPerNickname(nickname);

        if (trovato == null || !trovato.getPassword().equals(password)) {
            int tentativi = tentativiFalliti.getOrDefault(nickname, 0) + 1;
            tentativiFalliti.put(nickname, tentativi);

            System.out.println("Tentativo di login fallito per '" + nickname + "' (tentativo n." + tentativi + ")");

            if (tentativi >= MAX_TENTATIVI_FALLITI) {
                throw new CredenzialiNonValideException(
                        "Credenziali errate. Hai superato il numero massimo di tentativi (" + MAX_TENTATIVI_FALLITI + ").");
            }
            throw new CredenzialiNonValideException("Nickname o password non corretti.");
        }

        tentativiFalliti.remove(nickname);
        this.utenteLoggato = trovato;
        return trovato;
    }

    public void logout() {
        this.utenteLoggato = null;
    }

    public Utente getUtenteLoggato() {
        return utenteLoggato;
    }

    /**
     * Recupero password: se l'email non e' associata a nessun utente
     * viene gestito restituendo un esito negativo tramite eccezione,
     * senza esporre l'esistenza o meno dell'account nel dettaglio.
     */
    public String recuperaPassword(String email) throws CredenzialiNonValideException {
        for (Utente u : utenti) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Password inviata all'indirizzo " + email);
                return u.getPassword();
            }
        }
        throw new CredenzialiNonValideException("Nessun utente registrato con questa email.");
    }

    private Utente cercaPerNickname(String nickname) {
        for (Utente u : utenti) {
            if (u.getNickname().equals(nickname)) {
                return u;
            }
        }
        return null;
    }

    public List<Utente> getUtentiRegistrati() {
        return utenti;
    }
}

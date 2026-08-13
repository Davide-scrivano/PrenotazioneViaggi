package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import bean.RegistrazioneBean;
import dao.UtenteDAO;
import exceptions.CredenzialiNonValideException;
import exceptions.PersistenzaException;
import exceptions.RegistrazioneNonConsentitaException;
import model.TipoUtente;
import model.Utente;

public class GestoreUtenti {

    private static final Logger LOGGER = Logger.getLogger(GestoreUtenti.class.getName());

    private static final int MAX_TENTATIVI_FALLITI = 3;

    private final List<Utente> utenti = new ArrayList<>();
    private final Map<String, Integer> tentativiFalliti = new HashMap<>();
    private Utente utenteLoggato;
    private UtenteDAO dao;
    private int prossimoId = 1;

    public Utente registraUtente(RegistrazioneBean dati) throws RegistrazioneNonConsentitaException {
        return registra(dati, TipoUtente.CONSUMER);
    }

    public Utente registraUtenteAgenzia(RegistrazioneBean dati) throws RegistrazioneNonConsentitaException {
        return registra(dati, TipoUtente.AGENZIA);
    }

    private Utente registra(RegistrazioneBean dati, TipoUtente tipo) throws RegistrazioneNonConsentitaException {
        if (cercaPerNickname(dati.getNickname()) != null) {
            throw new RegistrazioneNonConsentitaException(
                    "Il nickname \"" + dati.getNickname() + "\" e' gia' in uso: scegline un altro.");
        }

        Utente nuovoUtente = new Utente(prossimoId++, dati.getNickname(), dati.getNome(), dati.getCognome(),
                dati.getEmail(), dati.getPassword(), tipo);
        utenti.add(nuovoUtente);
        salvaSeNecessario();
        return nuovoUtente;
    }

    public void attivaPersistenza(UtenteDAO dao) {
        this.dao = dao;
        sincronizzaDaPersistenza();
    }

    private void sincronizzaDaPersistenza() {
        if (dao == null) {
            return;
        }
        try {
            for (Utente salvato : dao.carica()) {
                if (cercaPerNickname(salvato.getNickname()) == null) {
                    utenti.add(salvato);
                    if (salvato.getId() >= prossimoId) {
                        prossimoId = salvato.getId() + 1;
                    }
                }
            }
        } catch (PersistenzaException e) {
            LOGGER.log(Level.WARNING, "Impossibile leggere gli utenti salvati: {0}", e.getMessage());
        }
    }

    private void salvaSeNecessario() {
        if (dao == null) {
            return;
        }
        try {
            dao.salva(utenti);
        } catch (PersistenzaException e) {
            LOGGER.log(Level.WARNING, "Impossibile salvare i dati in modo permanente: {0}", e.getMessage());
        }
    }

    public Utente login(String nickname, String password) throws CredenzialiNonValideException {
        sincronizzaDaPersistenza();
        Utente trovato = cercaPerNickname(nickname);

        if (trovato == null || !trovato.getPassword().equals(password)) {
            int tentativi = tentativiFalliti.getOrDefault(nickname, 0) + 1;
            tentativiFalliti.put(nickname, tentativi);

            LOGGER.log(Level.INFO, "Tentativo di login fallito per ''{0}'' (tentativo n.{1})",
                    new Object[] { nickname, tentativi });

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

    public String recuperaPassword(String email) throws CredenzialiNonValideException {
        for (Utente u : utenti) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                LOGGER.log(Level.INFO, "Password inviata all''indirizzo {0}", email);
                return u.getPassword();
            }
        }
        throw new CredenzialiNonValideException("Nessun utente registrato con questa email.");
    }

    public Utente cercaPerNickname(String nickname) {
        if (nickname == null) {
            return null;
        }
        for (Utente u : utenti) {
            if (u.getNickname().equals(nickname)) {
                return u;
            }
        }
        return null;
    }

    public List<Utente> getUtentiRegistrati() {
        return Collections.unmodifiableList(utenti);
    }

    public Utente getUtenteById(int id) {
        for (Utente u : utenti) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }
}

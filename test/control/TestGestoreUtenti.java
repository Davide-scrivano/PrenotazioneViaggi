package control;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bean.RegistrazioneBean;
import exceptions.CredenzialiNonValideException;
import exceptions.RegistrazioneNonConsentitaException;
import model.Utente;

class TestGestoreUtenti {

    private static final String NICKNAME_MARIO = "mariorossi";

    private GestoreUtenti gestore;

    @BeforeEach
    void creaGestore() {
        gestore = new GestoreUtenti();
    }

    /**
     * La registrazione riceve un Bean e non piu' cinque stringhe sciolte:
     * e' lo stesso oggetto che il Controller Grafico riempie leggendo il
     * form, quindi il test percorre esattamente la strada del caso d'uso.
     */
    private RegistrazioneBean bean(String nickname, String nome, String cognome, String email, String password) {
        RegistrazioneBean dati = new RegistrazioneBean();
        dati.setNickname(nickname);
        dati.setNome(nome);
        dati.setCognome(cognome);
        dati.setEmail(email);
        dati.setPassword(password);
        return dati;
    }

    @Test
    void testLoginConCredenzialiCorrette() throws CredenzialiNonValideException, RegistrazioneNonConsentitaException {
        gestore.registraUtente(bean(NICKNAME_MARIO, "Mario", "Rossi", "mario@email.com", "pass123"));

        Utente loggato = gestore.login(NICKNAME_MARIO, "pass123");

        assertEquals(NICKNAME_MARIO, loggato.getNickname());
        assertEquals(loggato, gestore.getUtenteLoggato());
    }

    @Test
    void testLoginConPasswordErrataLanciaEccezione() throws RegistrazioneNonConsentitaException {
        gestore.registraUtente(bean("annaverdi", "Anna", "Verdi", "anna@email.com", "correcta"));

        assertThrows(CredenzialiNonValideException.class,
                () -> gestore.login("annaverdi", "sbagliata"));
    }

    @Test
    void testRecuperaPasswordUtenteInesistenteLanciaEccezione() {

        assertThrows(CredenzialiNonValideException.class,
                () -> gestore.recuperaPassword("nonesiste@email.com"));
    }

    /**
     * L'unicita' del nickname e' una regola di business: il Bean puo' solo
     * dire se il campo e' compilato, mentre sapere se quel nickname esiste
     * gia' richiede di conoscere tutti gli utenti registrati. Senza questo
     * controllo il secondo utente omonimo non riuscirebbe mai a fare login,
     * perche' cercaPerNickname restituisce sempre il primo.
     */
    @Test
    void testRegistrazioneConNicknameGiaInUsoLanciaEccezione() throws RegistrazioneNonConsentitaException {
        gestore.registraUtente(bean("nicknameunico", "Primo", "Utente", "primo@email.com", "pass1"));

        assertThrows(RegistrazioneNonConsentitaException.class,
                () -> gestore.registraUtente(bean("nicknameunico", "Secondo", "Utente", "secondo@email.com", "pass2")));
    }

    @Test
    void testRegistrazioneAgenziaAssegnaIlRuoloCorretto() throws RegistrazioneNonConsentitaException {

        Utente agenzia = gestore.registraUtenteAgenzia(
                bean("agenziatest", "Agenzia", "Test", "agenzia@email.com", "pass"));
        Utente cliente = gestore.registraUtente(
                bean("clientetest", "Cliente", "Test", "cliente@email.com", "pass"));

        assertTrue(agenzia.isAgenzia());
        assertFalse(cliente.isAgenzia());
    }

    /**
     * La lista degli utenti registrati e' esposta in sola lettura: chi la
     * consulta non deve poterla modificare scavalcando registraUtente, che
     * e' l'unico punto in cui passa anche il salvataggio.
     */
    @Test
    void testListaUtentiNonModificabileDallEsterno() {
        // Come in TestCatalogo: dentro la lambda resta solo la add(), cosi'
        // l'eccezione attesa non puo' arrivare da altro.
        List<Utente> vistaSolaLettura = gestore.getUtentiRegistrati();
        Utente intruso = new Utente(9999, "intruso", "N", "C", "i@email.com", "pw");

        assertThrows(UnsupportedOperationException.class, () -> vistaSolaLettura.add(intruso));
    }
}

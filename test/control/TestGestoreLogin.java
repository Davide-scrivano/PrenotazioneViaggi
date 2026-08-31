package control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bean.LoginBean;
import dao.filesystem.DAOFactoryFileSystem;
import exceptions.ApplicazioneException;
import exceptions.CredenzialiNonValideException;
import model.Utente;

class TestGestoreLogin {

    @TempDir
    Path cartellaDati;

    private GestoreLogin gestoreLogin;

    @BeforeEach
    void preparaSistema() {
        gestoreLogin = new GestoreLogin(new DAOFactoryFileSystem(cartellaDati.toString()));
    }

    private LoginBean credenziali(String nickname, String password) {
        LoginBean bean = new LoginBean();
        bean.setNickname(nickname);
        bean.setPassword(password);
        return bean;
    }

    @Test
    void credenzialiCorretteApronoLaSessione() throws ApplicazioneException {
        Utente utente = gestoreLogin.autentica(credenziali("mariorossi", "cliente123"));

        assertEquals(DatiDiProva.ID_UTENTE_MARIO, utente.getId());
        assertEquals("Mario", utente.getNome());
    }

    @Test
    void passwordSbagliataVieneRifiutata() {
        assertThrows(CredenzialiNonValideException.class,
                () -> gestoreLogin.autentica(credenziali("mariorossi", "sbagliata")));
    }

    @Test
    void nicknameSconosciutoVieneRifiutato() {
        assertThrows(CredenzialiNonValideException.class,
                () -> gestoreLogin.autentica(credenziali("nessuno", "cliente123")));
    }
}

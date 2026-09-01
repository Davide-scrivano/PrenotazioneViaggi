package controller.grafico;

import java.util.logging.Level;
import java.util.logging.Logger;

import exceptions.ApplicazioneException;
import exceptions.PacchettoNonDisponibileException;
import exceptions.PersistenzaException;

final class TraduttoreErrori {

    private static final Logger LOGGER = Logger.getLogger(TraduttoreErrori.class.getName());

    private static final String PROBLEMA_TECNICO =
            "Spiacente, si e' verificato un problema tecnico. Riprova piu' tardi.";

    private TraduttoreErrori() {
    }

    static String perUtente(ApplicazioneException errore) {
        if (errore instanceof PersistenzaException) {
            LOGGER.log(Level.SEVERE, "Errore tecnico nell'esecuzione del caso d'uso", errore);
            return PROBLEMA_TECNICO;
        }
        return errore.getMessage();
    }

    static boolean postiInsufficienti(ApplicazioneException errore) {
        return errore instanceof PacchettoNonDisponibileException pacchetto
                && pacchetto.getMotivo() == PacchettoNonDisponibileException.Motivo.POSTI_INSUFFICIENTI;
    }
}

package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import bean.AssistenzaBean;
import model.RichiestaAssistenza;

public class GestoreAssistenza {

    private static final Logger LOGGER = Logger.getLogger(GestoreAssistenza.class.getName());

    private final List<RichiestaAssistenza> richieste = new ArrayList<>();
    private int prossimoId = 1;

    public RichiestaAssistenza inviaRichiesta(AssistenzaBean dati) {
        RichiestaAssistenza richiesta = new RichiestaAssistenza(prossimoId++, dati.getNome(), dati.getEmail(),
                dati.getMessaggio());
        richieste.add(richiesta);
        LOGGER.log(Level.INFO, "Nuova richiesta di assistenza da {0} ({1}): {2}",
                new Object[] { dati.getNome(), dati.getEmail(), dati.getMessaggio() });
        return richiesta;
    }

    public List<RichiestaAssistenza> getTutteLeRichieste() {
        return Collections.unmodifiableList(richieste);
    }
}

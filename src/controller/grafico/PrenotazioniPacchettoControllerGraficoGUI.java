package controller.grafico;

import java.util.List;

import bean.PrenotazioneVistaBean;
import control.GestorePrenotazioni;

public class PrenotazioniPacchettoControllerGraficoGUI {

    private final GestorePrenotazioni gestorePrenotazioni;

    public PrenotazioniPacchettoControllerGraficoGUI(GestorePrenotazioni gestorePrenotazioni) {
        this.gestorePrenotazioni = gestorePrenotazioni;
    }

    public List<PrenotazioneVistaBean> prenotazioniDelPacchetto(int idPacchetto) {
        return CostruttoreBeanVista.daPrenotazioni(gestorePrenotazioni.getPrenotazioniPacchetto(idPacchetto), true);
    }

    public int postiVenduti(int idPacchetto) {
        int totale = 0;
        for (PrenotazioneVistaBean prenotazione : prenotazioniDelPacchetto(idPacchetto)) {
            totale += prenotazione.getNumeroPartecipanti();
        }
        return totale;
    }
}

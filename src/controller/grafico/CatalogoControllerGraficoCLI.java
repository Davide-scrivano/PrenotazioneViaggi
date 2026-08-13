package controller.grafico;

import java.util.List;

import bean.PacchettoVistaBean;
import bean.RecensioneVistaBean;
import control.Catalogo;
import control.GestoreRecensioni;
import model.Pacchetto;

public class CatalogoControllerGraficoCLI {

    private final Catalogo catalogo = Catalogo.getInstance();
    private final GestoreRecensioni gestoreRecensioni;

    public CatalogoControllerGraficoCLI(GestoreRecensioni gestoreRecensioni) {
        this.gestoreRecensioni = gestoreRecensioni;
    }

    public List<PacchettoVistaBean> catalogoCompleto() {
        return CostruttoreBeanVista.daPacchetti(catalogo.pacchettiDisponibili(), gestoreRecensioni);
    }

    public List<PacchettoVistaBean> cercaPerDestinazione(String testo) {
        return CostruttoreBeanVista.daPacchetti(catalogo.ricercaPerDestinazione(testo), gestoreRecensioni);
    }

    public PacchettoVistaBean dettaglioPacchetto(int idPacchetto) {
        Pacchetto pacchetto = catalogo.getPacchettoById(idPacchetto);
        return pacchetto == null ? null : CostruttoreBeanVista.daPacchetto(pacchetto, gestoreRecensioni);
    }

    public List<RecensioneVistaBean> recensioniDelPacchetto(int idPacchetto) {
        return CostruttoreBeanVista.daRecensioni(gestoreRecensioni.getRecensioniPacchetto(idPacchetto));
    }
}

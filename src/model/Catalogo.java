package model;

import java.util.ArrayList;
import java.util.List;

public class Catalogo {

    private static Catalogo instance;
    private List<Pacchetto> pacchetti;

    private Catalogo() {
        this.pacchetti = new ArrayList<>();
    }

    public static Catalogo getInstance() {
        if (instance == null) {
            instance = new Catalogo();
        }
        return instance;
    }

    public List<Pacchetto> pacchettiDisponibili() {
        return pacchetti;
    }

    public void aggiungiPacchetto(Pacchetto pacchetto) {
        this.pacchetti.add(pacchetto);
    }

    public List<Pacchetto> ricercaPerData(long data) {
        List<Pacchetto> risultato = new ArrayList<>();
        for (Pacchetto p : pacchetti) {
            if (p.getDataPartenza() <= data && p.getDataRientro() >= data) {
                risultato.add(p);
            }
        }
        return risultato;
    }
}
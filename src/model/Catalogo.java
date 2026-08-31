package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Catalogo {

    private final int id;
    private final String titolo;
    private final List<Pacchetto> pacchetti;

    public Catalogo(int id, String titolo, List<Pacchetto> pacchetti) {
        this.id = id;
        this.titolo = titolo;
        this.pacchetti = new ArrayList<>(pacchetti);
    }

    public List<Pacchetto> pacchettiDisponibili() {
        return Collections.unmodifiableList(pacchetti);
    }

    public Pacchetto trovaPacchetto(int idPacchetto) {
        for (Pacchetto pacchetto : pacchetti) {
            if (pacchetto.getId() == idPacchetto) {
                return pacchetto;
            }
        }
        return null;
    }

    public List<Pacchetto> cercaPerDestinazione(String testo) {
        if (testo == null || testo.isBlank()) {
            return pacchettiDisponibili();
        }

        String cercato = testo.trim().toLowerCase();
        List<Pacchetto> risultato = new ArrayList<>();
        for (Pacchetto pacchetto : pacchetti) {
            if (pacchetto.getDestinazione().toLowerCase().contains(cercato)) {
                risultato.add(pacchetto);
            }
        }
        return risultato;
    }

    public int getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }
}

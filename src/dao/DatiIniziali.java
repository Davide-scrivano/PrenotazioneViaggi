package dao;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import model.valori.DettagliOfferta;
import model.valori.DurataViaggio;
import model.Pacchetto;
import model.valori.PeriodoViaggio;
import model.valori.TipoUtente;
import model.valori.TipoVolo;
import model.Utente;

public final class DatiIniziali {


    public static final int ID_CATALOGO = 1;
    public static final String TITOLO_CATALOGO = "Catalogo viaggi PrenotazioneViaggi";

    private DatiIniziali() {
    }

    public static List<Utente> utenti() {
        List<Utente> utenti = new ArrayList<>();
        utenti.add(new Utente(1, "mariorossi", "Mario", "Rossi", "mario.rossi@prenotazioneviaggi.it",
                "cliente123", TipoUtente.CONSUMER));
        utenti.add(new Utente(2, "annaverdi", "Anna", "Verdi", "anna.verdi@prenotazioneviaggi.it",
                "cliente456", TipoUtente.CONSUMER));
        utenti.add(new Utente(3, "agenziaviaggi", "Laura", "Bianchi", "agenzia@prenotazioneviaggi.it",
                "agenzia123", TipoUtente.AGENZIA));
        return utenti;
    }

    public static List<Pacchetto> pacchetti() {
        long oggi = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        List<Pacchetto> pacchetti = new ArrayList<>();
        pacchetti.add(crea(1, "Roma", finestra(oggi, 10, 130), 350f, 10,
                new DettagliOfferta(4, TipoVolo.DIRETTO)));
        pacchetti.add(crea(2, "Parigi", finestra(oggi, 20, 170), 480f, 3,
                new DettagliOfferta(5, TipoVolo.DIRETTO)));
        pacchetti.add(crea(3, "Barcellona", finestra(oggi, 15, 150), 400f, 2,
                new DettagliOfferta(3, TipoVolo.CON_SCALO)));
        pacchetti.add(crea(4, "Londra", finestra(oggi, 30, 210), 520f, 6,
                new DettagliOfferta(4, TipoVolo.DIRETTO)));
        pacchetti.add(crea(5, "New York", finestra(oggi, 45, 240), 950f, 4,
                new DettagliOfferta(5, TipoVolo.CON_SCALO)));
        pacchetti.add(crea(6, "Pechino", finestra(oggi, 60, 260), 1100f, 2,
                new DettagliOfferta(4, TipoVolo.CON_SCALO)));
        pacchetti.add(crea(7, "Melbourne", finestra(oggi, 75, 300), 1450f, 8,
                new DettagliOfferta(4, TipoVolo.CON_SCALO)));
        pacchetti.add(crea(8, "Rio de Janeiro", finestra(oggi, 50, 270), 1200f, 5,
                new DettagliOfferta(5, TipoVolo.CON_SCALO)));
        return pacchetti;
    }

    private static PeriodoViaggio finestra(long oggi, int giorniAPartenza, int giorniAlRientro) {
        return new PeriodoViaggio(oggi + DurataViaggio.giorniInMillisecondi(giorniAPartenza),
                oggi + DurataViaggio.giorniInMillisecondi(giorniAlRientro));
    }

    private static Pacchetto crea(int id, String destinazione, PeriodoViaggio disponibilita, float prezzo,
            int posti, DettagliOfferta offerta) {
        return new Pacchetto(id, destinazione, disponibilita, prezzo, posti, offerta);
    }
}

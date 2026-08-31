package model;

import model.valori.DettagliOfferta;
import model.valori.DurataViaggio;
import model.valori.PeriodoViaggio;
import model.valori.TipoVolo;
import exceptions.PacchettoNonDisponibileException;

public class Pacchetto {

    private final int id;
    private final String destinazione;
    private final PeriodoViaggio disponibilita;
    private final float prezzoSettimanale;
    private final DettagliOfferta offerta;
    private int postiDisponibili;

    public Pacchetto(int id, String destinazione, PeriodoViaggio disponibilita, float prezzoSettimanale,
            int postiDisponibili, DettagliOfferta offerta) {
        this.id = id;
        this.destinazione = destinazione;
        this.disponibilita = disponibilita;
        this.prezzoSettimanale = prezzoSettimanale;
        this.postiDisponibili = postiDisponibili;
        this.offerta = offerta;
    }

    public void verificaPrenotabilita(int numeroPartecipanti, PeriodoViaggio periodoRichiesto)
            throws PacchettoNonDisponibileException {

        if (!haPostiPer(numeroPartecipanti)) {
            throw new PacchettoNonDisponibileException("Il pacchetto \"" + destinazione
                    + "\" non ha abbastanza posti per " + numeroPartecipanti + " partecipanti: ne restano "
                    + postiDisponibili + ".");
        }
        if (!disponibilita.contiene(periodoRichiesto)) {
            throw new PacchettoNonDisponibileException("Le date scelte sono fuori dal periodo in cui \""
                    + destinazione + "\" e' prenotabile.");
        }
    }

    public float calcolaPrezzoTotale(int numeroPartecipanti, DurataViaggio durata) {
        return prezzoSettimanale * durata.getSettimane() * numeroPartecipanti;
    }

    public void occupaPosti(int quantita) {
        postiDisponibili -= quantita;
    }

    public boolean isEsaurito() {
        return !haPostiPer(1);
    }

    private boolean haPostiPer(int postiRichiesti) {
        return postiRichiesti > 0 && postiDisponibili >= postiRichiesti;
    }

    public int getId() {
        return id;
    }

    public String getDestinazione() {
        return destinazione;
    }

    public PeriodoViaggio getDisponibilita() {
        return disponibilita;
    }

    public float getPrezzoSettimanale() {
        return prezzoSettimanale;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public int getStelleHotel() {
        return offerta.getStelleHotel();
    }

    public TipoVolo getTipoVolo() {
        return offerta.getTipoVolo();
    }

    @Override
    public boolean equals(Object altro) {
        if (this == altro) {
            return true;
        }
        if (!(altro instanceof Pacchetto)) {
            return false;
        }
        return id == ((Pacchetto) altro).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

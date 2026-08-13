package model;

public class DettagliOfferta {

    private final int stelleHotel;
    private final TipoVolo tipoVolo;

    public DettagliOfferta(int stelleHotel, TipoVolo tipoVolo) {
        this.stelleHotel = stelleHotel;
        this.tipoVolo = tipoVolo;
    }

    public int getStelleHotel() {
        return stelleHotel;
    }

    public TipoVolo getTipoVolo() {
        return tipoVolo;
    }
}

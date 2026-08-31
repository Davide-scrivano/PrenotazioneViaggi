package model.valori;

public class DettagliOfferta {

    private static final int STELLE_MINIME = 1;
    private static final int STELLE_MASSIME = 5;

    private final int stelleHotel;
    private final TipoVolo tipoVolo;

    public DettagliOfferta(int stelleHotel, TipoVolo tipoVolo) {
        this.stelleHotel = Math.max(STELLE_MINIME, Math.min(STELLE_MASSIME, stelleHotel));
        this.tipoVolo = tipoVolo != null ? tipoVolo : TipoVolo.DIRETTO;
    }

    public int getStelleHotel() {
        return stelleHotel;
    }

    public TipoVolo getTipoVolo() {
        return tipoVolo;
    }
}

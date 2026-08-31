package bean;

import java.util.ArrayList;
import java.util.List;

public class CatalogoVistaBean {

    private String titolo;
    private List<PacchettoVistaBean> pacchetti = new ArrayList<>();

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public List<PacchettoVistaBean> getPacchetti() {
        return pacchetti;
    }

    public void setPacchetti(List<PacchettoVistaBean> pacchetti) {
        this.pacchetti = pacchetti;
    }
}

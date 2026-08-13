package payment;

public class PayPalPagamento implements Pagamento {

    private final String email;
    private final String password;
    private final float importo;

    PayPalPagamento(String email, String password, float importo) {
        this.email = email;
        this.password = password;
        this.importo = importo;
    }

    @Override
    public boolean elaboraPagamento() {
        return email != null && email.contains("@")
                && password != null && !password.isEmpty();
    }

    @Override
    public float costo() {
        return importo;
    }

    @Override
    public String descrizione() {
        return "PayPal";
    }
}

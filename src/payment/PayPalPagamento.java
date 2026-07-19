package payment;

public class PayPalPagamento implements Pagamento {

    private String email;
    private String password;
    private float importo;

    public PayPalPagamento(String email, String password, float importo) {
        this.email = email;
        this.password = password;
        this.importo = importo;
    }

    @Override
    public boolean metodoPagamento() {
        // verifica base: email valida (contiene @) e password non vuota
        return email != null && email.contains("@")
                && password != null && !password.isEmpty();
    }

    @Override
    public float costo() {
        return importo;
    }
}
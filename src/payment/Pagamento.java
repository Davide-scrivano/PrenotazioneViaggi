package payment;

public interface Pagamento {

    boolean elaboraPagamento();

    float costo();

    String descrizione();
}

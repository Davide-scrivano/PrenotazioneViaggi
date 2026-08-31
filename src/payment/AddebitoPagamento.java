package payment;

interface AddebitoPagamento {

    String addebita(float importo, String riferimentoStrumento, String codiceSicurezza);
}

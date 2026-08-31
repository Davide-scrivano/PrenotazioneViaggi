package payment;

class AdapterGatewayPagamento implements AddebitoPagamento {

    private static final int CENTESIMI_PER_EURO = 100;

    private final GatewayPagamentoEsterno gateway;
    private final String riferimentoTransazione;

    AdapterGatewayPagamento(GatewayPagamentoEsterno gateway, String riferimentoTransazione) {
        this.gateway = gateway;
        this.riferimentoTransazione = riferimentoTransazione;
    }

    @Override
    public String addebita(float importo, String riferimentoStrumento, String codiceSicurezza) {
        long centesimi = Math.round(importo * (double) CENTESIMI_PER_EURO);
        String codice = gateway.richiediAutorizzazione(centesimi, riferimentoTransazione,
                riferimentoStrumento, codiceSicurezza);
        if (GatewayPagamentoEsterno.autorizzazioneNegata(codice) || !gateway.confermaAddebito(codice)) {
            return null;
        }
        return codice;
    }
}

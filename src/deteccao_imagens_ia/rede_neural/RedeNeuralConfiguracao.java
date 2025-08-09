package deteccao_imagens_ia.rede_neural;

public record RedeNeuralConfiguracao(Integer numeroEpocas, Double limiar, Double taxaAprendizadoInicial,
                                     Double taxaDecaimentoTaxaAprendizado,
                                     Double passoDecaimentoTaxaAprendizado) {
    private static final Integer NUMERO_EPOCAS = 40;
    private static final Double LIMIAR = 0.5;
    private static final Double TAXA_APRENDIZADO_INICIAL = 0.2;
    private static final Double TAXA_DECAIMENTO_TAXA_APRENDIZADO = 0.85;
    private static final Double PASSO_DECAIMENTO_TAXA_APRENDIZADO = 20.0;

    public RedeNeuralConfiguracao() {
        this(NUMERO_EPOCAS, LIMIAR, TAXA_APRENDIZADO_INICIAL, TAXA_DECAIMENTO_TAXA_APRENDIZADO, PASSO_DECAIMENTO_TAXA_APRENDIZADO);
    }

    public Double obterTaxaAprendizadoAtual(Integer epocaAtual) {
        return taxaAprendizadoInicial * Math.pow(taxaDecaimentoTaxaAprendizado, epocaAtual / passoDecaimentoTaxaAprendizado);
    }

}

package deteccao_imagens_ia.rede_neural;

public record RedeNeuralConfiguracao(int numeroEpocas, double limiar, double taxaAprendizadoInicial,
                                     double taxaDecaimentoTaxaAprendizado,
                                     double passoDecaimentoTaxaAprendizado) {
    private static final int NUMERO_EPOCAS = 40;
    private static final double LIMIAR = 0.5;
    private static final double TAXA_APRENDIZADO_INICIAL = 0.2;
    private static final double TAXA_DECAIMENTO_TAXA_APRENDIZADO = 0.5;
    private static final double PASSO_DECAIMENTO_TAXA_APRENDIZADO = 5.0;

    public RedeNeuralConfiguracao() {
        this(NUMERO_EPOCAS, LIMIAR, TAXA_APRENDIZADO_INICIAL, TAXA_DECAIMENTO_TAXA_APRENDIZADO, PASSO_DECAIMENTO_TAXA_APRENDIZADO);
    }

    public double obterTaxaAprendizadoAtual(int epocaAtual) {
        return taxaAprendizadoInicial * Math.pow(taxaDecaimentoTaxaAprendizado, epocaAtual / passoDecaimentoTaxaAprendizado);
    }

}

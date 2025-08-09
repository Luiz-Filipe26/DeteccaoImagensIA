package deteccao_imagens_ia.rede_neural;

public class EstadoTreinamento {

    private final RedeNeuralConfiguracao redeNeuralConfiguracao;
    private int epocaAtual;

    public EstadoTreinamento() {
        this.redeNeuralConfiguracao = new RedeNeuralConfiguracao();
        epocaAtual = 0;
    }

    public EstadoTreinamento(RedeNeuralConfiguracao redeNeuralConfiguracao, int epocaAtual) {
        this.redeNeuralConfiguracao = redeNeuralConfiguracao;
        this.epocaAtual = epocaAtual;
    }

    public void incrementarEpocaAtual() {
        epocaAtual++;
    }

    public RedeNeuralConfiguracao getRedeNeuralConfiguracao() {
        return redeNeuralConfiguracao;
    }

    public Integer getEpocaAtual() {
        return epocaAtual;
    }
}

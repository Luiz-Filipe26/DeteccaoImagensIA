package deteccao_imagens_ia.rede_neural;

import java.util.ArrayList;
import java.util.List;

public class RedeNeural implements Cloneable {
    private static final double LIMIAR = 0.5;
    private static final double TAXA_APRENDIZADO_PADRAO = 0.5;

    private final List<Camada> camadas = new ArrayList<>();
    private double taxaAprendizado;

    public RedeNeural() {
        this.taxaAprendizado = TAXA_APRENDIZADO_PADRAO;
    }

    @Override
    public RedeNeural clone() {
        RedeNeural clone = new RedeNeural();
        clone.taxaAprendizado = this.taxaAprendizado;
        for (Camada camada : this.camadas)
            clone.camadas.add(camada.clone());
        return clone;
    }

    public void adicionarCamada(Camada camada) {
        camadas.add(camada);
    }

    public void adicionarCamadaVazia() {
        camadas.add(new Camada());
    }

    public Camada getUltimaCamada() {
        return camadas.get(camadas.size() - 1);
    }

    public int getTamanhoEntrada() {
        return camadas.get(0).getNumeroEntradas();
    }

    public int getNumeroCamadas() {
        return camadas.size();
    }

    public Camada getCamada(int index) {
        return camadas.get(index);
    }

    public boolean ehRedeInvalida() {
        if (camadas.isEmpty()) return true;
        if (camadas.stream().anyMatch(Camada::isEmpty)) return true;
        for (int i = 0; i < camadas.size(); i++) {
            var camada = camadas.get(i);
            var camadaAnterior = i == 0 ? null : camadas.get(i - 1);
            if (!ehCamadaConsistente(camada, camadaAnterior)) return true;
        }
        return false;
    }

    private boolean ehCamadaConsistente(Camada camada, Camada camadaAnterior) {
        int entradasEsperadas = camada.getNumeroEntradas();
        for (var perceptron : camada) {
            if (perceptron.ehInvalido() || perceptron.getNumeroPesos() != entradasEsperadas) return false;
        }
        return camadaAnterior == null || entradasEsperadas == camadaAnterior.size();
    }

    public void forcarAprendizado(double[] entrada) {
        var redeNeuralClone = clone();
        redeNeuralClone.taxaAprendizado = 1.0;
        redeNeuralClone.treinar(entrada, new double[]{1.0});
        camadas.clear();
        camadas.addAll(redeNeuralClone.camadas);
    }

    public void treinar(double[] entrada, double[] esperado) {
        validarTreino(entrada, esperado);
        var historicoDeAtivacao = calcularSaidas(entrada);
        double[][] deltas = inicializarDeltas();
        calcularDeltasCamadaSaida(historicoDeAtivacao, deltas, esperado);
        calcularDeltasCamadasOcultas(historicoDeAtivacao, deltas);
        aplicarDeltas(historicoDeAtivacao, deltas, entrada);
    }

    public ResultadoClassificacao detectar(double[] entrada) {
        double[] saida = calcularSaidas(entrada).getSaidaRedeAtivada();
        if (saida.length != 1) throw new IllegalArgumentException("A saída da rede neural deve ter só 1 valor!");
        return saida[0] > LIMIAR ? ResultadoClassificacao.DESENHO_ESPERADO : ResultadoClassificacao.DESENHO_NAO_ESPERADO;
    }

    private HistoricoDeAtivacao calcularSaidas(double[] entrada) {
        var historicoDeAtivacao = new HistoricoDeAtivacao();
        double[] saidaAtual = entrada;
        for (var camada : camadas)
            saidaAtual = calcularSaidaCamada(historicoDeAtivacao, camada, saidaAtual);
        return historicoDeAtivacao;
    }

    private double[] calcularSaidaCamada(HistoricoDeAtivacao historicoDeAtivacao, Camada camada, double[] entrada) {
        List<SaidasNeuronio> saidasNeuronios = new ArrayList<>(camada.size());
        for (var perceptron : camada)
            saidasNeuronios.add(perceptron.calcularSaida(entrada));
        historicoDeAtivacao.addSaidasCamada(saidasNeuronios);
        return historicoDeAtivacao.getSaidaAtivadaUltimaCamada();
    }

    private void validarTreino(double[] entrada, double[] esperado) {
        var camadaSaida = camadas.get(camadas.size() - 1);
        if (esperado.length != camadaSaida.size())
            throw new IllegalArgumentException("Tamanho do vetor esperado não corresponde ao da camada de saída.");
        if(ehRedeInvalida())
            throw new IllegalArgumentException("A Rede Neural não é válida!");
    }

    private double[][] inicializarDeltas() {
        var deltas = new double[camadas.size()][];
        for (int camadaIndex = 0; camadaIndex < camadas.size(); camadaIndex++)
            deltas[camadaIndex] = new double[camadas.get(camadaIndex).size()];
        return deltas;
    }

    private void calcularDeltasCamadaSaida(HistoricoDeAtivacao historicoDeAtivacao, double[][] deltas, double esperado[]) {
        int camadaSaidaIndex = camadas.size() - 1;
        var saidasCamadaAtivacao = historicoDeAtivacao.saidasAntesDeAtivar().get(camadaSaidaIndex);
        for (int neuronioIndex = 0; neuronioIndex < saidasCamadaAtivacao.length; neuronioIndex++)
            deltas[camadaSaidaIndex][neuronioIndex] = calcularDelta(saidasCamadaAtivacao[neuronioIndex], esperado[neuronioIndex]);
    }

    private double calcularDelta(double saidaAntesDeAtivar, double esperado) {
        double derivada = AtivacaoSigmoide.derivada(saidaAntesDeAtivar);
        double saidaAtivada = AtivacaoSigmoide.ativar(saidaAntesDeAtivar);
        double erro = esperado - saidaAtivada;
        return erro * derivada;
    }

    private void calcularDeltasCamadasOcultas(HistoricoDeAtivacao historicoDeAtivacao, double[][] deltas) {
        for (int camadaIndex = camadas.size() - 2; camadaIndex >= 0; camadaIndex--) {
            int camadaSucessoraIndex = camadaIndex + 1;
            var saidasCamada = historicoDeAtivacao.saidasAntesDeAtivar().get(camadaIndex);
            deltas[camadaIndex] = obterDeltasCamadaOculta(saidasCamada, camadas.get(camadaSucessoraIndex), deltas[camadaSucessoraIndex]);
        }
    }

    private double[] obterDeltasCamadaOculta(double[] saidasAntesDeAtivar, Camada camadaSucessora, double[] deltasSucessora) {
        double[] deltas = new double[saidasAntesDeAtivar.length];
        for (int neuronioIndex = 0; neuronioIndex < saidasAntesDeAtivar.length; neuronioIndex++) {
            double somaErros = calcularSomaErros(neuronioIndex, camadaSucessora, deltasSucessora);
            deltas[neuronioIndex] = somaErros * AtivacaoSigmoide.derivada(saidasAntesDeAtivar[neuronioIndex]);
        }
        return deltas;
    }

    private double calcularSomaErros(int neuronioIndex, Camada camadaSucessora, double[] deltasCamadaSucessora) {
        double somaErros = 0.0;
        for (int neuronioSucessorIndex = 0; neuronioSucessorIndex < camadaSucessora.size(); neuronioSucessorIndex++) {
            double peso = camadaSucessora.get(neuronioSucessorIndex).getPeso(neuronioIndex);
            somaErros += deltasCamadaSucessora[neuronioSucessorIndex] * peso;
        }
        return somaErros;
    }

    private void aplicarDeltas(HistoricoDeAtivacao historicoDeAtivacao, double[][] deltas, double[] entrada) {
        for (int camadaIndex = 0; camadaIndex < camadas.size(); camadaIndex++) {
            var camada = camadas.get(camadaIndex);
            double[] entradas = (camadaIndex == 0) ? entrada : historicoDeAtivacao.saidasAtivadas().get(camadaIndex - 1);
            for (int neuronioIndex = 0; neuronioIndex < camada.size(); neuronioIndex++)
                camada.get(neuronioIndex).atualizarPesos(entradas, taxaAprendizado, deltas[camadaIndex][neuronioIndex]);
        }
    }
}
package deteccao_imagens_ia.rede_neural;

import deteccao_imagens_ia.populador_exemplos_desenho.ClassificacaoDesenho;
import deteccao_imagens_ia.populador_exemplos_desenho.EntradaClassificada;
import deteccao_imagens_ia.utils.PersistenciaRedeNeural;
import deteccao_imagens_ia.utils.XMLEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedeNeural {
    private final ModeloRedeNeural modelo;
    private final EstadoTreinamento estadoTreinamento;
    private final RedeNeuralConfiguracao configuracao;

    public RedeNeural(EstadoTreinamento estadoTreinamento, ModeloRedeNeural modelo) {
        this.configuracao = estadoTreinamento.getRedeNeuralConfiguracao();
        this.estadoTreinamento = estadoTreinamento;
        this.modelo = modelo;
    }

    public void aumentarEpoca() {
        estadoTreinamento.incrementarEpocaAtual();
    }

    public ModeloRedeNeural getModelo() {
        return modelo;
    }

    public void treinarEmLote(List<EntradaClassificada> exemplos, ClassificacaoDesenho desenhoEsperado) {
        System.out.println("Iniciando treinamento em lote com " + exemplos.size() + " exemplos.");
        var exemplosCopia = new ArrayList<>(exemplos);

        for (int i = 0; i < configuracao.numeroEpocas(); i++) {
            aumentarEpoca();
            Collections.shuffle(exemplosCopia);
            for (var exemplo : exemplosCopia) {
                double[] esperado = new double[] {exemplo.classificacao() == desenhoEsperado ? 1.0 : 0.0};
                treinar(exemplo.entrada(), esperado);
            }
            int epocaAtual = estadoTreinamento.getEpocaAtual();;
            if (estadoTreinamento.getEpocaAtual() % 10 == 0 || epocaAtual == configuracao.numeroEpocas()) {
                double taxaAprendizado = configuracao.obterTaxaAprendizadoAtual(epocaAtual);
                System.out.printf("Época %d/%d concluída. Taxa de aprendizado atual: %.6f\n",
                        epocaAtual, configuracao.numeroEpocas(), taxaAprendizado);
            }
        }
        try {
            PersistenciaRedeNeural.salvarEstadoRedeNeural(estadoTreinamento);
        } catch (XMLEditor.FalhaXML e) {
            System.err.println("Falha ao salvar estado da rede neural!");
        }
        System.out.println("Treinamento em lote concluído.");
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
        return saida[0] > configuracao.limiar() ? ResultadoClassificacao.DESENHO_ESPERADO : ResultadoClassificacao.DESENHO_NAO_ESPERADO;
    }

    private HistoricoDeAtivacao calcularSaidas(double[] entrada) {
        var historicoDeAtivacao = new HistoricoDeAtivacao();
        double[] saidaAtual = entrada;
        for (var camada : modelo.getIteradorDeCamadas())
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
        var camadaSaida = modelo.getCamadaSaida();
        if(entrada.length != modelo.getTamanhoEntrada())
            throw new IllegalArgumentException("Tamanho do vetor entrada não corresponde ao da camada de entrada.");
        if (esperado.length != camadaSaida.size())
            throw new IllegalArgumentException("Tamanho do vetor esperado não corresponde ao da camada de saída.");
        if(modelo.saoCamadasInconsistentes())
            throw new IllegalArgumentException("A Rede Neural não é válida!");
    }

    private double[][] inicializarDeltas() {
        var deltas = new double[modelo.getNumeroCamadas()][];
        int camadaIndex = 0;
        for (var camada : modelo.getIteradorDeCamadas()) {
            deltas[camadaIndex] = new double[camada.size()];
            camadaIndex++;
        }
        return deltas;
    }

    private void calcularDeltasCamadaSaida(HistoricoDeAtivacao historicoDeAtivacao, double[][] deltas, double[] esperado) {
        int camadaSaidaIndex = modelo.getNumeroCamadas() - 1;
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
        for (int camadaIndex = modelo.getNumeroCamadas() - 2; camadaIndex >= 0; camadaIndex--) {
            int camadaSucessoraIndex = camadaIndex + 1;
            var saidasCamada = historicoDeAtivacao.saidasAntesDeAtivar().get(camadaIndex);
            deltas[camadaIndex] = obterDeltasCamadaOculta(saidasCamada, modelo.getCamada(camadaSucessoraIndex), deltas[camadaSucessoraIndex]);
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
        double taxaAprendizado = configuracao.obterTaxaAprendizadoAtual(estadoTreinamento.getEpocaAtual());
        int camadaIndex = 0;
        for (var camada : modelo.getIteradorDeCamadas()) {
            double[] entradas = (camadaIndex == 0) ? entrada : historicoDeAtivacao.saidasAtivadas().get(camadaIndex - 1);
            for (int neuronioIndex = 0; neuronioIndex < camada.size(); neuronioIndex++)
                camada.get(neuronioIndex).atualizarPesos(entradas, taxaAprendizado, deltas[camadaIndex][neuronioIndex]);
            camadaIndex++;
        }
    }
}
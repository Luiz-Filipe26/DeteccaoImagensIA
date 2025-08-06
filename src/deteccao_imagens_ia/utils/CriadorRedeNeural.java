package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.rede_neural.Camada;
import deteccao_imagens_ia.rede_neural.Perceptron;
import deteccao_imagens_ia.rede_neural.RedeNeural;

import java.awt.*;
import java.util.List;

public class CriadorRedeNeural {
    private static final int TAMANHO_CAMADA_ENTRADA = 2500;
    private static final int TAMANHO_CAMADA_OCULTA_1 = 100;
    private static final int TAMANHO_CAMADA_OCULTA_2 = 30;
    private static final int TAMANHO_CAMADA_SAIDA = 1;

    public static RedeNeural criarRede() {
        List<String> linhas = PersistenciaRedeNeural.lerArquivoDePesos();
        if (linhas.isEmpty()) {
            System.out.println("Arquivo de pesos vazio ou inexistente.");
            return null;
        }
        return PersistenciaRedeNeural.construirRede(linhas, new RedeNeural());
    }

    public static RedeNeural criarRedePelaEntrada(AvaliadorDesenho avaliadorDesenho, List<Point> bolinhas) {
        double[] entrada = avaliadorDesenho.calcularEntrada(bolinhas, TAMANHO_CAMADA_ENTRADA);
        var redeNeural = criarRedeNeuralVazia(obterTamanhoPorCamada());
        redeNeural.forcarAprendizado(entrada);
        return redeNeural;
    }

    private static List<Integer> obterTamanhoPorCamada() {
        return List.of(TAMANHO_CAMADA_ENTRADA, TAMANHO_CAMADA_OCULTA_1, TAMANHO_CAMADA_OCULTA_2, TAMANHO_CAMADA_SAIDA);
    }

    private static RedeNeural criarRedeNeuralVazia(List<Integer> tamanhosPorCamada) {
        var redeNeural = new RedeNeural();
        for (int tamanhoIndex = 1; tamanhoIndex < tamanhosPorCamada.size(); tamanhoIndex++) {
            int entradasPorNeuronio = tamanhosPorCamada.get(tamanhoIndex - 1);
            int quantidadeNeuronios = tamanhosPorCamada.get(tamanhoIndex);
            criarCamadaVazia(redeNeural, quantidadeNeuronios, entradasPorNeuronio);
        }
        return redeNeural;
    }

    private static void criarCamadaVazia(RedeNeural redeNeural, int quantidadeNeuronios, int entradasPorNeuronio) {
        redeNeural.adicionarCamada(new Camada());
        for (int neuronioIndex = 0; neuronioIndex < quantidadeNeuronios; neuronioIndex++) {
            var perceptron = new Perceptron(entradasPorNeuronio);
            redeNeural.getUltimaCamada().add(perceptron);
        }
    }
}

package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.rede_neural.*;

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
            System.out.println("Arquivo de pesos vazio ou inexistente. Criado rede neural vazia.");
            return criarRedeNeuralVazia();
        }
        return PersistenciaRedeNeural.construirRede(linhas, new ModeloRedeNeural());
    }

    private static RedeNeural criarRedeNeuralVazia() {
        var modelo = new ModeloRedeNeural(obterTamanhoPorCamada());
        var estadoTreinamento = PersistenciaRedeNeural.carregarEstadoTreinamento();
        return new RedeNeural(estadoTreinamento, modelo);
    }

    private static List<Integer> obterTamanhoPorCamada() {
        return List.of(TAMANHO_CAMADA_ENTRADA, TAMANHO_CAMADA_OCULTA_1, TAMANHO_CAMADA_OCULTA_2, TAMANHO_CAMADA_SAIDA);
    }
}

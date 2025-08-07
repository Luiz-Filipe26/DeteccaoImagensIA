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
        var redeNeuralVazia = criarRedeNeuralVazia();
        if (linhas.isEmpty()) {
            System.out.println("Arquivo de pesos vazio ou inexistente. Criado rede neural vazia.");
            return redeNeuralVazia;
        }
        return PersistenciaRedeNeural.construirRede(linhas, redeNeuralVazia.getModelo());
    }

    private static RedeNeural criarRedeNeuralVazia() {
        var modelo = new ModeloRedeNeural(obterTamanhoPorCamada());
        return new RedeNeural(new RedeNeuralConfiguracao(), modelo);
    }

    private static List<Integer> obterTamanhoPorCamada() {
        return List.of(TAMANHO_CAMADA_ENTRADA, TAMANHO_CAMADA_OCULTA_1, TAMANHO_CAMADA_OCULTA_2, TAMANHO_CAMADA_SAIDA);
    }
}

package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.populador_exemplos_desenho.BaseTreinamento;
import deteccao_imagens_ia.populador_exemplos_desenho.ClassificacaoDesenho;
import deteccao_imagens_ia.populador_exemplos_desenho.DesenhoClassificado;
import deteccao_imagens_ia.populador_exemplos_desenho.EntradaClassificada;
import deteccao_imagens_ia.rede_neural.ModeloRedeNeural;
import deteccao_imagens_ia.rede_neural.RedeNeural;
import deteccao_imagens_ia.rede_neural.ResultadoClassificacao;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AvaliadorDesenho {

    public static final int MAX_BOLINHAS_POR_CELULA = 10;
    private RedeNeural redeNeural;

    public ResultadoClassificacao analisarDesenho(List<Point> bolinhas) {
        if(redeNeural == null) criarRedeNeuralValida();
        var desenhoClassificao = new DesenhoClassificado(ClassificacaoDesenho.BONECO_DE_PALITO, bolinhas);
        int tamanhoEntrada = redeNeural.getModelo().getTamanhoEntrada();
        var entradaClassificada = EntradaClassificada.deDesenho(desenhoClassificao, tamanhoEntrada, MAX_BOLINHAS_POR_CELULA);
        return redeNeural.detectar(entradaClassificada.entrada());
    }

    public void treinarRede(BaseTreinamento baseTreinamento) {
        int tamanhoEntrada = redeNeural.getModelo().getTamanhoEntrada();
        var entradas = baseTreinamento.getEntradasClassificadas(tamanhoEntrada, MAX_BOLINHAS_POR_CELULA);
        redeNeural.setEpocaAtual(0); // carregar de checkpoint
        redeNeural.treinarEmLote(entradas, ClassificacaoDesenho.BONECO_DE_PALITO);
        salvarRede(redeNeural.getModelo());
    }

    private void salvarRede(ModeloRedeNeural modelo) {
        try {
            PersistenciaRedeNeural.salvarRede(modelo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar pesos: " + e.getMessage());
        }
    }

    private void criarRedeNeuralValida() {
        redeNeural = CriadorRedeNeural.criarRede();
        if (redeNeural == null) throw new IllegalStateException("Não foi possível ler arquivo de pesos!");
        if (redeNeural.getModelo().saoCamadasInconsistentes()) throw new IllegalStateException("A Rede Neural não é válida!");
    }
}
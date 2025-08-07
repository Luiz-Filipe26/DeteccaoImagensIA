package deteccao_imagens_ia.utils;

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

    public ResultadoClassificacao analisarDesenho(List<Point> bolinhas, boolean treinarModelo) {
        var redeNeural = criarRedeNeuralValida();
        var desenhoClassificao = new DesenhoClassificado(ClassificacaoDesenho.BONECO_DE_PALITO, bolinhas);
        var entradaClassificada = EntradaClassificada.deDesenhoClassificado(desenhoClassificao, redeNeural.getModelo().getTamanhoEntrada());
        if (treinarModelo) {
            redeNeural.treinar(entradaClassificada.entrada(), new double[]{1.0});
            salvarRede(redeNeural.getModelo());
        }
        return redeNeural.detectar(entradaClassificada.entrada());
    }

    private void salvarRede(ModeloRedeNeural modelo) {
        try {
            PersistenciaRedeNeural.salvarRede(modelo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar pesos: " + e.getMessage());
        }
    }

    private RedeNeural criarRedeNeuralValida() {
        var redeNeural = CriadorRedeNeural.criarRede();
        if (redeNeural == null) throw new IllegalStateException("Não foi possível ler arquivo de pesos!");
        if (redeNeural.getModelo().saoCamadasInconsistentes()) throw new IllegalStateException("A Rede Neural não é válida!");
        return redeNeural;
    }
}
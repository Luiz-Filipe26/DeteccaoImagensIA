package deteccao_imagens_ia.populador_exemplos_desenho;

import deteccao_imagens_ia.utils.XMLEditor;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseTreinamento {
    public static final String RAIZ_TAG = "exemplos";
    public static final String DESENHO_TAG = "desenho";
    public static final String HASH_ATR = "hash";
    public static final String ROTULO_ATR = "rotulo";
    public static final String PONTO_TAG = "pt";
    public static final String PONTO_X_ATR = "x";
    public static final String PONTO_Y_ATR = "y";
    private final List<DesenhoClassificado> desenhosClassificados = new ArrayList<>();
    private boolean exemplosCarregados;


    public void adicionarDesenho(DesenhoClassificado desenho) {
        if (!desenhosClassificados.contains(desenho))
            desenhosClassificados.add(desenho);
    }

    public List<DesenhoClassificado> getDesenhosClassificados() {
        return new ArrayList<>(desenhosClassificados);
    }

    public List<EntradaClassificada> getEntradasClassificadas(int quantidadeEntradas, int maxBolinhasPorCelula) {
        return desenhosClassificados.stream()
                .map(desenho -> EntradaClassificada.deDesenho(desenho, quantidadeEntradas, maxBolinhasPorCelula))
                .toList();
    }

    public void salvarExemplos(File arquivo) throws XMLEditor.FalhaXML {
        var editor = XMLEditor.comNovoDocumento();
        var raiz = editor.criarElementoRaiz(RAIZ_TAG);
        raiz.comFilhosDeColecao(DESENHO_TAG, desenhosClassificados, (desenhoElem, desenho) -> desenhoElem
                .comAtributo(HASH_ATR, Integer.toHexString(desenho.hashCode()))
                .comAtributo(ROTULO_ATR, desenho.classificacaoDesenho().paraTexto())
                .comFilhosDeColecao(PONTO_TAG, desenho.pontosDesenho(), (pontoElem, ponto) -> pontoElem
                        .comAtributo(PONTO_X_ATR, String.valueOf(ponto.x))
                        .comAtributo(PONTO_Y_ATR, String.valueOf(ponto.y)))
        );
        editor.salvar(arquivo);
    }

    public boolean isExemplosCarregados() {
        return exemplosCarregados;
    }

    public void carregarExemplos(File arquivo) throws XMLEditor.FalhaXML {
        var editor = XMLEditor.deArquivo(arquivo);
        desenhosClassificados.clear();
        desenhosClassificados.addAll(
                editor.obterElementoRaiz().mapearFilhosParaLista(
                        DESENHO_TAG, this::lerDesenhoClassificado
                ));
        exemplosCarregados = true;
    }

    private DesenhoClassificado lerDesenhoClassificado(XMLEditor.ElementoEncadeavel desenhoElem) {
        String rotulo = desenhoElem.obterAtributo(ROTULO_ATR);
        var classificacao = ClassificacaoDesenho.deTexto(rotulo);
        var pontos = desenhoElem.mapearFilhosParaLista(PONTO_TAG, pontoElem -> {
            int x = Integer.parseInt(pontoElem.obterAtributo(PONTO_X_ATR));
            int y = Integer.parseInt(pontoElem.obterAtributo(PONTO_Y_ATR));
            return new Point(x, y);
        });
        return new DesenhoClassificado(classificacao, pontos);
    }
}

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
        var editor = XMLEditor.deArquivo(arquivo);
        var raiz = editor.criarElementoRaiz(RAIZ_TAG);
        for (var desenhoClassificado : desenhosClassificados)
            montarExemplo(raiz, desenhoClassificado);
        editor.salvar(arquivo);
    }

    private void montarExemplo(XMLEditor.ElementoEncadeavel raiz, DesenhoClassificado desenho) {
        raiz.comFilho(DESENHO_TAG, desenhoElem -> desenhoElem
                .comAtributo(HASH_ATR, desenho.gerarHash())
                .comAtributo(ROTULO_ATR, desenho.classificacaoDesenho().paraTexto())
                .comFilhosDeColecao(PONTO_TAG, desenho.pontosDesenho(), (pontoElem, ponto) ->
                        pontoElem
                        .comAtributo(PONTO_X_ATR, String.valueOf(ponto.x))
                        .comAtributo(PONTO_Y_ATR, String.valueOf(ponto.y))));
    }

    public boolean isExemplosCarregados() {
        return exemplosCarregados;
    }

    public void carregarExemplos(File arquivo) throws XMLEditor.FalhaXML {
        var editor = XMLEditor.deArquivo(arquivo);
        desenhosClassificados.clear();
        desenhosClassificados.addAll(
                editor.obterElementoRaiz().obterFilhosStream(DESENHO_TAG).map(
                        this::lerDesenhoClassificado
                ).toList());
        exemplosCarregados = true;
    }

    private DesenhoClassificado lerDesenhoClassificado(XMLEditor.ElementoEncadeavel desenhoElem) {
        String rotulo = desenhoElem.getElemento().getAttribute(ROTULO_ATR);
        var classificacao = ClassificacaoDesenho.deTexto(rotulo);
        List<Point> pontos = desenhoElem.obterFilhosStream(PONTO_TAG).map(pontoElem -> {
            int x = Integer.parseInt(pontoElem.obterAtributo(PONTO_X_ATR));
            int y = Integer.parseInt(pontoElem.obterAtributo(PONTO_Y_ATR));
            return new Point(x, y);
        }).toList();
        return new DesenhoClassificado(classificacao, pontos);
    }
}

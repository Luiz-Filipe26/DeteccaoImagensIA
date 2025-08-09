package deteccao_imagens_ia.populador_exemplos_desenho;

import org.w3c.dom.Element;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseTreinamento {
    public static final String RAIZ_ATTR = "exemplos";
    public static final String DESENHO_ATTR = "desenho";
    public static final String HASH_ATTR = "hash";
    public static final String ROTULO_ATTR = "rotulo";
    public static final String PONTO_ATTR = "pt";
    public static final String PONTO_X_ATTR = "x";
    public static final String PONTO_Y_ATTR = "y";
    private final List<DesenhoClassificado> desenhosClassificados = new ArrayList<>();
    private boolean exemplosCarregados;


    public void adicionarDesenho(DesenhoClassificado desenho) {
        desenhosClassificados.add(desenho);
    }

    public List<DesenhoClassificado> getDesenhosClassificados() {
        return  new ArrayList<>(desenhosClassificados);
    }

    public List<DesenhoClassificado> getEntradasClassificadas() {
        return new ArrayList<>(desenhosClassificados);
    }

    public void salvarExemplos(File arquivo) throws XMLEditor.FalhaXML {
        var editor = XMLEditor.deArquivo(arquivo);
        var root = editor.criarAdicionandoElementoRaiz(RAIZ_ATTR);
        for (var desenhoClassificado : desenhosClassificados)
            montarExemplo(editor, root, desenhoClassificado);
        editor.salvar(arquivo);
    }

    private void montarExemplo(XMLEditor editor, Element root, DesenhoClassificado desenhoClassificado) {
        var desenhoElem = editor.criarAdicionandoElemento(root, DESENHO_ATTR);
        desenhoElem.setAttribute(HASH_ATTR, desenhoClassificado.gerarHash());
        desenhoElem.setAttribute(ROTULO_ATTR, desenhoClassificado.classificacaoDesenho().paraTexto());

        for (var ponto : desenhoClassificado.pontosDesenho()) {
            var pontoElem = editor.criarAdicionandoElemento(desenhoElem, PONTO_ATTR);
            pontoElem.setAttribute(PONTO_X_ATTR, String.valueOf(ponto.x));
            pontoElem.setAttribute(PONTO_Y_ATTR, String.valueOf(ponto.y));
        }
    }

    public boolean isExemplosCarregados() {
        return exemplosCarregados;
    }

    public void carregarExemplos(File arquivo) throws XMLEditor.FalhaXML {
        var editor = XMLEditor.deArquivo(arquivo);
        desenhosClassificados.clear();
        for (var desenhoElem : editor.obterFilhosIteraveis(DESENHO_ATTR))
            desenhosClassificados.add(lerDesenhoClassificado(editor, desenhoElem));
        exemplosCarregados = true;
    }

    private DesenhoClassificado lerDesenhoClassificado(XMLEditor editor, Element desenhoElem) {
        String rotulo = desenhoElem.getAttribute(ROTULO_ATTR);
        var classificacao = ClassificacaoDesenho.deTexto(rotulo);
        var pontos = new ArrayList<Point>();
        for (var pontoElem : editor.obterFilhosIteraveis(desenhoElem, PONTO_ATTR)) {
            int x = Integer.parseInt(pontoElem.getAttribute(PONTO_X_ATTR));
            int y = Integer.parseInt(pontoElem.getAttribute(PONTO_Y_ATTR));
            pontos.add(new Point(x, y));
        }
        return new DesenhoClassificado(classificacao, pontos);
    }
}

package deteccao_imagens_ia.populador_exemplos_desenho;

import org.w3c.dom.Element;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseTreinamento {
    private final List<DesenhoClassificado> desenhosClassificados = new ArrayList<>();

    public List<DesenhoClassificado> getDesenhosClassificados() {
        return desenhosClassificados;
    }

    public void adicionarDesenho(DesenhoClassificado desenho) {
        desenhosClassificados.add(desenho);
    }

    public void salvarExemplos(File arquivo) {
        var editor = new XMLEditor();
        if (editor.estaSemDocumento()) throw new IllegalStateException("Erro ao criar documento XML");
        var root = editor.criarAdicionandoElementoRaiz("exemplos");
        for (var desenhoClassificado : desenhosClassificados)
            montarExemplo(editor, root, desenhoClassificado);
        if (!editor.salvar(arquivo)) throw new IllegalStateException("Erro ao salvar XML");
    }

    private void montarExemplo(XMLEditor editor, Element root, DesenhoClassificado desenhoClassificado) {
        var desenhoElem = editor.criarAdicionandoElemento(root, "desenho");
        desenhoElem.setAttribute("hash", desenhoClassificado.gerarHash());
        desenhoElem.setAttribute("rotulo", desenhoClassificado.classificacaoDesenho().paraTexto());

        for (var ponto : desenhoClassificado.pontosDesenho()) {
            var pontoElem = editor.criarAdicionandoElemento(desenhoElem, "ponto");
            pontoElem.setAttribute("x", String.valueOf(ponto.x));
            pontoElem.setAttribute("y", String.valueOf(ponto.y));
        }
    }

    public void carregarExemplos(File arquivo) {
        var editor = new XMLEditor(arquivo);
        if (editor.estaSemDocumento()) throw new IllegalStateException("Erro ao ler documento XML");
        desenhosClassificados.clear();
        for (var desenhoElem : editor.obterFilhosIteraveis("desenho"))
            desenhosClassificados.add(lerDesenhoClassificado(editor, desenhoElem));
    }

    private DesenhoClassificado lerDesenhoClassificado(XMLEditor editor, Element desenhoElem) {
        String rotulo = desenhoElem.getAttribute("rotulo");
        var classificacao = ClassificacaoDesenho.deTexto(rotulo);
        var pontos = new ArrayList<Point>();
        for (var pontoElem : editor.obterFilhosIteraveis(desenhoElem, "ponto")) {
            int x = Integer.parseInt(pontoElem.getAttribute("x"));
            int y = Integer.parseInt(pontoElem.getAttribute("y"));
            pontos.add(new Point(x, y));
        }
        return new DesenhoClassificado(classificacao, pontos);
    }

    public List<DesenhoClassificado> obterDesenhosClassificados() {
        return new ArrayList<>(desenhosClassificados);
    }

    public List<DesenhoClassificado> obterEntradasClassificadas() {
        return new ArrayList<>(desenhosClassificados);
    }
}

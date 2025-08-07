package deteccao_imagens_ia.populador_exemplos_desenho;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;

public class XMLEditor {

    private final Document documento;

    public XMLEditor() {
        documento = criarDocumentoVazio();
    }

    public XMLEditor(File arquivo) {
        documento = carregarDocumento(arquivo);
    }

    public boolean estaSemDocumento() {
        return documento == null;
    }

    private Document criarDocumentoVazio() {
        var factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return null;
        }
        return builder.newDocument();
    }

    private Document carregarDocumento(File arquivo) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(arquivo);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean salvar(File arquivo) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(documento), new StreamResult(arquivo));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Iterable<Element> obterFilhosIteraveis(String nomeElemento) {
        return obterElementoIteravel(documento.getElementsByTagName(nomeElemento));
    }

    public Iterable<Element> obterFilhosIteraveis(Element pai, String nomeElemento) {
        return obterElementoIteravel(pai.getElementsByTagName(nomeElemento));
    }

    public Iterable<Element> obterElementoIteravel(NodeList nodeList){
        var nodes = new ArrayList<Element>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add((Element) nodeList.item(i));
        }
        return nodes;
    }

    public Element criarAdicionandoElementoRaiz(String nomeElemento) {
        var elemento = documento.createElement(nomeElemento);
        documento.appendChild(elemento);
        return elemento;
    }

    public Element criarAdicionandoElemento(Element pai, String elementoName) {
        var elemento = documento.createElement(elementoName);
        pai.appendChild(elemento);
        return elemento;
    }
}

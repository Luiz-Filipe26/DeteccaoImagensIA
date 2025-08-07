package deteccao_imagens_ia.populador_exemplos_desenho;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

public class XMLEditor {
    public static class FalhaXML extends Exception {
        public FalhaXML(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final Document documento;

    public static XMLEditor comNovoDocumento() throws FalhaXML {
        return new XMLEditor(criarDocumentoVazio());
    }

    public static XMLEditor deArquivo(File arquivo) throws FalhaXML {
        return new XMLEditor(carregarDocumento(arquivo));
    }

    private XMLEditor(Document documento) {
        this.documento = documento;
    }

    // Métodos privados agora propagam a exceção
    private static Document criarDocumentoVazio() throws FalhaXML {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new FalhaXML("Falha ao criar um novo documento XML em branco.", e);
        }
    }

    private static Document carregarDocumento(File arquivo) throws FalhaXML {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(arquivo);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new FalhaXML("Falha ao carregar ou processar o arquivo: " + arquivo.getName(), e);
        }
    }

    public void salvar(File arquivo) throws FalhaXML {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(documento), new StreamResult(arquivo));
        } catch (TransformerException e ) {
            throw new FalhaXML("Falha ao salvar o arquivo: " + arquivo.getName(), e);
        }
    }

    public Iterable<Element> obterFilhosIteraveis(String nomeElemento) {
        return obterElementoIteravel(documento.getElementsByTagName(nomeElemento));
    }

    public Iterable<Element> obterFilhosIteraveis(Element pai, String nomeElemento) {
        return obterElementoIteravel(pai.getElementsByTagName(nomeElemento));
    }

    public Iterable<Element> obterElementoIteravel(NodeList nodeList) {
        return () -> IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .map(node -> (Element) node)
                .iterator();
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
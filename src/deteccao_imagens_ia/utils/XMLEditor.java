package deteccao_imagens_ia.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class XMLEditor {
    private static final int TAMANHO_IDENTACAO_PADRAO = 2;

    public static class FalhaXML extends Exception {
        public FalhaXML(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ElementoEncadeavel {
        private final Element elementoAtual;
        private final Document documento;

        ElementoEncadeavel(Document doc, Element elemento) {
            this.documento = doc;
            this.elementoAtual = elemento;
        }

        public Element getElemento() {
            return this.elementoAtual;
        }

        public ElementoEncadeavel comAtributo(String nome, String valor) {
            elementoAtual.setAttribute(nome, valor);
            return this;
        }

        public String obterAtributo(String nome) {
            return elementoAtual.getAttribute(nome);
        }

        public String obterValor() {
            return elementoAtual.getTextContent();
        }

        public ElementoEncadeavel comFilho(String nome, Consumer<ElementoEncadeavel> configurador) {
            var elementoFilho = documento.createElement(nome);
            elementoAtual.appendChild(elementoFilho);
            var construtorFilho = new ElementoEncadeavel(documento, elementoFilho);
            configurador.accept(construtorFilho);
            return this;
        }

        public ElementoEncadeavel comFilho(String nome, String valor) {
            return comFilho(nome, filho -> filho.elementoAtual.setTextContent(valor));
        }

        public ElementoEncadeavel comFilho(String nome) {
            return comFilho(nome, configurador -> {});
        }

        public Optional<ElementoEncadeavel> obterFilho(String nomeElemento) {
            return this.obterFilhosStream(nomeElemento).findFirst();
        }

        public Stream<ElementoEncadeavel> obterFilhosStream(String nomeElemento) {
            return StreamSupport.stream(obterFilhos(nomeElemento).spliterator(), false);
        }

        public Iterable<ElementoEncadeavel> obterFilhos(String nomeElemento) {
            var filhos = elementoAtual.getChildNodes();
            return () -> IntStream.range(0, filhos.getLength())
                    .mapToObj(filhos::item)
                    .filter(node -> node instanceof Element)
                    .map(node -> (Element) node)
                    .filter(element -> element.getTagName().equals(nomeElemento))
                    .map(element -> new ElementoEncadeavel(documento, element))
                    .iterator();
        }

        public <T> ElementoEncadeavel comFilhosDeColecao(String nomeElemento, Iterable<T> colecao, BiConsumer<ElementoEncadeavel, T> criadorElemento) {
            for (var item : colecao) {
                var elementoFilho = documento.createElement(nomeElemento);
                elementoAtual.appendChild(elementoFilho);
                var construtorFilho = new ElementoEncadeavel(documento, elementoFilho);
                criadorElemento.accept(construtorFilho, item);
            }
            return this;
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

    private static Document criarDocumentoVazio() throws FalhaXML {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new FalhaXML("Falha ao criar um novo documento XML em branco.", e);
        }
    }

    private static Document carregarDocumento(File arquivo) throws FalhaXML {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var document = builder.parse(arquivo);
            document.getDocumentElement().normalize();
            return document;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new FalhaXML("Falha ao carregar ou processar o arquivo: " + arquivo.getName(), e);
        }
    }

    public void salvarSemIdentar(File arquivo) throws FalhaXML {
        Consumer<Transformer> transformador = transformer -> transformer.setOutputProperty(OutputKeys.INDENT, "no");
        salvar(arquivo, transformador);
    }

    public void salvar(File arquivo) throws FalhaXML {
        salvar(arquivo, TAMANHO_IDENTACAO_PADRAO);
    }

    public void salvar(File arquivo, int tamanhoIdentacao) throws FalhaXML {
        Consumer<Transformer> transformador = transformer -> {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(tamanhoIdentacao));
        };
        salvar(arquivo, transformador);
    }

    private void salvar(File arquivo, Consumer<Transformer> transformador) throws FalhaXML {
        try {
            var transformer = TransformerFactory.newInstance().newTransformer();
            transformador.accept(transformer);
            transformer.transform(new DOMSource(documento), new StreamResult(arquivo));
        } catch (TransformerException e) {
            throw new FalhaXML("Falha ao salvar o arquivo: " + arquivo.getName(), e);
        }
    }

    public ElementoEncadeavel criarElementoRaiz(String nomeElemento) {
        var elemento = documento.createElement(nomeElemento);
        documento.appendChild(elemento);
        return new ElementoEncadeavel(this.documento, elemento);
    }

    public ElementoEncadeavel obterElementoRaiz() {
        var elemento = documento.getDocumentElement();
        return new ElementoEncadeavel(this.documento, elemento);
    }
}
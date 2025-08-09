package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.rede_neural.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PersistenciaRedeNeural {

    private static final String PASTA_ARQUIVO = "IA-Desenho";
    private static final String NOME_ARQUIVO_PESOS = "pesos_rede.txt";
    private static final Path CAMINHO_ARQUIVO_PESOS = resolverCaminho(PASTA_ARQUIVO, NOME_ARQUIVO_PESOS);
    private static final String NOME_ARQUIVO_CONFIG = "estado_rede.xml";
    private static final Path CAMINHO_ARQUIVO_CONFIG = resolverCaminho(PASTA_ARQUIVO, NOME_ARQUIVO_CONFIG);
    private static final int PRECISAO_DECIMAL = 6;
    private static final FormatadorDecimal formatadorDecimal = new FormatadorDecimal(PRECISAO_DECIMAL);

    private static final String PESOS_PREFIXO = "pesos:";
    private static final String VIES_PREFIXO = "viés:";
    private static final String COMENTARIO_PREFIXO = "#";
    private static final String SEPARADOR_CAMADA_PREFIXO = "---";
    private static final String SEPARADOR_PESO_OU_VIES = " ";

    private static final String DECIMAL_REGEX = "-?\\d+(\\.\\d+)?";
    private static final String SEPARADOR_CAMADA_REGEX = SEPARADOR_CAMADA_PREFIXO + ".+";
    private static final String LINHA_IGNORAVEL_REGEX = "(\\s+)|(" + COMENTARIO_PREFIXO + ".+)";
    private static final String PESOS_REGEX = PESOS_PREFIXO + "\\s*" + DECIMAL_REGEX + "[-\\d.\\s]*";
    private static final String VIES_REGEX = VIES_PREFIXO + "\\s*" + DECIMAL_REGEX;


    public static List<String> lerArquivoDePesos() {
        try {
            var linhas = Files.readAllLines(CAMINHO_ARQUIVO_PESOS);
            return linhas.stream().map(String::trim).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    private static Path resolverCaminho(String pastaArquivo, String nomeArquivo) {
        String appData = System.getenv("APPDATA");
        String pastaRaiz = (appData != null && !appData.isBlank()) ? appData : System.getProperty("user.home");
        var pasta = Path.of(pastaRaiz, pastaArquivo);
        try {
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar diretório para salvar pesos: " + pasta, e);
        }
        return pasta.resolve(nomeArquivo);
    }

    public static EstadoTreinamento carregarEstadoTreinamento() {
        File arquivoConfig = CAMINHO_ARQUIVO_CONFIG.toFile();

        if (!arquivoConfig.exists()) {
            System.out.println("Arquivo de configuração não encontrado. Usando configuração padrão.");
            return new EstadoTreinamento();
        }

        try {
            var editor = XMLEditor.deArquivo(arquivoConfig);
            var raiz = editor.obterElementoRaiz();

            int epocaAtual = obterValorIntObrigatorio(raiz, "epocaAtual");
            int numeroEpocas = obterValorIntObrigatorio(raiz, "numeroEpocas");
            double limiar = obterValorDoubleObrigatorio(raiz, "limiar");
            double taxaAprendizadoInicial = obterValorDoubleObrigatorio(raiz, "taxaAprendizadoInicial");
            double taxaDecaimento = obterValorDoubleObrigatorio(raiz, "taxaDecaimentoTaxaAprendizado");
            double passoDecaimento = obterValorDoubleObrigatorio(raiz, "passoDecaimentoTaxaAprendizado");

            System.out.println("Configuração da rede carregada do arquivo com sucesso.");
            var config = new RedeNeuralConfiguracao(numeroEpocas, limiar, taxaAprendizadoInicial, taxaDecaimento, passoDecaimento);
            return new EstadoTreinamento(config, epocaAtual);

        } catch (Exception e) {
            System.err.println("Erro ao carregar ou processar o arquivo de configuração: " + e.getMessage());
            System.err.println("Usando configuração padrão da rede neural.");
            return new EstadoTreinamento();
        }
    }

    private static int obterValorIntObrigatorio(XMLEditor.ElementoEncadeavel pai, String nomeTag) {
        return Integer.parseInt(obterValorObrigatorio(pai, nomeTag));
    }

    private static double obterValorDoubleObrigatorio(XMLEditor.ElementoEncadeavel pai, String nomeTag) {
        return Double.parseDouble(obterValorObrigatorio(pai, nomeTag));
    }

    private static String obterValorObrigatorio(XMLEditor.ElementoEncadeavel pai, String nomeTag) {
        String mensagemErro = "Tag obrigatória de configuração não encontrada: <" + nomeTag + ">";
        return pai.obterFilho(nomeTag)
                .orElseThrow(() -> new NoSuchElementException(mensagemErro))
                .obterValor();
    }

    public static void salvarEstadoRedeNeural(EstadoTreinamento estadoTreinamento) throws XMLEditor.FalhaXML {
        var config = estadoTreinamento.getRedeNeuralConfiguracao();
        File arquivoConfig = CAMINHO_ARQUIVO_CONFIG.toFile();
        XMLEditor editor = XMLEditor.comNovoDocumento();
        editor.criarElementoRaiz("configuracao")
            .comFilho("epocaAtual", estadoTreinamento.getEpocaAtual().toString())
            .comFilho("numeroEpocas", config.numeroEpocas().toString())
            .comFilho("limiar", config.limiar().toString())
            .comFilho("taxaAprendizadoInicial", config.taxaAprendizadoInicial().toString())
            .comFilho("taxaDecaimentoTaxaAprendizado", config.taxaDecaimentoTaxaAprendizado().toString())
            .comFilho("passoDecaimentoTaxaAprendizado", config.passoDecaimentoTaxaAprendizado().toString());

        editor.salvar(arquivoConfig);
        System.out.println("Arquivo de configuração salvo em: " + arquivoConfig.getAbsolutePath());
    }

    public static RedeNeural construirRede(List<String> linhas, ModeloRedeNeural modeloAPopular) {
        var perceptronAtual = new Perceptron();
        modeloAPopular.adicionarCamada(new Camada());
        for (var linha : linhas) {
            processarLinha(linha, modeloAPopular, perceptronAtual);
            perceptronAtual = obterPerceptronAtual(modeloAPopular, perceptronAtual);
        }
        return new RedeNeural(new EstadoTreinamento(), modeloAPopular);
    }

    private static void processarLinha(String linha, ModeloRedeNeural modelo, Perceptron perceptronAtual) {
        if (linha.matches(LINHA_IGNORAVEL_REGEX)) return;
        if (linha.matches(SEPARADOR_CAMADA_REGEX)) modelo.adicionarCamada(new Camada());
        else if (linha.matches(PESOS_REGEX)) perceptronAtual.setPesos(extrairPesos(linha));
        else if (linha.matches(VIES_REGEX)) perceptronAtual.setVies(extrairVies(linha));
        else throw new IllegalArgumentException("Linha não reconhecida: " + linha);
    }

    private static Perceptron obterPerceptronAtual(ModeloRedeNeural modelo, Perceptron perceptronAtual) {
        if (perceptronAtual.ehInvalido()) return perceptronAtual;
        modelo.getUltimaCamada().add(perceptronAtual);
        return new Perceptron();
    }

    private static double[] extrairPesos(String linha) {
        var pesosString = linha.substring(PESOS_PREFIXO.length()).trim().split("\\s+");
        var pesos = new double[pesosString.length];
        for (int i = 0; i < pesosString.length; i++)
            pesos[i] = Double.parseDouble(pesosString[i]);
        return pesos;
    }

    private static Double extrairVies(String linha) {
        return Double.parseDouble(linha.substring(VIES_PREFIXO.length()).trim());
    }

    public static void salvarRede(ModeloRedeNeural modelo) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (int i = 0; i < modelo.getNumeroCamadas(); i++) {
            if (i > 0) linhas.add(SEPARADOR_CAMADA_PREFIXO + " Camada " + i);
            criarCamadaLinhas(modelo.getCamada(i), linhas);
        }
        Files.write(CAMINHO_ARQUIVO_PESOS, linhas);
    }

    private static void criarCamadaLinhas(Camada camada, List<String> linhas) {
        var stringBuilder = inicializarStringBuilder(camada);
        for (var neuronio : camada) {
            stringBuilder.setLength(0);
            linhas.add(criarPesosLinha(neuronio, stringBuilder));
            linhas.add(criarViesLinha(neuronio));
        }
    }

    private static StringBuilder inicializarStringBuilder(Camada camada) {
        int numeroPesos = camada.get(0).getPesos().length;
        int capacidadeEstimada = numeroPesos * (PRECISAO_DECIMAL + FormatadorDecimal.SINAL_E_SEPARADOR_DECIMAL_TAMANHO + SEPARADOR_PESO_OU_VIES.length());
        return new StringBuilder(capacidadeEstimada);
    }

    private static String criarPesosLinha(Perceptron neuronio, StringBuilder stringBuilder) {
        var pesos = neuronio.getPesos();
        stringBuilder.append(formatadorDecimal.formatar(pesos[0]));
        for (int i = 1; i < pesos.length; i++) {
            stringBuilder.append(' ');
            stringBuilder.append(formatadorDecimal.formatar(pesos[i]));
        }
        return PESOS_PREFIXO + SEPARADOR_PESO_OU_VIES + stringBuilder;
    }

    private static String criarViesLinha(Perceptron neuronio) {
        return VIES_PREFIXO + SEPARADOR_PESO_OU_VIES + formatadorDecimal.formatar(neuronio.getVies());
    }
}

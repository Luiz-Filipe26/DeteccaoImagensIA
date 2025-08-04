package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.rede_neural.Camada;
import deteccao_imagens_ia.rede_neural.Perceptron;
import deteccao_imagens_ia.rede_neural.RedeNeural;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PersistenciaRedeNeural {

    private static final String PASTA_ARQUIVO = "IA-Desenho";
    private static final String NOME_ARQUIVO = "pesos_rede.txt";
    private static final Path CAMINHO_ARQUIVO = resolverCaminho();

    private static final String PESOS_PREFIX = "pesos:";
    private static final String VIES_PREFIX = "viés:";
    private static final String COMENTARIO_PREFIX = "#";
    private static final String SEPARADOR_CAMADA_PREFIX = "---";

    private static final String DECIMAL_REGEX = "-?\\d+(\\.\\d+)?";
    private static final String SEPARADOR_CAMADA_REGEX = SEPARADOR_CAMADA_PREFIX + ".+";
    private static final String LINHA_IGNORAVEL_REGEX = "(\\s+)|(" + COMENTARIO_PREFIX + ".+)";
    private static final String PESOS_REGEX = "pesos:\\s*" + DECIMAL_REGEX + "(\\s+" + DECIMAL_REGEX + ")*";
    private static final String VIES_REGEX = VIES_PREFIX + "\\s*" + DECIMAL_REGEX;


    public static List<String> lerArquivoDePesos() {
        try {
            var linhas = Files.readAllLines(CAMINHO_ARQUIVO);
            return linhas.stream().map(String::trim).toList();
        } catch (Exception e) {
            System.err.println("Erro ao ler o arquivo de pesos: " + e.getMessage());
            return List.of();
        }
    }

    private static Path resolverCaminho() {
        String appData = System.getenv("APPDATA");
        String pastaRaiz = (appData != null && !appData.isBlank()) ? appData : System.getProperty("user.home");
        var pasta = Path.of(pastaRaiz, PASTA_ARQUIVO);
        try {
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar diretório para salvar pesos: " + pasta, e);
        }
        return pasta.resolve(NOME_ARQUIVO);
    }

    public static RedeNeural construirRede(List<String> linhas, RedeNeural redeNeural) {
        redeNeural.adicionarCamadaVazia();
        var perceptronAtual = new Perceptron();
        for (var linha : linhas) {
            processarLinha(linha, redeNeural, perceptronAtual);
            perceptronAtual = pegarPerceptronAtual(redeNeural, perceptronAtual);
        }
        return redeNeural;
    }

    private static void processarLinha(String linha, RedeNeural redeNeural, Perceptron perceptronAtual) {
        if (linha.matches(LINHA_IGNORAVEL_REGEX)) return;
        if (linha.matches(SEPARADOR_CAMADA_REGEX)) redeNeural.adicionarCamadaVazia();
        else if (linha.matches(PESOS_REGEX)) perceptronAtual.setPesos(extrairPesos(linha));
        else if (linha.matches(VIES_REGEX)) perceptronAtual.setVies(extrairVies(linha));
        else throw new IllegalArgumentException("Linha não reconhecida: " + linha);
    }

    private static Perceptron pegarPerceptronAtual(RedeNeural redeNeural, Perceptron perceptronAtual) {
        if (perceptronAtual.ehInvalido()) return perceptronAtual;
        redeNeural.getUltimaCamada().add(perceptronAtual);
        return new Perceptron();
    }

    private static double[] extrairPesos(String linha) {
        var pesosStr = linha.substring(PESOS_PREFIX.length()).trim().split("\\s+");
        var pesos = new double[pesosStr.length];
        for (int i = 0; i < pesosStr.length; i++)
            pesos[i] = Double.parseDouble(pesosStr[i]);
        return pesos;
    }

    private static Double extrairVies(String linha) {
        return Double.parseDouble(linha.substring(VIES_PREFIX.length()).trim());
    }

    public static void salvarRede(RedeNeural redeNeural) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (int i = 0; i < redeNeural.getNumeroCamadas(); i++) {
            if (i > 0) linhas.add(SEPARADOR_CAMADA_PREFIX + " Camada " + i);
            criarCamadaLinhas(redeNeural.getCamada(i), linhas);
        }
        Files.write(CAMINHO_ARQUIVO, linhas);
    }

    private static void criarCamadaLinhas(Camada camada, List<String> linhas) {
        for (var neuronio : camada) {
            String pesosStr = Arrays.stream(neuronio.getPesos())
                    .mapToObj(PersistenciaRedeNeural::formatarDecimal)
                    .collect(Collectors.joining(" "));
            linhas.add(PESOS_PREFIX + " " + pesosStr);
            linhas.add(VIES_PREFIX + " " + formatarDecimal(neuronio.getVies()));
        }
    }

    private static String formatarDecimal(double decimal) {
        var formatado = String.format(Locale.US, "%.10f", decimal);
        return formatado.replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}

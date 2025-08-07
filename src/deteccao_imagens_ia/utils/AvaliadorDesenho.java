package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.rede_neural.RedeNeural;
import deteccao_imagens_ia.rede_neural.ResultadoClassificacao;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AvaliadorDesenho {

    private static final int MAX_BOLINHAS_POR_CELULA = 10;

    public ResultadoClassificacao analisarDesenho(List<Point> bolinhas, boolean treinarModelo) {
        var redeNeural = criarRedeNeuralValida(bolinhas, treinarModelo);
        var entrada = normalizarEntrada(bolinhas, redeNeural.getTamanhoEntrada());
        if (treinarModelo) {
            redeNeural.treinar(entrada, new double[]{1.0});
            salvarRede(redeNeural);
        }
        return redeNeural.detectar(entrada);
    }

    private void salvarRede(RedeNeural redeNeural) {
        try {
            PersistenciaRedeNeural.salvarRede(redeNeural);
        } catch (IOException e) {
            System.err.println("Erro ao salvar pesos: " + e.getMessage());
        }
    }

    private RedeNeural criarRedeNeuralValida(List<Point> bolinhas, boolean treinarModelo) {
        var redeNeural = CriadorRedeNeural.criarRede();
        if (redeNeural == null) throw new IllegalStateException("Não foi possível ler arquivo de pesos!");
        if (redeNeural.ehRedeInvalida()) throw new IllegalStateException("A Rede Neural não é válida!");
        return redeNeural;
    }

    private Rectangle encontrarAreaDesenho(List<Point> bolinhas) {
        var statsX = bolinhas.stream().mapToInt(p -> p.x).summaryStatistics();
        var statsY = bolinhas.stream().mapToInt(p -> p.y).summaryStatistics();
        var menorPonto = new Point(statsX.getMin(), statsY.getMin());
        var distancia = new Dimension(statsX.getMax() - menorPonto.x, statsY.getMax() - menorPonto.y);
        return new Rectangle(menorPonto.x, menorPonto.y, distancia.width, distancia.height);
    }

    public double[] normalizarEntrada(List<Point> bolinhas, int quantidadeEntradas) {
        int[] contagens = new int[quantidadeEntradas];
        Rectangle areaDesenho = encontrarAreaDesenho(bolinhas);
        contarEntradas(bolinhas, areaDesenho, contagens);
        return normalizarEntrada(contagens);
    }

    private void contarEntradas(List<Point> bolinhas, Rectangle areaDesenho, int[] bolinhasPorEntrada) {
        int tamanhoLadoDoGrid = calcularTamanhoLadoDoGrid(bolinhasPorEntrada.length);
        for (var bolinha : bolinhas) {
            int coluna = (int) ((double) (bolinha.x - areaDesenho.x) / areaDesenho.width * tamanhoLadoDoGrid);
            int linha = (int) ((double) (bolinha.y - areaDesenho.y) / areaDesenho.height * tamanhoLadoDoGrid);
            if (!estaDentroDoGrid(linha, coluna, tamanhoLadoDoGrid)) continue;
            int indice = linha * tamanhoLadoDoGrid + coluna;
            bolinhasPorEntrada[indice]++;
        }
    }

    private double[] normalizarEntrada(int[] bolinhasPorEntrada) {
        return Arrays.stream(bolinhasPorEntrada)
                .mapToDouble(item -> Math.min((double) item / MAX_BOLINHAS_POR_CELULA, 1.0))
                .toArray();
    }

    private boolean estaDentroDoGrid(int linha, int coluna, int tamanhoLadoGrid) {
        return linha >= 0 && linha < tamanhoLadoGrid && coluna >= 0 && coluna < tamanhoLadoGrid;
    }

    private int calcularTamanhoLadoDoGrid(int quantidadeEntradas) {
        int lado = (int) Math.sqrt(quantidadeEntradas);
        if (lado * lado != quantidadeEntradas)
            throw new IllegalStateException("Número de entradas da rede não forma um grid quadrado perfeito!");
        return lado;
    }
}
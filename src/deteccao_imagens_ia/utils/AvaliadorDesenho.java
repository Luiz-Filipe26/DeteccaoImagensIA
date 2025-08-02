package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.rede_neural.ResultadoClassificacao;
import deteccao_imagens_ia.rede_neural.RedeNeural;

import java.awt.*;

public class AvaliadorDesenho {

    public ResultadoClassificacao analisarDesenho(Point[] bolinhas) {
        var rede = criarRedeNeuralValida();
        var entradas = calcularEntradas(bolinhas, rede.getTamanhoEntrada());
        return rede.detectar(entradas);
    }

    private RedeNeural criarRedeNeuralValida() {
        var rede = CriadorRedeNeural.criarRede();
        if (!rede.validarRede())
            throw new IllegalStateException("A Rede Neural não é válida!");
        return rede;
    }

    private int[] encontrarDimensoesDesenho(Point[] bolinhas) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point p : bolinhas) {
            if (p.x < minX) minX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.x > maxX) maxX = p.x;
            if (p.y > maxY) maxY = p.y;
        }

        int larguraDesenho = maxX - minX;
        int alturaDesenho = maxY - minY;

        return new int[] {minX, minY, larguraDesenho, alturaDesenho};
    }

    private double[] calcularEntradas(Point[] bolinhas, int quantidadeEntradas) {
        int tamanhoLadoDoGrid = calcularTamanhoLadoDoGrid(quantidadeEntradas); // entrada de 50 ou 100
        double[] entradas = new double[quantidadeEntradas];

        int[] novasDimensoesDesenho = encontrarDimensoesDesenho(bolinhas);
        int minX = novasDimensoesDesenho[0];
        int minY = novasDimensoesDesenho[1];
        int larguraDesenho = novasDimensoesDesenho[2];
        int alturaDesenho = novasDimensoesDesenho[3];

        int larguraCelula = larguraDesenho / tamanhoLadoDoGrid;
        int alturaCelula = alturaDesenho / tamanhoLadoDoGrid;

        for (var ponto : bolinhas) {
            int coluna = (ponto.x - minX) / larguraCelula;
            int linha = (ponto.y - minY) / alturaCelula;

            if (!estaDentroDoGrid(linha, coluna, tamanhoLadoDoGrid)) continue;
            int indice = linha * tamanhoLadoDoGrid + coluna;
            entradas[indice]++;
        }

        return entradas;
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
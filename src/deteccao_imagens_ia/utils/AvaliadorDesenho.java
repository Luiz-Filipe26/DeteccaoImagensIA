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
        if(rede == null) {
            throw new IllegalStateException("Não foi possível ler arquivo de pesos!");
        }

        if (rede.ehRedeInvalida())
            throw new IllegalStateException("A Rede Neural não é válida!");
        return rede;
    }

    private Rectangle encontrarAreaDesenho(Point[] bolinhas) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (var bolinha : bolinhas) {
            if (bolinha.x < minX) minX = bolinha.x;
            if (bolinha.y < minY) minY = bolinha.y;
            if (bolinha.x > maxX) maxX = bolinha.x;
            if (bolinha.y > maxY) maxY = bolinha.y;
        }

        return new Rectangle(minX, minY, maxX - minX, maxY-minY);
    }

    private double[] calcularEntradas(Point[] bolinhas, int quantidadeEntradas) {
        int tamanhoLadoDoGrid = calcularTamanhoLadoDoGrid(quantidadeEntradas); // entrada de 50 ou 100
        double[] entradas = new double[quantidadeEntradas];
        Rectangle areaDesenho = encontrarAreaDesenho(bolinhas);

        for (var bolinha : bolinhas) {
            int coluna = (bolinha.x - areaDesenho.x) / areaDesenho.width;
            int linha = (bolinha.y - areaDesenho.y) / areaDesenho.height;

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
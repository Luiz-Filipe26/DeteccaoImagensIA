package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.rede_neural.ResultadoClassificacao;
import deteccao_imagens_ia.rede_neural.RedeNeural;

import java.awt.*;

public class AvaliadorDesenho {
    private final int larguraDesenho;
    private final int alturaDesenho;

    public AvaliadorDesenho(int larguraDesenho, int alturaDesenho) {
        this.larguraDesenho = larguraDesenho;
        this.alturaDesenho = alturaDesenho;
    }

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

    private double[] calcularEntradas(Point[] bolinhas, int quantidadeEntradas) {
        int tamanhoLadoDoGrid = calcularTamanhoLadoDoGrid(quantidadeEntradas);
        double[] entradas = new double[quantidadeEntradas];

        int larguraCelula = larguraDesenho / tamanhoLadoDoGrid;
        int alturaCelula = alturaDesenho / tamanhoLadoDoGrid;

        for (var ponto : bolinhas) {
            int coluna = ponto.x / larguraCelula;
            int linha = ponto.y / alturaCelula;

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
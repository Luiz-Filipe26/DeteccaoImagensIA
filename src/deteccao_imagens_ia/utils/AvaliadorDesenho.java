package deteccao_imagens_ia.utils;

import deteccao_imagens_ia.rede_neural.ResultadoClassificacao;
import deteccao_imagens_ia.rede_neural.RedeNeural;

import java.awt.*;
import java.util.List;

public class AvaliadorDesenho {

    public ResultadoClassificacao analisarDesenho(List<Point> bolinhas, boolean treinarModelo) {
        var rede = criarRedeNeuralValida(bolinhas, treinarModelo);
        var entrada = calcularEntrada(bolinhas, rede.getTamanhoEntrada());
        var resultado = rede.detectar(entrada);
        if(resultado != ResultadoClassificacao.DESENHO_ESPERADO)
            rede.treinar(entrada, new double[]{1.0});
        return rede.detectar(entrada);
    }


    private RedeNeural criarRedeNeuralValida(List<Point> bolinhas, boolean treinarModelo) {
        var rede = CriadorRedeNeural.criarRede();
        if(rede == null && treinarModelo)
            rede = CriadorRedeNeural.criarRedePelaEntrada(this, bolinhas);
        if(rede == null)
            throw new IllegalStateException("Não foi possível ler arquivo de pesos!");
        if (rede.ehRedeInvalida())
            throw new IllegalStateException("A Rede Neural não é válida!");
        return rede;
    }

    private Rectangle encontrarAreaDesenho(List<Point> bolinhas) {
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

    public double[] calcularEntrada(List<Point> bolinhas, int quantidadeEntradas) {
        int tamanhoLadoDoGrid = calcularTamanhoLadoDoGrid(quantidadeEntradas); // entrada de 50 ou 100
        double[] entrada = new double[quantidadeEntradas];
        Rectangle areaDesenho = encontrarAreaDesenho(bolinhas);

        for (var bolinha : bolinhas) {
            int coluna = (int)((double)(bolinha.x - areaDesenho.x) / areaDesenho.width * tamanhoLadoDoGrid);
            int linha = (int)((double)(bolinha.y - areaDesenho.y) / areaDesenho.height * tamanhoLadoDoGrid);
            if (!estaDentroDoGrid(linha, coluna, tamanhoLadoDoGrid)) continue;
            int indice = linha * tamanhoLadoDoGrid + coluna;
            entrada[indice] += 1.0;
        }

        return entrada;
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
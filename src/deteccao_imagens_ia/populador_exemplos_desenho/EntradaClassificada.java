package deteccao_imagens_ia.populador_exemplos_desenho;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public record EntradaClassificada(double[] entrada, ClassificacaoDesenho classificacao) {

    public static EntradaClassificada deDesenho(DesenhoClassificado desenhoClassificado, int quantidadeEntradas, int maxBolinhaPorCelula) {
        double[] entrada = normalizarEntrada(desenhoClassificado.pontosDesenho(), quantidadeEntradas, maxBolinhaPorCelula);
        return new EntradaClassificada(entrada, desenhoClassificado.classificacaoDesenho());
    }

    private static double[] normalizarEntrada(List<Point> pontos, int quantidadeEntradas, int maxBolinhaPorCelula) {
        int[] contagens = new int[quantidadeEntradas];
        Rectangle areaDesenho = encontrarAreaDesenho(pontos);
        contarEntradas(pontos, areaDesenho, contagens);
        return normalizarEntrada(contagens, maxBolinhaPorCelula);
    }

    private static Rectangle encontrarAreaDesenho(List<Point> pontos) {
        var statsX = pontos.stream().mapToInt(p -> p.x).summaryStatistics();
        var statsY = pontos.stream().mapToInt(p -> p.y).summaryStatistics();
        var menorPonto = new Point(statsX.getMin(), statsY.getMin());
        var distancia = new Dimension(statsX.getMax() - menorPonto.x, statsY.getMax() - menorPonto.y);
        return new Rectangle(menorPonto.x, menorPonto.y, distancia.width, distancia.height);
    }

    private static void contarEntradas(List<Point> pontos, Rectangle areaDesenho, int[] pontosPorEntrada) {
        int tamanhoLadoDoGrid = calcularTamanhoLadoDoGrid(pontosPorEntrada.length);
        for (var bolinha : pontos) {
            int coluna = (int) ((double) (bolinha.x - areaDesenho.x) / areaDesenho.width * tamanhoLadoDoGrid);
            int linha = (int) ((double) (bolinha.y - areaDesenho.y) / areaDesenho.height * tamanhoLadoDoGrid);
            if (!estaDentroDoGrid(linha, coluna, tamanhoLadoDoGrid)) continue;
            int indice = linha * tamanhoLadoDoGrid + coluna;
            pontosPorEntrada[indice]++;
        }
    }

    private static double[] normalizarEntrada(int[] pontosPorEntrada, int maxBolinhaPorCelula) {
        return Arrays.stream(pontosPorEntrada)
                .mapToDouble(item -> Math.min((double) item / maxBolinhaPorCelula, 1.0))
                .toArray();
    }

    private static boolean estaDentroDoGrid(int linha, int coluna, int tamanhoLadoGrid) {
        return linha >= 0 && linha < tamanhoLadoGrid && coluna >= 0 && coluna < tamanhoLadoGrid;
    }

    private static int calcularTamanhoLadoDoGrid(int quantidadeEntradas) {
        int lado = (int) Math.sqrt(quantidadeEntradas);
        if (lado * lado != quantidadeEntradas)
            throw new IllegalStateException("Número de entradas da rede não forma um grid quadrado perfeito!");
        return lado;
    }
}

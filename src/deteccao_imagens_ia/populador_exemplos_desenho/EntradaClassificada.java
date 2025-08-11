package deteccao_imagens_ia.populador_exemplos_desenho;

import java.awt.*;
import java.util.*;
import java.util.List;

public record EntradaClassificada(double[] entrada, ClassificacaoDesenho classificacao) {

    public static EntradaClassificada deDesenho(DesenhoClassificado desenhoClassificado, int quantidadeEntradas, int maxBolinhaPorCelula) {
        double[] entrada = normalizarEntrada(desenhoClassificado.pontosDesenho(), quantidadeEntradas, maxBolinhaPorCelula);
        return new EntradaClassificada(entrada, desenhoClassificado.classificacaoDesenho());
    }

    private static double[] normalizarEntrada(List<Point> pontos, int quantidadeEntradas, int maxBolinhaPorCelula) {
        if (pontos == null || pontos.isEmpty()) return new double[quantidadeEntradas];
        int[] contagens = new int[quantidadeEntradas];
        var areaDesenho = encontrarAreaDesenho(pontos);
        contarEntradas(pontos, areaDesenho, contagens);
        return normalizarContagem(contagens, maxBolinhaPorCelula);
    }

    private static Rectangle encontrarAreaDesenho(List<Point> pontos) {
        var statsX = pontos.stream().mapToInt(p -> p.x).summaryStatistics();
        var statsY = pontos.stream().mapToInt(p -> p.y).summaryStatistics();
        var menorPonto = new Point(statsX.getMin(), statsY.getMin());
        var distancia = new Dimension(statsX.getMax() - menorPonto.x, statsY.getMax() - menorPonto.y);
        return new Rectangle(menorPonto.x, menorPonto.y, distancia.width, distancia.height);
    }

    private static void contarEntradas(List<Point> pontos, Rectangle areaDesenho, int[] pontosPorEntrada) {
        int menorLadoIdealGrid = calcularMenorLadoIdealGrid(pontosPorEntrada.length);
        int maiorLadoIdealGrid = pontosPorEntrada.length / menorLadoIdealGrid;
        var gridIdeal = new Dimension(menorLadoIdealGrid, maiorLadoIdealGrid);
        for (var ponto : pontos)
            calcularIndiceEntrada(areaDesenho, gridIdeal, ponto).ifPresent(indice -> pontosPorEntrada[indice]++);
    }

    /**
     * Calcula o menor lado do grid "mais quadrado" possível que pode ser criado
     */
    private static int calcularMenorLadoIdealGrid(int area) {
        int melhorLadoMenor = 1;
        for (int ladoMenorCandidato = 2; ladoMenorCandidato <= Math.sqrt(area); ladoMenorCandidato++)
            if (area % ladoMenorCandidato == 0)
                melhorLadoMenor = obterMelhorLadoMenor(area, melhorLadoMenor, ladoMenorCandidato);
        return melhorLadoMenor;
    }

    private static int obterMelhorLadoMenor(int area, int ladoMenor1, int ladoMenor2) {
        int ladoMaior1 = area / ladoMenor1;
        int ladoMaior2 = area / ladoMenor2;
        return ladoMaior1 - ladoMenor1 < ladoMaior2 - ladoMenor2 ? ladoMenor1 : ladoMenor2;
    }

    private static Optional<Integer> calcularIndiceEntrada(Rectangle areaDesenho, Dimension grid, Point ponto) {
        double posXRelativa = calcularPosicaoRelativa(ponto.x, areaDesenho.x, areaDesenho.width);
        int coluna = (int) (grid.width * posXRelativa);
        double posYRelativa = calcularPosicaoRelativa(ponto.y, areaDesenho.y, areaDesenho.height);
        int linha = (int) (grid.height * posYRelativa);
        return estaDentroDoGrid(linha, coluna, grid) ? Optional.of(linha * grid.width + coluna) : Optional.empty();
    }

    private static double calcularPosicaoRelativa(double coordenadaPonto, int coordenadaMinimaArea, int tamanhoArea) {
        if (tamanhoArea == 0) return 0.0;
        return (coordenadaPonto - coordenadaMinimaArea) / (double) tamanhoArea;
    }

    private static boolean estaDentroDoGrid(int linha, int coluna, Dimension grid) {
        return linha >= 0 && linha < grid.height && coluna >= 0 && coluna < grid.width;
    }

    private static double[] normalizarContagem(int[] pontosPorEntrada, int maxBolinhaPorCelula) {
        return Arrays.stream(pontosPorEntrada)
                .mapToDouble(item -> Math.min((double) item / maxBolinhaPorCelula, 1.0))
                .toArray();
    }
}

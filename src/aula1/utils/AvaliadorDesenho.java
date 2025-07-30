package aula1.utils;

import java.awt.*;

public class AvaliadorDesenho {

    private static final int LIMIAR_CLASSIFICACAO = 47;
    private final int larguraDesenho;
    private final int alturaDesenho;

    public AvaliadorDesenho(int larguraDesenho, int alturaDesenho) {
        this.larguraDesenho = larguraDesenho;
        this.alturaDesenho = alturaDesenho;
    }

    private static final int PROJECAO_LARGURA = 10;
    private static final int PROJECAO_ALTURA = 10;

    private int[] calcularProjecaoVertical(Point[] bolinhas, int altura) {
        int[] projecao = new int[PROJECAO_ALTURA];
        int intervalo = altura / PROJECAO_ALTURA;

        for (var bolinha : bolinhas) {
            int indice = bolinha.y / intervalo;
            if (indice >= 0 && indice < projecao.length) {
                projecao[indice]++;
            }
        }

        return projecao;
    }

    private int[] calcularProjecaoHorizontal(Point[] bolinhas, int largura) {
        int[] projecao = new int[PROJECAO_LARGURA];
        int intervalo = largura / PROJECAO_LARGURA;

        for (var bolinha : bolinhas) {
            int indice = bolinha.x / intervalo;
            if (indice >= 0 && indice < projecao.length) {
                projecao[indice]++;
            }
        }
        return projecao;
    }

    public ResultadoClassificacao analisarDesenho(Point[] bolinhas) {
        int[] projecaoVertical = calcularProjecaoVertical(bolinhas, alturaDesenho);
        int[] projecaoHorizontal = calcularProjecaoHorizontal(bolinhas, larguraDesenho);
        RedeNeural redeNeural = new RedeNeural(projecaoVertical, projecaoHorizontal);
        return redeNeural.aplica(LIMIAR_CLASSIFICACAO);
    }
}

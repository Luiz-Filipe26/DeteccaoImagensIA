package aula1.utils;

public class RedeNeural {
    private final double[] pesosPrimeiraCamada = {
            3, 3, 2, 2, 2, 1, 1, 1, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private final double[] pesosSegundaCamada = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 1, 2, 2, 1, 1, 1, 1
    };

    private final double pesoSaida1 = 1.0;
    private final double pesoSaida2 = 0.5;

    private final double[] entradas;

    public RedeNeural(int[] projecaoVertical, int[] projecaoHorizontal) {
        entradas = new double[projecaoVertical.length + projecaoHorizontal.length];

        for (int i = 0; i < projecaoVertical.length; i++) {
            entradas[i] = projecaoVertical[i];
        }
        for (int i = 0; i < projecaoHorizontal.length; i++) {
            entradas[i + projecaoVertical.length] = projecaoHorizontal[i];
        }
    }

    private double calcularAtivacao(double[] entradas, double[] pesos) {
        double soma = 0;
        for (int i = 0; i < entradas.length; i++) {
            soma += entradas[i] * pesos[i];
        }
        return soma;
    }

    public ResultadoClassificacao aplica(double limiar) {
        double somaPrimeiraCamada = 0;
        double somaSegundaCamada = 0;

        somaPrimeiraCamada += calcularAtivacao(entradas, pesosPrimeiraCamada);
        somaSegundaCamada += calcularAtivacao(entradas, pesosSegundaCamada);

        double saida = somaPrimeiraCamada * pesoSaida1 + somaSegundaCamada * pesoSaida2;

        System.out.println("Saída da rede = " + saida);

        return saida > limiar ? ResultadoClassificacao.ARVORE : ResultadoClassificacao.NAO_ARVORE;
    }
}

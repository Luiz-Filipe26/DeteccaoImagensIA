package deteccao_imagens_ia.rede_neural;

import java.util.ArrayList;
import java.util.List;

public class RedeNeural {
    private final List<Camada> camadas = new ArrayList<>();
    private static final double LIMIAR = 0.5;

    public void adicionarCamada(Camada camada) {
        camadas.add(camada);
    }

    public void adicionarCamadaVazia() {
        camadas.add(new Camada());
    }

    public Camada getUltimaCamada() {
        return camadas.get(camadas.size() - 1);
    }

    public boolean validarRede() {
        if (camadas.isEmpty())
            return false;

        if(camadas.stream().anyMatch(Camada::isEmpty)) {
            return false;
        }

        for (int i = 0; i < camadas.size(); i++) {
            var camada = camadas.get(i);

            int entradasEsperadas = camada.getNumeroEntradas();

            for (var perceptron : camada) {
                if (perceptron.ehInvalido() || perceptron.getNumeroPesos() != entradasEsperadas)
                    return false;
            }

            if (i > 0) {
                int saidaAnterior = camadas.get(i - 1).size();
                if (entradasEsperadas != saidaAnterior)
                    return false;
            }
        }

        return true;
    }

    public int getTamanhoEntrada() {
        return camadas.get(0).getNumeroEntradas();
    }

    public ResultadoClassificacao detectar(double[] entrada) {
        double[] saida = calcularSaida(entrada);

        if(saida.length != 1) {
            throw new IllegalArgumentException("A saída da rede neural deve ter só 1 valor!");
        }

        return saida[0] > LIMIAR ? ResultadoClassificacao.DESENHO_ESPERADO : ResultadoClassificacao.DESENHO_NAO_ESPERADO;
    }

    private double[] calcularSaida(double[] entrada) {
        double[] saidaAtual = entrada;

        for (var camada : camadas) {
            double[] novaSaida = new double[camada.size()];
            for (int i = 0; i < camada.size(); i++) {
                var perceptron = camada.get(i);
                novaSaida[i] = perceptron.calcularSaida(saidaAtual);
            }
            saidaAtual = novaSaida;
        }

        return saidaAtual;
    }
}
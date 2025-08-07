package deteccao_imagens_ia.rede_neural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModeloRedeNeural {
    private final List<Camada> camadas = new ArrayList<>();

    public ModeloRedeNeural() {

    }

    public ModeloRedeNeural(List<Integer> tamanhosPorCamada) {
        criarCamadasVazias(tamanhosPorCamada);
    }

    private void criarCamadasVazias(List<Integer> tamanhosPorCamada) {
        for (int tamanhoIndex = 1; tamanhoIndex < tamanhosPorCamada.size(); tamanhoIndex++) {
            int entradasPorNeuronio = tamanhosPorCamada.get(tamanhoIndex - 1);
            int quantidadeNeuronios = tamanhosPorCamada.get(tamanhoIndex);
            criarCamadaVazia(quantidadeNeuronios, entradasPorNeuronio);
        }
    }

    private void criarCamadaVazia(int quantidadeNeuronios, int entradasPorNeuronio) {
        var camada = new Camada();
        for (int neuronioIndex = 0; neuronioIndex < quantidadeNeuronios; neuronioIndex++) {
            var perceptron = Perceptron.comInicializacaoAleatoria(entradasPorNeuronio);
            camada.add(perceptron);
        }
        camadas.add(camada);
    }

    public Iterable<Camada> getIteradorDeCamadas() {
        return Collections.unmodifiableList(camadas);
    }

    public Camada getUltimaCamada() {
        return camadas.get(camadas.size() - 1);
    }

    public Camada getCamadaSaida() {
        return getUltimaCamada();
    }

    public int getTamanhoEntrada() {
        return camadas.get(0).getNumeroEntradas();
    }

    public int getNumeroCamadas() {
        return camadas.size();
    }

    public Camada getCamada(int index) {
        return camadas.get(index);
    }

    public void adicionarCamada(Camada camada) {
        camadas.add(camada);
    }

    public boolean saoCamadasInconsistentes() {
        if (camadas.isEmpty()) return true;
        if (camadas.stream().anyMatch(Camada::isEmpty)) return true;
        for (int i = 0; i < camadas.size(); i++) {
            var camada = camadas.get(i);
            var camadaAnterior = i == 0 ? null : camadas.get(i - 1);
            if (!ehCamadaConsistente(camada, camadaAnterior)) return true;
        }
        return false;
    }

    private boolean ehCamadaConsistente(Camada camada, Camada camadaAnterior) {
        int entradasEsperadas = camada.getNumeroEntradas();
        for (var perceptron : camada) {
            if (perceptron.ehInvalido() || perceptron.getNumeroPesos() != entradasEsperadas) return false;
        }
        return camadaAnterior == null || entradasEsperadas == camadaAnterior.size();
    }
}

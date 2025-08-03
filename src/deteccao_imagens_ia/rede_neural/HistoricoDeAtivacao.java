package deteccao_imagens_ia.rede_neural;

import java.util.ArrayList;
import java.util.List;

public record HistoricoDeAtivacao(List<double[]> saidasAntesDeAtivar, List<double[]> saidasAtivadas ) {
    public HistoricoDeAtivacao() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public void addSaidasCamada(List<SaidasNeuronio> saidasNeuronios) {
        double[] saidaAntesDeAtivar = new double[saidasNeuronios.size()];
        double[] saidaAtivada = new double[saidasNeuronios.size()];
        for(int i=0; i<saidasNeuronios.size(); i++) {
            saidaAntesDeAtivar[i] = saidasNeuronios.get(i).saidaAntesDeAtivar();
            saidaAtivada[i] = saidasNeuronios.get(i).saidaAtivada();
        }
        saidasAntesDeAtivar.add(saidaAntesDeAtivar);
        saidasAtivadas.add(saidaAtivada);
    }

    public double[] getSaidaAtivadaUltimaCamada() {
        return saidasAtivadas.get(saidasAtivadas.size() - 1);
    }

    public double[] getSaidaRedeAtivada() {
        return getSaidaAtivadaUltimaCamada();
    }
}
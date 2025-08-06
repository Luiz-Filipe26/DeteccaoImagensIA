package deteccao_imagens_ia.rede_neural;

public class Perceptron implements Cloneable {
    private double[] pesos;
    private Double vies;

    public Perceptron() {
        vies = null;
    }

    public Perceptron(double[] pesos, double vies) {
        this.pesos = pesos;
        this.vies = vies;
    }

    public Perceptron(int numeroPesos) {
        this.pesos = new double[numeroPesos];
        for (int i = 0; i < numeroPesos; i++) {
            this.pesos[i] = (Math.random() * 2 - 1) * 0.1;
        }
        this.vies = (Math.random() * 2 - 1) * 0.1;
    }

    @Override
    public Perceptron clone() {
        Perceptron clone = new Perceptron();
        clone.pesos = pesos.clone();
        clone.vies = vies;
        return clone;
    }

    public void setPesos(double[] pesos) {
        this.pesos = pesos;
    }
    public void setVies(double vies) {
        this.vies = vies;
    }

    public double getPeso(int index) {
        return pesos[index];
    }

    public double[] getPesos() {
        return pesos;
    }

    public double getVies() {
        return vies;
    }

    public int getNumeroPesos() {
        if (pesos == null)
            throw new IllegalStateException("Pesos ainda não foram definidos");

        return pesos.length;
    }

    public void atualizarPesos(double[] entradas, double taxaAprendizado, double erro) {
        for (int i = 0; i < pesos.length; i++)
            pesos[i] += taxaAprendizado * erro * entradas[i];

        vies += taxaAprendizado * erro;
    }

    public boolean ehInvalido() {
        return pesos == null || pesos.length == 0 || vies == null;
    }

    public SaidasNeuronio calcularSaida(double[] entradas) {
        double soma = vies;
        for (int i = 0; i < entradas.length; i++)
            soma += entradas[i] * pesos[i];

        return new SaidasNeuronio(soma, AtivacaoSigmoide.ativar(soma));
    }
}

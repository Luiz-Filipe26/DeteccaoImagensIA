package deteccao_imagens_ia.rede_neural;

public class Perceptron {
    private double[] pesos;
    private Double vies;

    public Perceptron() {
        vies = null;
    }

    public Perceptron(double[] pesos, double vies) {
        this.pesos = pesos;
        this.vies = vies;
    }

    public void adicionarPesos(double[] pesos) {
        this.pesos = pesos;
    }
    public void adicionarVies(double vies) {
        this.vies = vies;
    }

    public int getNumeroPesos() {
        if (pesos == null)
            throw new IllegalStateException("Pesos ainda não foram definidos");

        return pesos.length;
    }

    public boolean ehInvalido() {
        return pesos == null || pesos.length == 0 || vies == null;
    }

    public double calcularSaida(double[] entradas) {
        if (entradas.length != pesos.length)
            throw new IllegalArgumentException("Número incorreto de entradas");

        double soma = vies;
        for (int i = 0; i < pesos.length; i++)
            soma += entradas[i] * pesos[i];

        return AtivacaoSigmoide.ativar(soma);
    }
}

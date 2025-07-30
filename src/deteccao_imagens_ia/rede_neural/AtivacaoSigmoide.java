package deteccao_imagens_ia.rede_neural;

public class AtivacaoSigmoide {
    public static double ativar(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static double derivada(double x) {
        double sig = ativar(x);
        return sig * (1 - sig);
    }
}

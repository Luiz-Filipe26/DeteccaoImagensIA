package deteccao_imagens_ia.rede_neural;

import java.util.ArrayList;

public class Camada extends ArrayList<Perceptron> {
    public int getNumeroEntradas() {
        if (isEmpty())
            throw new IllegalStateException("Camada está vazia");

        return get(0).getNumeroPesos();
    }
}

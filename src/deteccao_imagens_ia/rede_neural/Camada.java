package deteccao_imagens_ia.rede_neural;

import java.util.ArrayList;

public class Camada extends ArrayList<Perceptron> implements Cloneable {

    @Override
    public Camada clone() {
        Camada clone = new Camada();
        for (Perceptron p : this)
            clone.add(p.clone());
        return clone;
    }

    public int getNumeroEntradas() {
        if (isEmpty())
            throw new IllegalStateException("Camada está vazia");

        return get(0).getNumeroPesos();
    }
}

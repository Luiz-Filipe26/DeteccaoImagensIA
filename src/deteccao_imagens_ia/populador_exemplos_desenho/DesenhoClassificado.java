package deteccao_imagens_ia.populador_exemplos_desenho;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record DesenhoClassificado(ClassificacaoDesenho classificacaoDesenho, List<Point> pontosDesenho) {
    public String gerarHash() {
        var pontosCopia = new ArrayList<>(pontosDesenho());
        var comparador = Comparator.comparingDouble(Point::getX).thenComparingDouble(Point::getY);
        pontosCopia.sort(comparador);
        return Integer.toHexString(pontosCopia.hashCode());
    }
}

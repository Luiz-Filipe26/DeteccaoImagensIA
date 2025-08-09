package deteccao_imagens_ia.view;

import java.awt.*;

public record CirculoGrafico(Point pontoCentral, int raio, Color cor, Tipo tipo) {
    public enum Tipo {
        OCO, PREENCHIDO
    }

    public CirculoGrafico(Point pontoCentral, int raio, Color cor) {
        this(pontoCentral, raio, cor, Tipo.PREENCHIDO);
    }

    public int getX() {
        return (int) pontoCentral.getX();
    }

    public int getY() {
        return (int) pontoCentral.getY();
    }

    public void desenhar(Graphics graphics) {
        graphics.setColor(cor);
        int x = getX() - raio;
        int y = getY() - raio;
        int diametro = raio * 2;
        if (tipo == Tipo.OCO)
            graphics.drawOval(x, y, diametro, diametro);
        else
            graphics.fillOval(x, y, diametro, diametro);
    }
}

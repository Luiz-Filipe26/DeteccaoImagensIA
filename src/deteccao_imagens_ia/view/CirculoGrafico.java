package deteccao_imagens_ia.view;

import java.awt.*;

public record CirculoGrafico(Point pontoCentral, int raio, Color cor) {
    public int getX() {
        return (int) pontoCentral().getX();
    }
    public int getY() {
        return (int) pontoCentral().getY();
    }
    public void desenhar(Graphics graphics) {
        graphics.setColor(cor);
        int x = getX() - raio;
        int y = getY() - raio;
        int d = raio * 2;
        graphics.fillOval(x, y, d, d);
    }
}

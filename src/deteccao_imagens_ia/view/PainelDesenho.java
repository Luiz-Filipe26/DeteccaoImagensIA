package deteccao_imagens_ia.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PainelDesenho extends JPanel {
    private final BufferedImage bufferGrafico;
    private final Graphics desenhoGraphics;

    public PainelDesenho(int largura, int altura) {
        bufferGrafico = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        desenhoGraphics = bufferGrafico.getGraphics();
        setBackground(Color.WHITE);
    }

    public Graphics getDesenhoGraphics() {
        return desenhoGraphics;
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(bufferGrafico, 0, 0, null);
    }

    public void adicionarBolinha(int xCentral, int yCentral, int raio, Color cor) {
        int x = xCentral - raio;
        int y = yCentral - raio;
        desenhoGraphics.setColor(cor);
        desenhoGraphics.fillOval(x, y, raio * 2, raio * 2);
        repaint();
    }
}

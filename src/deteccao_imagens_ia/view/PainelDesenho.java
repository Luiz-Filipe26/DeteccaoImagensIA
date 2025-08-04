package deteccao_imagens_ia.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PainelDesenho extends JPanel {
    private static final int RAIO_BOLINHA = 25;
    private static final Color COR_FUNDO = Color.WHITE;
    private static final Color COR_BOLINHA = Color.BLACK;
    private static final int INTERVALO_TIMER_MS = 100;

    private final BufferedImage bufferGrafico;
    private final Graphics desenhoGraphics;

    private final Timer timer;
    private final PainelDesenhoListener painelDesenhoListener;
    private Point posicaoMouseAtual;

    public PainelDesenho(PainelDesenhoListener painelDesenhoListener, Dimension dimensao) {
        this.painelDesenhoListener = painelDesenhoListener;
        bufferGrafico = new BufferedImage((int) dimensao.getWidth(), (int) dimensao.getHeight(), BufferedImage.TYPE_INT_ARGB);
        desenhoGraphics = bufferGrafico.getGraphics();
        limparDesenho();
        timer = criarTimer();
        configurarEventosMouse();
    }

    public void limparDesenho() {
        desenhoGraphics.setColor(COR_FUNDO);
        desenhoGraphics.fillRect(0, 0, bufferGrafico.getWidth(), bufferGrafico.getHeight());
        setBackground(COR_FUNDO);
        repaint();
    }

    private Timer criarTimer() {
        return new Timer(INTERVALO_TIMER_MS, e -> {
            if (posicaoMouseAtual != null)
                adicionarBolinha(posicaoMouseAtual, RAIO_BOLINHA, COR_BOLINHA);
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(bufferGrafico, 0, 0, null);
    }

    private void configurarEventosMouse() {
        var mouseAdapter = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent mouseEvent) {
                mouseClicado(mouseEvent);
            }
            @Override public void mouseDragged(MouseEvent mouseEvent) {
                mouseArrastado(mouseEvent);
            }
            @Override public void mouseReleased(MouseEvent mouseEvent) {
                mouseSolto(mouseEvent);
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void mouseClicado(MouseEvent mouseEvent) {
        posicaoMouseAtual = mouseEvent.getPoint();
        adicionarBolinha(posicaoMouseAtual, RAIO_BOLINHA, COR_BOLINHA);
        timer.start();
    }

    private void mouseArrastado(MouseEvent mouseEvent) {
        posicaoMouseAtual = mouseEvent.getPoint();
    }

    private void mouseSolto(MouseEvent mouseEvent) {
        timer.stop();
        posicaoMouseAtual = null;
    }

    public void adicionarBolinha(Point pontoCentral, int raio, Color cor) {
        int x = (int) pontoCentral.getX() - raio;
        int y = (int) pontoCentral.getY() - raio;
        desenhoGraphics.setColor(cor);
        desenhoGraphics.fillOval(x, y, raio * 2, raio * 2);
        repaint();
        painelDesenhoListener.adicionarBolinha(posicaoMouseAtual);
    }
}

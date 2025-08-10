package deteccao_imagens_ia.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

public class PainelDesenho extends JPanel {
    private static final int RAIO_BOLINHA = 20;
    private static final int RAIO_CURSOR = 20;
    private static final Color COR_BOLINHA = Color.BLACK;
    private static final Color COR_CURSOR = Color.LIGHT_GRAY;
    private static final Color COR_FUNDO = Color.WHITE;
    private static final int INTERVALO_TIMER_MS = 70;
    private static final double MINIMO_ESCALA_BOLINHA = 0.1;
    private static final double MAXIMO_ESCALA_BOLINHA = 3.0;
    public static final double FATOR_ESCALA_MOUSE = 0.1;

    private final BufferedImage bufferGrafico;
    private final BufferedImage cursorGrafico;
    private final BufferedImage malhaGrafico;
    private final Graphics2D desenhoGraphics;
    private final Graphics2D cursorGraphics;
    private final Graphics2D malhaGraphics;

    private final Timer timer;
    private final PainelDesenhoListener painelDesenhoListener;
    private Point posicaoMouseAtual;
    private double escalaBolinha = 1.0;

    public PainelDesenho(PainelDesenhoListener painelDesenhoListener, Dimension dimensao) {
        this.painelDesenhoListener = painelDesenhoListener;
        bufferGrafico = new BufferedImage((int) dimensao.getWidth(), (int) dimensao.getHeight(), BufferedImage.TYPE_INT_ARGB);
        cursorGrafico = new BufferedImage((int) dimensao.getWidth(), (int) dimensao.getHeight(), BufferedImage.TYPE_INT_ARGB);
        malhaGrafico = new BufferedImage((int) dimensao.getWidth(), (int) dimensao.getHeight(), BufferedImage.TYPE_INT_ARGB);
        desenhoGraphics = (Graphics2D) bufferGrafico.getGraphics();
        cursorGraphics = (Graphics2D) cursorGrafico.getGraphics();
        malhaGraphics = (Graphics2D) malhaGrafico.getGraphics();
        desenharMalhaQuadriculada(30, 30);
        limparDesenho();
        timer = criarTimer();
        configurarEventosMouse();
    }

    private void desenharMalhaQuadriculada(int numQuadradosHorizontal, int numQuadradosVertical) {
        malhaGraphics.setColor(Color.GRAY);
        malhaGraphics.setStroke(new BasicStroke(1));
        int largura = malhaGrafico.getWidth();
        int altura = malhaGrafico.getHeight();
        int espacamentoX = largura / numQuadradosHorizontal;
        int espacamentoY = altura / numQuadradosVertical;
        for (int i = 0; i <= numQuadradosHorizontal; i++) {
            int x = i * espacamentoX;
            malhaGraphics.drawLine(x, 0, x, altura);
        }
        for (int i = 0; i <= numQuadradosVertical; i++) {
            int y = i * espacamentoY;
            malhaGraphics.drawLine(0, y, largura, y);
        }
    }

    public void limparDesenho() {
        desenhoGraphics.setColor(COR_FUNDO);
        desenhoGraphics.fillRect(0, 0, bufferGrafico.getWidth(), bufferGrafico.getHeight());
        setBackground(COR_FUNDO);
        repaint();
    }

    private Timer criarTimer() {
        return new Timer(INTERVALO_TIMER_MS, e -> {
            if(!estaDentroPainel()) return;
            var circulo = new CirculoGrafico(posicaoMouseAtual, (int)(RAIO_BOLINHA * escalaBolinha), COR_BOLINHA);
            circulo.desenhar(desenhoGraphics);
            desenharCursor(posicaoMouseAtual);
            repaint();
            painelDesenhoListener.adicionarBolinha(posicaoMouseAtual);
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(bufferGrafico, 0, 0, null);
        graphics.drawImage(malhaGrafico, 0, 0, null);
        graphics.drawImage(cursorGrafico, 0, 0, null);
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
            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                mouseMoveu(mouseEvent);
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(criarMouseWheelListener());
    }

    private MouseWheelListener criarMouseWheelListener() {
        return e -> {
            int notches = e.getWheelRotation();
            escalaBolinha -= FATOR_ESCALA_MOUSE * notches;
            if (escalaBolinha < MINIMO_ESCALA_BOLINHA) escalaBolinha = MINIMO_ESCALA_BOLINHA;
            if (escalaBolinha > MAXIMO_ESCALA_BOLINHA) escalaBolinha = MAXIMO_ESCALA_BOLINHA;
            desenharCursor(posicaoMouseAtual);
        };
    }

    private void desenharCursor(Point ponto) {
        cursorGraphics.setColor(COR_FUNDO);
        cursorGraphics.setComposite(AlphaComposite.Clear);
        cursorGraphics.fillRect(0, 0, cursorGrafico.getWidth(), cursorGrafico.getHeight());
        cursorGraphics.setComposite(AlphaComposite.SrcOver);
        new CirculoGrafico(ponto, (int) (RAIO_CURSOR * escalaBolinha), COR_CURSOR, CirculoGrafico.Tipo.OCO).desenhar(cursorGraphics);
        repaint();
    }

    private void mouseMoveu(MouseEvent mouseEvent) {
        posicaoMouseAtual = mouseEvent.getPoint();
        desenharCursor(posicaoMouseAtual);
    }

    private void mouseClicado(MouseEvent mouseEvent) {
        if (!SwingUtilities.isLeftMouseButton(mouseEvent))
            return;
        posicaoMouseAtual = mouseEvent.getPoint();
        if(!estaDentroPainel()) return;
        new CirculoGrafico(posicaoMouseAtual, (int) (RAIO_BOLINHA * escalaBolinha), COR_BOLINHA).desenhar(desenhoGraphics);
        painelDesenhoListener.adicionarBolinha(posicaoMouseAtual);
        timer.start();
    }

    private void mouseArrastado(MouseEvent mouseEvent) {
        posicaoMouseAtual = mouseEvent.getPoint();
    }

    private void mouseSolto(MouseEvent mouseEvent) {
        if (!SwingUtilities.isLeftMouseButton(mouseEvent))
            return;
        timer.stop();
        posicaoMouseAtual = mouseEvent.getPoint();
    }

    private boolean estaDentroPainel() {
        if (posicaoMouseAtual == null) return false;
        return posicaoMouseAtual.x >= 0 && posicaoMouseAtual.y  >= 0
                && posicaoMouseAtual.x <= getWidth() && posicaoMouseAtual.y <= getHeight();
    }
}
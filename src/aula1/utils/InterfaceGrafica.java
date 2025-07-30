package aula1.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class InterfaceGrafica extends JFrame {
    public static final int LARGURA_JANELA = 400;
    public static final int ALTURA_JANELA = 500;
    public static final Color COR_MARROM = new Color(150, 75, 0);
    private static final Color COR_VERDE = Color.GREEN;
    public static final int RAIO_BOLINHA = 30;
    public static final int TOTAL_BOLINHAS = 20;
    private final Point[] bolinhas = new Point[TOTAL_BOLINHAS];
    private int quantidadeBolinhas;
    private final BufferedImage bufferGrafico = new BufferedImage(LARGURA_JANELA, ALTURA_JANELA, BufferedImage.TYPE_INT_ARGB);
    private final JTextArea textAreaOutput;
    private final static Font fonte = new Font("Arial", Font.PLAIN, 20);
    private final JCheckBox checkBoxTreinarModelo;

    public InterfaceGrafica() {
        setTitle("Desenhador de Árvore");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setLocationRelativeTo(null);

        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        textAreaOutput = new JTextArea();
        textAreaOutput.setEditable(false);
        textAreaOutput.setFont(fonte);
        JScrollPane scrollPaneOutput = new JScrollPane(textAreaOutput);
        scrollPaneOutput.setPreferredSize(new Dimension(LARGURA_JANELA, (int) (ALTURA_JANELA * 0.2)));
        contentPane.add(scrollPaneOutput, BorderLayout.NORTH);

        JPanel panelGrafico = new JPanel() {
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                graphics.drawImage(bufferGrafico, 0, 0, null);
            }
        };
        panelGrafico.setBackground(Color.WHITE);
        panelGrafico.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                mouseClique(mouseEvent);
            }
        });
        contentPane.add(panelGrafico, BorderLayout.CENTER);

        checkBoxTreinarModelo = new JCheckBox("Treinar modelo");
        checkBoxTreinarModelo.setFont(fonte);
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelInferior.add(checkBoxTreinarModelo);
        contentPane.add(painelInferior, BorderLayout.SOUTH);


        textAreaOutput.setText("Desenhe uma árvore com 20 bolinhas!");
    }

    public void mouseClique(MouseEvent mouseEvent) {
        if (quantidadeBolinhas >= bolinhas.length) return;

        adicionarBolinha(mouseEvent);
        repaint();

        textAreaOutput.setText("Bolinha " + quantidadeBolinhas + "/" + TOTAL_BOLINHAS + " desenhada.");

        quantidadeBolinhas++;
        if (quantidadeBolinhas == bolinhas.length) {
            var avaliadorDesenho = new AvaliadorDesenho(LARGURA_JANELA, ALTURA_JANELA);
            var resultado = avaliadorDesenho.analisarDesenho(bolinhas);
            mostrarResultado(resultado);
        }
    }

    private void adicionarBolinha(MouseEvent event) {
        int x = event.getX() - RAIO_BOLINHA;
        int y = event.getY() - RAIO_BOLINHA;
        bolinhas[quantidadeBolinhas] = new Point(x, y);

        var graphics = bufferGrafico.getGraphics();
        graphics.setColor(SwingUtilities.isRightMouseButton(event) ? COR_MARROM : COR_VERDE);
        graphics.fillOval(x, y, RAIO_BOLINHA * 2, RAIO_BOLINHA * 2);
    }

    public void mostrarResultado(ResultadoClassificacao resultado) {
        if(checkBoxTreinarModelo.isSelected()) {
            if (resultado == ResultadoClassificacao.ARVORE) {
                textAreaOutput.setText("Resultado - o modelo acertou: uma árvore!");
            } else {
                textAreaOutput.setText("Resultado - o modelo errou: Não é uma árvore!");
            }
        }
        else {
            if (resultado == ResultadoClassificacao.ARVORE) {
                textAreaOutput.setText("Resultado: uma árvore!");
            } else {
                textAreaOutput.setText("Resultado: Não é uma árvore!");
            }
        }
    }
}
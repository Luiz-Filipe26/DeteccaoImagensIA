package deteccao_imagens_ia.view;

import deteccao_imagens_ia.utils.AvaliadorDesenho;
import deteccao_imagens_ia.rede_neural.ResultadoClassificacao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class InterfaceGrafica extends JFrame {
    public static final int LARGURA_JANELA = 600;
    public static final int ALTURA_JANELA = 800;
    private static final Color COR_PRETA = Color.BLACK;
    public static final int RAIO_BOLINHA = 30;
    public static final int TOTAL_BOLINHAS = 20;
    private final Point[] bolinhas = new Point[TOTAL_BOLINHAS];
    private int quantidadeBolinhas;
    private final BufferedImage bufferGrafico = new BufferedImage(LARGURA_JANELA, ALTURA_JANELA, BufferedImage.TYPE_INT_ARGB);
    private final JLabel textoBolinhasRestantes;
    private final static Font fonte = new Font("Arial", Font.PLAIN, 20);
    private final static Font fonteMenor = new Font("Arial", Font.PLAIN, 16);
    private final static Font fonteTitulo = new Font("Arial", Font.BOLD, 24);
    private final JCheckBox checkBoxTreinarModelo;
    private final JRadioButton isBonecoPalito;
    private final JRadioButton isNotBonecoPalito;
    private final ButtonGroup grupoRaddioButton;
    private final JButton limparDesenho;
    private final JPanel painelGrafico;

    public InterfaceGrafica() {
        setTitle("Desenhador de Boneco de Palito");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setLocationRelativeTo(null);

        var painelPrincipal = getContentPane();
        painelPrincipal.setLayout(new BorderLayout());

        // Agrupar título texto de instruções e botões
        var painelSuperior = new JPanel();
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Rede para Reconhecer Desenho");
        titulo.setFont(fonteTitulo);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        painelSuperior.add(titulo);

        var painelInstrucao = new JPanel(new GridLayout(2, 1));
        painelInstrucao.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel textoInstrucao = new JLabel("Desenhe um boneco de palito com 20 bolinhas!");
        textoBolinhasRestantes = new JLabel();
        textoInstrucao.setFont(fonte);
        textoBolinhasRestantes.setFont(fonte);
        painelInstrucao.add(textoInstrucao);
        painelInstrucao.add(textoBolinhasRestantes);
        painelSuperior.add(painelInstrucao, BorderLayout.CENTER);

        // Agrupar botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        JPanel painelLinha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        checkBoxTreinarModelo = new JCheckBox("Treinar modelo");
        checkBoxTreinarModelo.setFont(fonteMenor);
        isBonecoPalito = new JRadioButton("É um boneco de palito");
        isNotBonecoPalito = new JRadioButton("Não é");
        grupoRaddioButton = new ButtonGroup();
        isBonecoPalito.setEnabled(false);
        isNotBonecoPalito.setEnabled(false);
        isBonecoPalito.setFont(fonteMenor);
        isNotBonecoPalito.setFont(fonteMenor);
        grupoRaddioButton.add(isBonecoPalito);
        grupoRaddioButton.add(isNotBonecoPalito);
        painelLinha1.add(checkBoxTreinarModelo);
        painelLinha1.add(isBonecoPalito);
        painelLinha1.add(isNotBonecoPalito);
        JPanel painelLinha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        limparDesenho = new JButton("Limpar Desenho");
        limparDesenho.setFont(fonteMenor);
        painelLinha2.add(limparDesenho);
        painelBotoes.add(painelLinha1);
        painelBotoes.add(painelLinha2);

        painelSuperior.add(painelBotoes);
        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);

        painelGrafico = new JPanel() {
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                graphics.drawImage(bufferGrafico, 0, 0, null);
            }
        };
        painelGrafico.setBackground(Color.WHITE);
        painelPrincipal.add(painelGrafico, BorderLayout.CENTER);

        configurarEventosListeners();
    }

    private void configurarEventosListeners() {

        // Desenhar uma bolinha
        painelGrafico.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                mouseClique(mouseEvent);
            }
        });

        // Treinar a rede
        checkBoxTreinarModelo.addActionListener(e -> {
            boolean isSelecionado = checkBoxTreinarModelo.isSelected();

            if (isSelecionado && quantidadeBolinhas == TOTAL_BOLINHAS) {
                isBonecoPalito.setEnabled(true);
                isNotBonecoPalito.setEnabled(true);
                // trienar rede ...
            } else {
                grupoRaddioButton.clearSelection();
                isBonecoPalito.setEnabled(false);
                isNotBonecoPalito.setEnabled(false);
            }
        });

        // Limpar tela e começar um novo desenho
        limparDesenho.addActionListener(e -> limparDesenho());
    }

    public void mouseClique(MouseEvent mouseEvent) {
        if (quantidadeBolinhas >= bolinhas.length) return;

        adicionarBolinha(mouseEvent);
        repaint();

        textoBolinhasRestantes.setText((quantidadeBolinhas + 1) + " restantes / " + TOTAL_BOLINHAS + " desenhada");

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
        graphics.setColor(COR_PRETA);
        graphics.fillOval(x, y, RAIO_BOLINHA * 2, RAIO_BOLINHA * 2);
    }

    public void mostrarResultado(ResultadoClassificacao resultado) {
        var ehBoneco = resultado == ResultadoClassificacao.DESENHO_ESPERADO;
        var texto = checkBoxTreinarModelo.isSelected()
                ? (ehBoneco ? "Resultado - o modelo acertou: um boneco de palito!" : "Resultado - o modelo errou: Não é um boneco de palito!")
                : (ehBoneco ? "Resultado: um boneco de palito!" : "Resultado: Não é um boneco de palito!");
        textoBolinhasRestantes.setText(texto);
    }

    private void limparDesenho() {
        quantidadeBolinhas = 0;
        Arrays.fill(bolinhas, null);

        Graphics buffer = bufferGrafico.getGraphics();
        buffer.setColor(Color.WHITE);
        buffer.fillRect(0, 0, LARGURA_JANELA, ALTURA_JANELA);
        buffer.dispose();

        textoBolinhasRestantes.setText("");
        grupoRaddioButton.clearSelection();

        painelGrafico.repaint();
    }

}
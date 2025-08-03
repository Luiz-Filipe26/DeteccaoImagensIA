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
    private static final int LARGURA_JANELA = 600;
    private static final int ALTURA_JANELA = 800;
    private static final Color COR_PRETA = Color.BLACK;
    private static final int RAIO_BOLINHA = 30;
    private static final int TOTAL_BOLINHAS = 20;
    private final Point[] bolinhas = new Point[TOTAL_BOLINHAS];
    private int quantidadeBolinhas;

    private final static Font fonte = new Font("Arial", Font.PLAIN, 20);
    private final static Font fonteMenor = new Font("Arial", Font.PLAIN, 16);
    private final static Font fonteTitulo = new Font("Arial", Font.BOLD, 24);

    private JLabel textoBolinhasRestantes;
    private JCheckBox checkBoxTreinarModelo;
    private JButton limparDesenho;
    private PainelDesenho painelGrafico;

    public InterfaceGrafica() {
        setTitle("Desenhador de Boneco de Palito");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setLocationRelativeTo(null);
        iniciarComponentes();
        configurarEventos();
    }

    private void iniciarComponentes() {
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
        painelLinha1.add(checkBoxTreinarModelo);
        JPanel painelLinha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        limparDesenho = new JButton("Limpar Desenho");
        limparDesenho.setFont(fonteMenor);
        painelLinha2.add(limparDesenho);
        painelBotoes.add(painelLinha1);
        painelBotoes.add(painelLinha2);

        painelSuperior.add(painelBotoes);
        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);

        painelGrafico = new PainelDesenho(LARGURA_JANELA, ALTURA_JANELA);
        painelPrincipal.add(painelGrafico, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        painelGrafico.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                mouseClique(mouseEvent);
            }
        });
        limparDesenho.addActionListener(e -> limparDesenho());
    }

    public void mouseClique(MouseEvent mouseEvent) {
        if (quantidadeBolinhas >= bolinhas.length) return;

        adicionarBolinha(mouseEvent);

        quantidadeBolinhas++;
        textoBolinhasRestantes.setText(quantidadeBolinhas + " / " + TOTAL_BOLINHAS + " desenhadas");

        if (quantidadeBolinhas == bolinhas.length) {
            var avaliadorDesenho = new AvaliadorDesenho();
            var resultado = avaliadorDesenho.analisarDesenho(bolinhas);
            mostrarResultado(resultado);
        }
    }

    private void adicionarBolinha(MouseEvent event) {
        int x = event.getX() - RAIO_BOLINHA;
        int y = event.getY() - RAIO_BOLINHA;
        bolinhas[quantidadeBolinhas] = new Point(x, y);

        var graphics = painelGrafico.getDesenhoGraphics();
        graphics.setColor(COR_PRETA);
        graphics.fillOval(x, y, RAIO_BOLINHA * 2, RAIO_BOLINHA * 2);
        repaint();
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

        var graphics = painelGrafico.getDesenhoGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, LARGURA_JANELA, ALTURA_JANELA);

        textoBolinhasRestantes.setText("");

        painelGrafico.repaint();
    }

}
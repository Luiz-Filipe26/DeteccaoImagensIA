package deteccao_imagens_ia.view;

import deteccao_imagens_ia.populador_exemplos_desenho.BaseTreinamento;
import deteccao_imagens_ia.populador_exemplos_desenho.ClassificacaoDesenho;
import deteccao_imagens_ia.populador_exemplos_desenho.DesenhoClassificado;
import deteccao_imagens_ia.populador_exemplos_desenho.XMLEditor;
import deteccao_imagens_ia.utils.AvaliadorDesenho;
import deteccao_imagens_ia.rede_neural.ResultadoClassificacao;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class InterfaceGrafica extends JFrame implements PainelDesenhoListener {
    private static final int LARGURA_JANELA = 600;
    private static final int ALTURA_JANELA = 800;
    private static final int MINIMO_BOLINHAS = 20;

    private final static Font fonte = new Font("Arial", Font.PLAIN, 20);
    private final static Font fonteMenor = new Font("Arial", Font.PLAIN, 16);
    private final static Font fonteTitulo = new Font("Arial", Font.BOLD, 24);

    private JLabel textoBolinhasRestantes;
    private JButton limparDesenho;
    private JButton avaliarDesenho;
    private PainelDesenho painelGrafico;
    private JButton salvarDesenho;
    private JButton carregarExemplos;
    private JCheckBox checkBoxEhBonecoPalito;

    private final List<Point> bolinhas = new ArrayList<>();
    private final BaseTreinamento baseTreinamento = new BaseTreinamento();

    public InterfaceGrafica() {
        configurarJFrame();
        iniciarComponentes();
        configurarEventos();
    }

    @Override
    public void adicionarBolinha(Point bolinha) {
        bolinhas.add(bolinha);
        textoBolinhasRestantes.setText(bolinhas.size() + " desenhadas");
    }

    private void configurarJFrame() {
        setTitle("Desenhador de Boneco de Palito");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setLocationRelativeTo(null);
    }

    private void iniciarComponentes() {
        var painelPrincipal = getContentPane();
        painelPrincipal.setLayout(new BorderLayout());
        painelPrincipal.add(criarPainelSuperior(), BorderLayout.NORTH);
        painelGrafico = new PainelDesenho(this, new Dimension(LARGURA_JANELA, ALTURA_JANELA));
        painelPrincipal.add(painelGrafico, BorderLayout.CENTER);
    }

    private JPanel criarPainelSuperior() {
        var painelSuperior = new JPanel();
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));
        painelSuperior.add(criarTitulo());
        painelSuperior.add(criarPainelInstrucao());
        painelSuperior.add(criarPainelBotoes());
        return painelSuperior;
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("Rede para Reconhecer Desenho");
        titulo.setFont(fonteTitulo);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return titulo;
    }

    private JPanel criarPainelInstrucao() {
        var painelInstrucao = new JPanel(new GridLayout(2, 1));
        painelInstrucao.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var textoInstrucao = new JLabel("Desenhe um boneco de palito com, no mínimo, " + MINIMO_BOLINHAS + " bolinhas!");
        textoInstrucao.setFont(fonte);

        textoBolinhasRestantes = new JLabel();
        textoBolinhasRestantes.setFont(fonte);

        painelInstrucao.add(textoInstrucao);
        painelInstrucao.add(textoBolinhasRestantes);

        return painelInstrucao;
    }

    private JPanel criarPainelBotoes() {
        var painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        var painelLinha2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        var painelLinha3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        limparDesenho = new JButton("Limpar Desenho");
        limparDesenho.setFont(fonteMenor);
        avaliarDesenho = new JButton("Avaliar Desenho");
        avaliarDesenho.setFont(fonteMenor);
        checkBoxEhBonecoPalito = new JCheckBox("É um Boneco de Palito");
        checkBoxEhBonecoPalito.setFont(fonteMenor);
        salvarDesenho = new JButton("Salvar Desenho");
        salvarDesenho.setFont(fonteMenor);
        carregarExemplos = new JButton("Carregar Exemplos");
        carregarExemplos.setFont(fonteMenor);
        var treinarComExemplos = new JButton("Treinar com Todos Exemplos");
        treinarComExemplos.setFont(fonteMenor);
        painelLinha2.add(avaliarDesenho);
        painelLinha2.add(limparDesenho);
        painelLinha2.add(checkBoxEhBonecoPalito);
        painelLinha3.add(salvarDesenho);
        painelLinha3.add(carregarExemplos);
        painelLinha3.add(treinarComExemplos);
        painelBotoes.add(painelLinha2);
        painelBotoes.add(painelLinha3);
        return painelBotoes;
    }

    private void configurarEventos() {
        avaliarDesenho.addActionListener(event -> avaliarDesenhoClicado());
        limparDesenho.addActionListener(event -> limparDesenho());
        carregarExemplos.addActionListener(event -> carregarExemplos());
        salvarDesenho.addActionListener(event -> salvarDesenho());
    }

    public void avaliarDesenhoClicado() {
        if (bolinhas.size() < MINIMO_BOLINHAS) {
            textoBolinhasRestantes.setText(textoBolinhasRestantes.getText() + " - Preencher quantidade mínima de bolinhas!");
            return;
        }
        var avaliadorDesenho = new AvaliadorDesenho();
        var resultado = avaliadorDesenho.analisarDesenho(bolinhas, true);
        mostrarResultado(resultado);
    }

    private void mostrarResultado(ResultadoClassificacao resultado) {
        var ehBoneco = resultado == ResultadoClassificacao.DESENHO_ESPERADO;
        var texto = true
                ? (ehBoneco ? "Resultado - o modelo acertou: um boneco de palito!" : "Resultado - o modelo errou: Não é um boneco de palito!")
                : (ehBoneco ? "Resultado: um boneco de palito!" : "Resultado: Não é um boneco de palito!");
        textoBolinhasRestantes.setText(texto);
    }

    private void limparDesenho() {
        bolinhas.clear();
        textoBolinhasRestantes.setText("");
        painelGrafico.limparDesenho();
    }

    private void carregarExemplos() {
        var arquivoOpctional = solicitarArquivo("Selecione o arquivo de exemplos para carregar");
        arquivoOpctional.ifPresentOrElse(arquivo -> {
            try {
                baseTreinamento.carregarExemplos(arquivo);
                JOptionPane.showMessageDialog(this, "Exemplos de desenho carregados com sucesso!");
            } catch (XMLEditor.FalhaXML falhaXML) {
                var message = "Erro ao carregar exemplos: " + falhaXML.getMessage();
                JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }, () -> JOptionPane.showMessageDialog(this, "Selecionou nenhum arquivo!"));
    }

    private Optional<File> solicitarArquivo(String titulo) {
        var fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(titulo);
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION)
            return Optional.empty();
        return Optional.ofNullable(fileChooser.getSelectedFile());
    }

    private void salvarDesenho() {
        var novoDesenho = gerarDesenhoClassificado();

        var message = "É necessário desenhar ao menos " + MINIMO_BOLINHAS + " bolinhas para salvar o exemplo.";
        JOptionPane.showMessageDialog(this, message, "Desenho Incompleto", JOptionPane.WARNING_MESSAGE);

        var arquivoOpcional = solicitarArquivoParaSalvar("Salvar arquivo de exemplos", ".xml");

        arquivoOpcional.ifPresentOrElse(arquivo -> {
            try {
                baseTreinamento.salvarExemplos(arquivo);
                message = "Desenho adicionado e base de exemplos salva com sucesso!";
                JOptionPane.showMessageDialog(this, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (XMLEditor.FalhaXML e) {
                message = "Erro ao salvar o arquivo de exemplos: " + e.getMessage();
                JOptionPane.showMessageDialog(this, message, "Erro de Salvamento", JOptionPane.ERROR_MESSAGE);
            }
        }, () -> JOptionPane.showMessageDialog(this, "Operação de salvamento cancelada."));
    }

    private Optional<DesenhoClassificado> gerarDesenhoClassificado() {
        if (bolinhas.size() < MINIMO_BOLINHAS) return Optional.empty();
        var classificacao = checkBoxEhBonecoPalito.isSelected() ? ClassificacaoDesenho.BONECO_DE_PALITO : ClassificacaoDesenho.OUTROS_DESENHOS;
        var novoDesenho = new DesenhoClassificado(classificacao, new ArrayList<>(bolinhas));
        baseTreinamento.adicionarDesenho(novoDesenho);
        return novoDesenho;
    }

    private Optional<File> solicitarArquivoParaSalvar(String titulo, String extensao) {
        var fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(titulo);
        int selecaoUsuario = fileChooser.showSaveDialog(this);
        if (selecaoUsuario != JFileChooser.APPROVE_OPTION)
            return Optional.empty();
        var arquivoParaSalvar = fileChooser.getSelectedFile();
        String caminhoArquivo = arquivoParaSalvar.getAbsolutePath();
        if (!caminhoArquivo.toLowerCase().endsWith(extensao.toLowerCase()))
            arquivoParaSalvar = new File(caminhoArquivo + extensao.toLowerCase());
        return Optional.of(arquivoParaSalvar);
    }
}
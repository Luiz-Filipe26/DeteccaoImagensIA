package deteccao_imagens_ia.view;

import deteccao_imagens_ia.populador_exemplos_desenho.BaseTreinamento;
import deteccao_imagens_ia.populador_exemplos_desenho.ClassificacaoDesenho;
import deteccao_imagens_ia.populador_exemplos_desenho.DesenhoClassificado;
import deteccao_imagens_ia.rede_neural.ResultadoClassificacao;
import deteccao_imagens_ia.utils.AvaliadorDesenho;
import deteccao_imagens_ia.utils.XMLEditor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InterfaceGrafica extends JFrame implements PainelDesenhoListener {
    private static final int LARGURA_JANELA = 600;
    private static final int ALTURA_JANELA = 800;
    private static final int MINIMO_BOLINHAS = 20;

    private final static Font fonte = new Font("Arial", Font.PLAIN, 20);
    private final static Font fonteMenor = new Font("Arial", Font.PLAIN, 16);
    private final static Font fonteTitulo = new Font("Arial", Font.BOLD, 24);

    private PainelDesenho painelGrafico;
    private JLabel textoBolinhasRestantes;
    private JLabel labelStatus;
    private JButton limparDesenho;
    private JButton avaliarDesenho;
    private JButton adicionarDesenho;
    private JButton salvarBase;
    private JButton carregarExemplos;
    private JButton treinarComExemplos;
    private JCheckBox checkBoxEhBonecoPalito;

    private final List<Point> bolinhas = new ArrayList<>();
    private final BaseTreinamento baseTreinamento = new BaseTreinamento();
    private final AvaliadorDesenho avaliadorDesenho = new AvaliadorDesenho();

    public InterfaceGrafica() {
        configurarJFrame();
        iniciarComponentes();
        configurarEventos();
    }

    @Override
    public void adicionarBolinha(Point bolinha) {
        bolinhas.add(bolinha);
        textoBolinhasRestantes.setText(bolinhas.size() + " desenhadas");
        limparMensagemInformativa();
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
        labelStatus = new JLabel("Desenhando boneco.", SwingConstants.CENTER);
        labelStatus.setFont(fonteMenor);
        labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelStatus.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        painelSuperior.add(labelStatus, BorderLayout.SOUTH);
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
        var painelInstrucao = new JPanel();
        painelInstrucao.setLayout(new BoxLayout(painelInstrucao, BoxLayout.Y_AXIS));
        painelInstrucao.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var textoInstrucao = new JLabel("Desenhe um boneco de palito com, no mínimo, " + MINIMO_BOLINHAS + " bolinhas!");
        textoInstrucao.setFont(fonte);
        textoInstrucao.setAlignmentX(Component.CENTER_ALIGNMENT);

        textoBolinhasRestantes = new JLabel();
        textoBolinhasRestantes.setFont(fonte);
        textoBolinhasRestantes.setAlignmentX(Component.CENTER_ALIGNMENT);

        checkBoxEhBonecoPalito = new JCheckBox("É um Boneco de Palito");
        checkBoxEhBonecoPalito.setSelected(true);
        checkBoxEhBonecoPalito.setFont(fonteMenor);
        checkBoxEhBonecoPalito.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelInstrucao.add(textoInstrucao);
        painelInstrucao.add(Box.createVerticalStrut(5)); // espaço vertical entre elementos
        painelInstrucao.add(textoBolinhasRestantes);
        painelInstrucao.add(Box.createVerticalStrut(5));
        painelInstrucao.add(checkBoxEhBonecoPalito);

        return painelInstrucao;
    }

    private JPanel criarPainelBotoes() {
        var painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        limparDesenho = new JButton("Limpar Desenho");
        limparDesenho.setFont(fonteMenor);
        avaliarDesenho = new JButton("Avaliar Desenho");
        avaliarDesenho.setFont(fonteMenor);
        adicionarDesenho = new JButton("Adicionar Desenho");
        adicionarDesenho.setFont(fonteMenor);
        salvarBase = new JButton("Salvar Exemplos");
        salvarBase.setFont(fonteMenor);
        carregarExemplos = new JButton("Carregar Exemplos");
        carregarExemplos.setFont(fonteMenor);
        treinarComExemplos = new JButton("Treinar com Todos Exemplos");
        treinarComExemplos.setFont(fonteMenor);
        var painelLinha2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // hgap=5, vgap=5
        var painelLinha3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        var painelLinha4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        painelLinha2.add(adicionarDesenho);
        painelLinha2.add(limparDesenho);
        painelLinha3.add(avaliarDesenho);
        painelLinha3.add(treinarComExemplos);
        painelLinha4.add(carregarExemplos);
        painelLinha4.add(salvarBase);
        painelBotoes.add(painelLinha2);
        painelBotoes.add(painelLinha3);
        painelBotoes.add(painelLinha4);
        return painelBotoes;
    }

    private void configurarEventos() {
        avaliarDesenho.addActionListener(event -> avaliarDesenhoClicado());
        limparDesenho.addActionListener(event -> limparDesenho());
        carregarExemplos.addActionListener(event -> carregarExemplos());
        adicionarDesenho.addActionListener(event -> adicionarDesenho());
        salvarBase.addActionListener(event -> salvarBase());
        treinarComExemplos.addActionListener(event -> treinarComExemplos());
    }

    public void avaliarDesenhoClicado() {
        if (bolinhas.size() < MINIMO_BOLINHAS) {
            mostrarMensagemInformativa("Preencher quantidade mínima de bolinhas!");
            return;
        }
        var resultado = avaliadorDesenho.analisarDesenho(bolinhas);
        mostrarResultado(resultado);
    }

    private void mostrarResultado(ResultadoClassificacao resultado) {
        var ehBoneco = resultado == ResultadoClassificacao.DESENHO_ESPERADO;
        var texto = checkBoxEhBonecoPalito.isSelected()
                ? (ehBoneco ? "Resultado - o modelo acertou: um boneco de palito!" : "Resultado - o modelo errou: Não é um boneco de palito!")
                : (ehBoneco ? "Resultado: um boneco de palito!" : "Resultado: Não é um boneco de palito!");
        mostrarMensagemInformativa(texto);
    }

    private void limparDesenho() {
        bolinhas.clear();
        textoBolinhasRestantes.setText("");
        limparMensagemInformativa();
        painelGrafico.limparDesenho();
    }

    private void carregarExemplos() {
        var arquivoOpctional = solicitarArquivo("Selecione o arquivo de exemplos para carregar");
        arquivoOpctional.ifPresentOrElse(arquivo -> {
            try {
                baseTreinamento.carregarExemplos(arquivo);
                mostrarMensagemInformativa("Exemplos de desenho carregados com sucesso!");
            } catch (XMLEditor.FalhaXML falhaXML) {
                mostrarPopUpErro("Erro ao carregar exemplos: " + falhaXML.getMessage());
            }
        }, () -> mostrarMensagemInformativa("Nenhum arquivo selecionado."));
    }

    private Optional<File> solicitarArquivo(String titulo) {
        var fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(titulo);
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION)
            return Optional.empty();
        return Optional.ofNullable(fileChooser.getSelectedFile());
    }

    private void adicionarDesenho() {
        var novoDesenhoOpctional = gerarDesenhoClassificado();
        novoDesenhoOpctional.ifPresentOrElse(novoDesenho -> {
                    baseTreinamento.adicionarDesenho(novoDesenho);
                    int totalExemplos = baseTreinamento.getDesenhosClassificados().size();
                    mostrarMensagemInformativa("Exemplo adicionado com sucesso! Total na sessão: " + totalExemplos);
                },
                () -> mostrarPopUpErro("É necessário desenhar ao menos " + MINIMO_BOLINHAS + " bolinhas para salvar o exemplo."));
    }

    private void salvarBase() {
        carregarBaseCasoNecessario();
        var arquivoOpcional = solicitarArquivoParaSalvar("Salvar arquivo de exemplos", ".xml");
        arquivoOpcional.ifPresentOrElse(arquivo -> {
            try {
                baseTreinamento.salvarExemplos(arquivo);
                mostrarMensagemInformativa("Base de exemplos salva com sucesso!");
            } catch (XMLEditor.FalhaXML e) {
                mostrarPopUpErro("Erro ao salvar o arquivo de exemplos: " + e.getMessage());
            }
        }, () -> mostrarMensagemInformativa("Operação de salvamento cancelada."));
    }

    private void carregarBaseCasoNecessario() {
        if (!baseTreinamento.isExemplosCarregados()) {
            int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja carregar a base de treinamento antes?");
            if (confirmacao == JOptionPane.YES_OPTION)
                carregarExemplos();
        }
    }

    private Optional<DesenhoClassificado> gerarDesenhoClassificado() {
        if (bolinhas.size() < MINIMO_BOLINHAS) return Optional.empty();
        var classificacao = checkBoxEhBonecoPalito.isSelected() ? ClassificacaoDesenho.BONECO_DE_PALITO : ClassificacaoDesenho.OUTROS_DESENHOS;
        var novoDesenho = new DesenhoClassificado(classificacao, new ArrayList<>(bolinhas));
        return Optional.of(novoDesenho);
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

    private void treinarComExemplos() {
        avaliadorDesenho.treinarRede(baseTreinamento);
        mostrarMensagemInformativa("Treinado com os exemplos!");
    }

    private void mostrarMensagemInformativa(String mensagem) {
        labelStatus.setText(mensagem);
    }

    private void limparMensagemInformativa() {
        labelStatus.setText("-----");
    }

    private void mostrarPopUpErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

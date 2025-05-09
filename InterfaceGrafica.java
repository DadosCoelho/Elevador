import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InterfaceGrafica extends JFrame {
    private Predio predio;
    private Simulador simulador;
    private JPanel configPanel;
    private JPanel simulationPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea pessoasTextArea;
    private JPanel statsPanel;
    private JPanel controlPanel;
    private JPanel predioPanel;
    private JScrollPane predioScrollPane;
    private Color[] cores = {
            new Color(65, 105, 225),   // Royal Blue
            new Color(220, 20, 60),    // Crimson
            new Color(50, 205, 50),    // Lime Green
            new Color(255, 165, 0),    // Orange
            new Color(138, 43, 226),   // Blue Violet
            new Color(0, 139, 139)     // Dark Cyan
    };

    // Painéis de configuração
    private JTextField andaresField;
    private JTextField elevadoresField;
    private JTextField capacidadeField;
    private JTextField velocidadeField;
    private JTextField tempoPicoField;
    private JTextField tempoForaPicoField;
    private JComboBox<String> heuristicaCombo;
    private JComboBox<String> painelCombo;
    private JTextField pessoasField;

    // Controle de simulação
    private JButton pauseButton;
    private JButton continueButton;
    public JButton restartButton; // Botão de reiniciar
    public JButton backToConfigButton; // Botão de voltar para a configuração
    private JSlider velocidadeSlider;
    private JLabel statusLabel;
    private JLabel velocidadeLabel;

    public InterfaceGrafica() {
        setTitle("Simulador de Elevadores");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicia maximizado

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        configPanel = criarPainelConfiguracao();

        // Painel de simulação com layout melhorado
        simulationPanel = new JPanel(new BorderLayout(10, 10));
        simulationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel do prédio com desenho avançado
        predioPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (simulador != null && predio != null) {
                    renderizarPredio((Graphics2D) g);
                }
            }
        };
        predioPanel.setBackground(new Color(240, 240, 240));
        predioPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Adicionar o predioPanel a um JScrollPane
        predioScrollPane = new JScrollPane(predioPanel);
        predioScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        predioScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        predioScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Painel de estatísticas com melhor aparência
        statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120)),
                "Estatísticas", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(50, 50, 50)));

        // Texto de pessoas mais organizado
        pessoasTextArea = new JTextArea();
        pessoasTextArea.setEditable(false);
        pessoasTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pessoasTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(pessoasTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120)),
                "Pessoas", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(50, 50, 50)));
        // Criar um JPanel para envolver o JScrollPane e definir a largura fixa
        JPanel pessoasPanel = new JPanel(new BorderLayout());
        pessoasPanel.setPreferredSize(new Dimension(300, 600)); // Largura fixa de 300, altura de 600
        pessoasPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel de controle de simulação
        controlPanel = criarPainelControle();

        // Montagem dos painéis
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setPreferredSize(new Dimension(700, 600));
        westPanel.add(statsPanel, BorderLayout.NORTH);
        westPanel.add(predioScrollPane, BorderLayout.CENTER);
        westPanel.add(controlPanel, BorderLayout.SOUTH);

        simulationPanel.add(westPanel, BorderLayout.CENTER);
        simulationPanel.add(scrollPane, BorderLayout.EAST);

        mainPanel.add(configPanel, "Config");
        mainPanel.add(simulationPanel, "Simulation");
        add(mainPanel);

        cardLayout.show(mainPanel, "Config");
    }

    private JPanel criarPainelConfiguracao() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título do painel de configuração
        JLabel titleLabel = new JLabel("Configuração da Simulação", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Painel de campos configuração
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {
                "Quantidade de Andares (mín. 5):",
                "Quantidade de Elevadores (mín. 1):",
                "Capacidade Máxima por Elevador:",
                "Velocidade de Simulação (ms):",
                "Tempo de Viagem em Pico (min):",
                "Tempo de Viagem Fora de Pico (min):",
                "Heurística:",
                "Tipo de Painel:",
                "Quantidade de Pessoas:"
        };

        // Campos de texto
        andaresField = new JTextField("12", 10);
        elevadoresField = new JTextField("7", 10);
        capacidadeField = new JTextField("8", 10);
        velocidadeField = new JTextField("1000", 10);
        tempoPicoField = new JTextField("2", 10);
        tempoForaPicoField = new JTextField("1", 10);
        pessoasField = new JTextField("30", 10);

        // Estilização dos campos
        JTextField[] fields = {andaresField, elevadoresField, capacidadeField,
                velocidadeField, tempoPicoField, tempoForaPicoField, pessoasField};
        for (JTextField field : fields) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    field.getBorder(),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        }

        // Comboboxes com estilos
        String[] heuristicas = {"1 - Ordem de Chegada", "2 - Otimização de Tempo", "3 - Otimização de Energia"};
        heuristicaCombo = new JComboBox<>(heuristicas);
        heuristicaCombo.setSelectedIndex(0);

        String[] paineis = {"UNICO_BOTAO", "DOIS_BOTOES", "PAINEL_NUMERICO"};
        painelCombo = new JComboBox<>(paineis);
        painelCombo.setSelectedIndex(0);

        // Adicionando labels e campos ao painel
        int row = 0;
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 13));

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.4;
            fieldsPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.6;
            if (i == 6) {
                fieldsPanel.add(heuristicaCombo, gbc);
            } else if (i == 7) {
                fieldsPanel.add(painelCombo, gbc);
            } else if (i == 8) {
                fieldsPanel.add(pessoasField, gbc);
            } else {
                fieldsPanel.add(fields[i], gbc);
            }
            row++;
        }

        // Botão de iniciar com estilo
        JButton startButton = criarBotao("Iniciar Simulação", new Color(60, 130, 200));
        startButton.setForeground(new Color(60, 130, 200));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validarParametros()) {
                    iniciarSimulacao();
                    cardLayout.show(mainPanel, "Simulation");
                }
            }
        });

        // Painel para o botão
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        buttonPanel.add(startButton);

        // Montagem do painel de configuração
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelControle() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Botão de pausa
        pauseButton = criarBotao("Pausar", new Color(240, 240, 240));
        pauseButton.setForeground(new Color(60, 130, 200));
        pauseButton.setVisible(false); // Inicialmente invisível
        pauseButton.addActionListener(e -> {
            if (simulador != null) {
                simulador.pausar();
                pauseButton.setVisible(false);
                continueButton.setText("Continuar");
                continueButton.setVisible(true);
                restartButton.setVisible(true);
                backToConfigButton.setVisible(true);
                updateControlButtons(false);
                // Tornar invisível
                velocidadeLabel.setVisible(false);
                velocidadeSlider.setVisible(false);
            }
        });

        // Botão de continuar (inicialmente "Iniciar")
        continueButton = criarBotao("Iniciar", new Color(240, 240, 240));
        continueButton.setForeground(new Color(60, 130, 200));
        continueButton.addActionListener(e -> {
            if (simulador != null) {
                if (continueButton.getText().equals("Iniciar")) {
                    simulador.iniciar();
                    continueButton.setVisible(false);
                    pauseButton.setVisible(true);
                    updateControlButtons(true);
                    // Tornar visível
                    velocidadeLabel.setVisible(true);
                    velocidadeSlider.setVisible(true);
                } else {
                    simulador.continuar();
                    continueButton.setVisible(false);
                    pauseButton.setVisible(true);
                    restartButton.setVisible(false);
                    backToConfigButton.setVisible(false);
                    updateControlButtons(true);
                    // Manter visível
                    velocidadeLabel.setVisible(true);
                    velocidadeSlider.setVisible(true);
                }
            }
        });

        // Botão de reiniciar
        restartButton = criarBotao("Reiniciar Simulação", new Color(240, 240, 240));
        restartButton.setForeground(new Color(60, 130, 200));
        restartButton.setVisible(false); // Inicialmente invisível
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int resposta = JOptionPane.showConfirmDialog(InterfaceGrafica.this,
                        "Deseja realmente reiniciar a simulação?",
                        "Reiniciar Simulação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (resposta == JOptionPane.YES_OPTION) {
                    reiniciarSimulacao();
                    pauseButton.setVisible(false);
                    continueButton.setText("Iniciar");
                    continueButton.setVisible(true);
                    restartButton.setVisible(false);
                    backToConfigButton.setVisible(false);
                    updateControlButtons(false);
                    // Tornar invisível
                    velocidadeLabel.setVisible(false);
                    velocidadeSlider.setVisible(false);
                }
            }
        });

        // Botão de voltar para a configuração
        backToConfigButton = criarBotao("Voltar para Configuração", new Color(240, 240, 240));
        backToConfigButton.setForeground(new Color(60, 130, 200));
        backToConfigButton.setVisible(false);
        backToConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int resposta = JOptionPane.showConfirmDialog(InterfaceGrafica.this,
                        "Deseja realmente voltar para a configuração? A simulação atual será perdida.",
                        "Voltar para Configuração",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (resposta == JOptionPane.YES_OPTION) {
                    cardLayout.show(mainPanel, "Config");
                    reiniciarConfiguracao(); // Reiniciar a configuração ao voltar
                    pauseButton.setVisible(false);
                    continueButton.setText("Iniciar");
                    continueButton.setVisible(true);
                    restartButton.setVisible(false);
                    backToConfigButton.setVisible(false);
                    updateControlButtons(false);
                    // Tornar invisível
                    velocidadeLabel.setVisible(false);
                    velocidadeSlider.setVisible(false);
                }
            }
        });

        // Slider de velocidade
        velocidadeLabel = new JLabel("Velocidade: ");
        velocidadeSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, 1000);
        velocidadeSlider.setInverted(true); // Valores menores = mais rápido
        velocidadeSlider.setPreferredSize(new Dimension(150, 30));
        velocidadeSlider.addChangeListener(e -> {
            if (simulador != null && !velocidadeSlider.getValueIsAdjusting()) {
                int novaVelocidade = velocidadeSlider.getValue();
                simulador.pausar();
                simulador.setVelocidadeEmMsInterno(novaVelocidade);
                simulador.continuar();
            }
        });

        // Inicialmente invisível
        velocidadeLabel.setVisible(false);
        velocidadeSlider.setVisible(false);

        // Label de status
        statusLabel = new JLabel("Pronto para iniciar");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        // Adicionar componentes ao painel
        panel.add(pauseButton);
        panel.add(continueButton);
        panel.add(restartButton); // Adicione o botão ao painel de controle
        panel.add(backToConfigButton); // Adicione o botão de voltar
        panel.add(new JSeparator(JSeparator.VERTICAL));
        panel.add(velocidadeLabel);
        panel.add(velocidadeSlider);
        panel.add(new JSeparator(JSeparator.VERTICAL));
        panel.add(statusLabel);

        return panel;
    }

    // Método para criar botões padronizados
    private JButton criarBotao(String texto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE); // Cor da letra alterada para branco
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return botao;
    }

    public void updateControlButtons(boolean isRunning) {
        pauseButton.setEnabled(isRunning);
        continueButton.setEnabled(!isRunning);
        statusLabel.setText(isRunning ? "Simulação em andamento" : "Simulação pausada");
    }

    private boolean validarParametros() {
        try {
            int andares = Integer.parseInt(andaresField.getText());
            int elevadores = Integer.parseInt(elevadoresField.getText());
            int capacidade = Integer.parseInt(capacidadeField.getText());
            int velocidade = Integer.parseInt(velocidadeField.getText());
            int tempoPico = Integer.parseInt(tempoPicoField.getText());
            int tempoForaPico = Integer.parseInt(tempoForaPicoField.getText());
            int pessoas = Integer.parseInt(pessoasField.getText());

            if (andares < 5) {
                mostrarErro("Quantidade de andares deve ser no mínimo 5.");
                return false;
            }
            if (elevadores < 1) {
                mostrarErro("Quantidade de elevadores deve ser no mínimo 1.");
                return false;
            }
            if (capacidade < 1) {
                mostrarErro("Capacidade máxima deve ser positiva.");
                return false;
            }
            if (velocidade < 1) {
                mostrarErro("Velocidade de simulação deve ser positiva.");
                return false;
            }
            if (tempoPico < 1) {
                mostrarErro("Tempo de viagem em pico deve ser positivo.");
                return false;
            }
            if (tempoForaPico < 1) {
                mostrarErro("Tempo de viagem fora de pico deve ser positivo.");
                return false;
            }
            if (pessoas < 1) {
                mostrarErro("Quantidade de pessoas deve ser positiva.");
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            mostrarErro("Por favor, insira valores numéricos válidos.");
            return false;
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro de Validação",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void iniciarSimulacao() {
        int andares = Integer.parseInt(andaresField.getText());
        int elevadores = Integer.parseInt(elevadoresField.getText());
        int capacidade = Integer.parseInt(capacidadeField.getText());
        int velocidade = Integer.parseInt(velocidadeField.getText());
        int tempoPico = Integer.parseInt(tempoPicoField.getText());
        int tempoForaPico = Integer.parseInt(tempoForaPicoField.getText());
        int heuristica = heuristicaCombo.getSelectedIndex() + 1;
        TipoPainel tipoPainel = TipoPainel.valueOf((String) painelCombo.getSelectedItem());
        int quantidadePessoas = Integer.parseInt(pessoasField.getText());

        simulador = new Simulador(andares, elevadores, velocidade, capacidade, tempoPico, tempoForaPico, heuristica, tipoPainel);
        simulador.setGui(this);
        predio = simulador.getPredio();

        // Configurar o slider com o valor inicial
        velocidadeSlider.setValue(velocidade);

        // Ajustar o tamanho preferido do predioPanel com base no número de andares e elevadores
        int minAndarHeight = 50;
        int minElevadorWidth = 60;
        int espacoEntreElevadores = 10;
        int margemEsquerda = 80;
        int margemSuperior = 30;
        int margemDireita = 80;
        int margemInferior = 30;

        int panelWidth = margemEsquerda + (elevadores * (minElevadorWidth + espacoEntreElevadores)) + margemDireita;
        int panelHeight = margemSuperior + (andares * minAndarHeight) + margemInferior;
        predioPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        // Forçar o JScrollPane a revalidar para exibir barras de rolagem, se necessário
        predioScrollPane.revalidate();
        predioScrollPane.repaint();

        GerenciadorSimulacao gerenciador = new GerenciadorSimulacao();
        List<Pessoa> pessoas = gerenciador.gerarListaPessoas(quantidadePessoas, andares, null);
        for (Pessoa pessoa : pessoas) {
            Andar andar = getAndarPorNumero(predio, pessoa.getAndarOrigem());
            if (andar != null) {
                andar.adicionarPessoa(pessoa);
            }
        }

        //updateControlButtons(true);
        statusLabel.setText("Simulação pronta para iniciar");
        //restartButton.setVisible(false); // Esconder o botão de reiniciar ao iniciar
        //backToConfigButton.setVisible(true); // Mostrar o botão de voltar ao iniciar
        //simulador.iniciar();
    }

    private void renderizarPredio(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int quantidadeAndares = predio.getAndares().tamanho();
        int quantidadeElevadores = predio.getCentral().getElevadores().tamanho();

        // Dimensões mínimas fixas
        int andarHeight = 50;
        int elevadorWidth = 60;
        int espacoEntreElevadores = 10;

        int margemEsquerda = 80;
        int margemSuperior = 30;
        int margemDireita = 80;
        int margemInferior = 30;

        // Calculando dimensões totais do desenho
        int larguraPredio = margemEsquerda + (quantidadeElevadores * (elevadorWidth + espacoEntreElevadores)) + margemDireita - espacoEntreElevadores;
        int alturaPredio = margemSuperior + (quantidadeAndares * andarHeight) + margemInferior;

        // Ajustar o tamanho do painel, se necessário
        predioPanel.setPreferredSize(new Dimension(larguraPredio, alturaPredio));

        int yBase = alturaPredio - margemInferior;

        // Desenhando os andares
        Ponteiro p = predio.getAndares().getInicio();
        int andarIndex = 0;

        // Desenha um fundo do prédio
        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRect(0, 0, larguraPredio, alturaPredio);
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawRect(0, 0, larguraPredio - 1, alturaPredio - 1);

        // Loop pelos andares
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            int y = yBase - (andarIndex * andarHeight);

            // Desenho do andar
            g2d.setColor(new Color(220, 220, 220));
            g2d.fillRect(margemEsquerda, y - andarHeight, larguraPredio - margemEsquerda - margemDireita, andarHeight);
            g2d.setColor(new Color(150, 150, 150));
            g2d.drawRect(margemEsquerda, y - andarHeight, larguraPredio - margemEsquerda - margemDireita, andarHeight);

            // Linha divisória entre andares
            if (andarIndex > 0) {
                g2d.setColor(new Color(120, 120, 120));
                g2d.drawLine(margemEsquerda, y, larguraPredio - margemDireita, y);
            }

            // Texto do andar
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            g2d.drawString("Andar " + andar.getNumero(), 10, y - andarHeight / 2 + 5);

            // Indicação de pessoas esperando
            int pessoasAguardando = andar.getPessoasAguardando().tamanho();
            if (pessoasAguardando > 0) {
                g2d.setColor(new Color(70, 70, 70));
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString(pessoasAguardando + " pessoas", margemEsquerda + 10, y - andarHeight / 2 + 5);

                // Desenha pequenas figuras representando pessoas
                g2d.setColor(new Color(80, 80, 80));
                int xPessoa = margemEsquerda + 90;
                int yPessoa = y - andarHeight / 2;
                for (int i = 0; i < Math.min(pessoasAguardando, 5); i++) {
                    g2d.fillOval(xPessoa, yPessoa - 8, 8, 8);
                    g2d.drawLine(xPessoa + 4, yPessoa, xPessoa + 4, yPessoa + 8);
                    g2d.drawLine(xPessoa, yPessoa + 4, xPessoa + 8, yPessoa + 4);
                    xPessoa += 12;
                }
                if (pessoasAguardando > 5) {
                    g2d.drawString("+" + (pessoasAguardando - 5), xPessoa + 2, yPessoa + 5);
                }
            }

            // Status do painel
            PainelElevador painel = andar.getPainel();
            int xPainel = larguraPredio - margemDireita - 120;
            int yPainel = y - andarHeight / 2;

            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Arial", Font.ITALIC, 11));

            if (painel.getTipoPainel() == TipoPainel.UNICO_BOTAO) {
                g2d.drawString("Painel: ", xPainel, yPainel);
                g2d.setColor(painel.isChamadaGeralAtivada() ? Color.RED : Color.GRAY);
                g2d.fillOval(xPainel + 50, yPainel - 10, 12, 12);
            } else if (painel.getTipoPainel() == TipoPainel.DOIS_BOTOES) {
                g2d.drawString("Painel: ", xPainel, yPainel);
                // Botão subir
                g2d.setColor(painel.isBotaoSubirAtivado() ? Color.RED : Color.GRAY);
                int[] xPontosSubir = {xPainel + 50, xPainel + 56, xPainel + 62};
                int[] yPontosSubir = {yPainel - 8, yPainel - 14, yPainel - 8};
                g2d.fillPolygon(xPontosSubir, yPontosSubir, 3);

                // Botão descer
                g2d.setColor(painel.isBotaoDescerAtivado() ? Color.RED : Color.GRAY);
                int[] xPontosDescer = {xPainel + 50, xPainel + 56, xPainel + 62};
                int[] yPontosDescer = {yPainel + 2, yPainel + 8, yPainel + 2};
                g2d.fillPolygon(xPontosDescer, yPontosDescer, 3);
            }

            andarIndex++;
            p = p.getProximo();
        }

        // Desenhando os elevadores
        Ponteiro pElevadores = predio.getCentral().getElevadores().getInicio();
        int elevadorIndex = 0;
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            int x = margemEsquerda + (elevadorIndex * (elevadorWidth + espacoEntreElevadores));
            int elevadorY = yBase - (elevador.getAndarAtual() * andarHeight) - andarHeight;

            // Cor do elevador baseado no ID (índice de cores cíclico)
            Color corElevador = cores[elevador.getId() % cores.length];

            // Desenho da caixa do elevador
            g2d.setColor(corElevador);
            g2d.fillRoundRect(x, elevadorY + 5, elevadorWidth - 10, andarHeight - 10, 10, 10);

            // Borda do elevador
            g2d.setColor(corElevador.darker());
            g2d.drawRoundRect(x, elevadorY + 5, elevadorWidth - 10, andarHeight - 10, 10, 10);

            // Número do elevador
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String elevId = "E" + elevador.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(elevId);
            g2d.drawString(elevId, x + (elevadorWidth - 10 - textWidth) / 2, elevadorY + andarHeight / 2 + 5);

            // Quantidade de pessoas no elevador
            int pessoasNoElevador = elevador.getPessoasNoElevador().tamanho();
            if (pessoasNoElevador > 0) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String pessoasText = pessoasNoElevador + "p";
                g2d.drawString(pessoasText, x + (elevadorWidth - 10) / 2 - 5, elevadorY + andarHeight - 10);
            }

            // Desenho das guias do elevador (trilhos)
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawLine(x - 3, margemSuperior - 5, x - 3, yBase);
            g2d.drawLine(x + elevadorWidth - 7, margemSuperior - 5, x + elevadorWidth - 7, yBase);

            elevadorIndex++;
            pElevadores = pElevadores.getProximo();
        }

        // Informações gerais
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Minuto: " + simulador.getMinutoSimulado(), margemEsquerda + 10, margemSuperior - 15);

        if (simulador.isHorarioPico()) {
            g2d.setColor(new Color(200, 50, 50));
            g2d.drawString("HORÁRIO DE PICO", margemEsquerda + 120, margemSuperior - 15);
        }
    }

    public void atualizar() {
        atualizarListaPessoas();
        atualizarEstatisticas();
        predioPanel.repaint();
        predioScrollPane.revalidate();
    }

    private void atualizarListaPessoas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de Pessoas:\n");
        sb.append("┌──────┬─────────────────────┬───────────┐\n");
        sb.append("   ID     Posição Atual    Destino    \n");
        sb.append("├──────┼─────────────────────┼───────────┤\n");

        Ponteiro pAndares = predio.getAndares().getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            Ponteiro pPessoas = andar.getPessoasAguardando().getPonteiroInicio();
            while (pPessoas != null && pPessoas.isValido()) {
                Pessoa pessoa =(Pessoa) pPessoas.getElemento();
                sb.append(String.format(" P%-3d  Andar %-2d %-9s  Andar %-3d \n",
                        pessoa.getId(),
                        andar.getNumero(),
                        pessoa.isPrioritaria() ? "(PRIOR.)" : "(Aguard.)",
                        pessoa.getAndarDestino()));
                pPessoas = pPessoas.getProximo();
            }
            pAndares = pAndares.getProximo();
        }

        Ponteiro pElevadores = predio.getCentral().getElevadores().getInicio();
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            Ponteiro pPessoas = elevador.getPessoasNoElevador().getPonteiroInicio();
            while (pPessoas != null && pPessoas.isValido()) {
                Pessoa pessoa = (Pessoa) pPessoas.getElemento();
                sb.append(String.format("  P%-3d   Elevador %-1d (A%-2d)     Andar %-3d  \n",
                        pessoa.getId(),
                        elevador.getId(),
                        elevador.getAndarAtual(),
                        pessoa.getAndarDestino()));
                pPessoas = pPessoas.getProximo();
            }
            pElevadores = pElevadores.getProximo();
        }

        pessoasTextArea.setText(sb.toString());
    }

    private void atualizarEstatisticas() {
        statsPanel.removeAll();
        Estatisticas stats = simulador.getEstatisticas();
        statsPanel.add(new JLabel("Tempo Médio Espera: " + String.format("%.2f", stats.getTempoMedioEspera()) + " min"));
        statsPanel.add(new JLabel("Energia Consumida: " + String.format("%.2f", stats.getEnergiaConsumida()) + " un"));
        statsPanel.add(new JLabel("Pessoas Transportadas: " + stats.getTotalPessoasTransportadas()));
        statsPanel.add(new JLabel("Minuto Simulado: " + simulador.getMinutoSimulado()));
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private Andar getAndarPorNumero(Predio predio, int numero) {
        Ponteiro p = predio.getAndares().getInicio();
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            if (andar.getNumero() == numero) {
                return andar;
            }
            p = p.getProximo();
        }
        return null;
    }

    private void reiniciarSimulacao() {
        // Obter parâmetros da simulação atual
        int andares = Integer.parseInt(andaresField.getText());
        int elevadores = Integer.parseInt(elevadoresField.getText());
        int capacidade = Integer.parseInt(capacidadeField.getText());
        int velocidade = Integer.parseInt(velocidadeField.getText());
        int tempoPico = Integer.parseInt(tempoPicoField.getText());
        int tempoForaPico = Integer.parseInt(tempoForaPicoField.getText());
        int heuristica = heuristicaCombo.getSelectedIndex() + 1;
        TipoPainel tipoPainel = TipoPainel.valueOf((String) painelCombo.getSelectedItem());
        int quantidadePessoas = Integer.parseInt(pessoasField.getText());

        // Criar novo simulador
        simulador = new Simulador(andares, elevadores, velocidade, capacidade, tempoPico, tempoForaPico, heuristica, tipoPainel);
        simulador.setGui(this);
        predio = simulador.getPredio();

        // Configurar o slider com o valor inicial
        velocidadeSlider.setValue(velocidade);

        // Ajustar o tamanho preferido do predioPanel com base no número de andares e elevadores
        int minAndarHeight = 50;
        int minElevadorWidth = 60;
        int espacoEntreElevadores = 10;
        int margemEsquerda = 80;
        int margemSuperior = 30;
        int margemDireita = 80;
        int margemInferior = 30;

        int panelWidth = margemEsquerda + (elevadores * (minElevadorWidth + espacoEntreElevadores)) + margemDireita;
        int panelHeight = margemSuperior + (andares * minAndarHeight) + margemInferior;
        predioPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        // Forçar o JScrollPane a revalidar para exibir barras de rolagem, se necessário
        predioScrollPane.revalidate();
        predioScrollPane.repaint();

        GerenciadorSimulacao gerenciador = new GerenciadorSimulacao();
        List<Pessoa> pessoas = gerenciador.gerarListaPessoas(quantidadePessoas, andares, null);
        for (Pessoa pessoa : pessoas) {
            Andar andar = getAndarPorNumero(predio, pessoa.getAndarOrigem());
            if (andar != null) {
                andar.adicionarPessoa(pessoa);
            }
        }

        // Reiniciar a simulação
        updateControlButtons(true);
        statusLabel.setText("Simulação reiniciada");
        restartButton.setVisible(false); // Esconder o botão de reiniciar ao iniciar
        backToConfigButton.setVisible(true); // Mostrar o botão de voltar ao iniciar
        //simulador.iniciar();
    }

    private void reiniciarConfiguracao() {
        andaresField.setText("12");
        elevadoresField.setText("7");
        capacidadeField.setText("8");
        velocidadeField.setText("1000");
        tempoPicoField.setText("2");
        tempoForaPicoField.setText("1");
        heuristicaCombo.setSelectedIndex(0);
        painelCombo.setSelectedIndex(0);
        pessoasField.setText("30");

        // Limpar estatísticas e estado atual
        if (simulador != null) {
            simulador.getEstatisticas().zerar();
            predio.resetar();
            simulador.setMinutoSimulado(0);
        }

        // Ajustar visibilidade dos botões
        restartButton.setVisible(false);
        backToConfigButton.setVisible(false);
        updateControlButtons(false);
        statusLabel.setText("Pronto para iniciar");
    }
}
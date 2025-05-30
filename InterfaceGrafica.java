import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;

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
    private JSpinner prioridadeSpinner;
    private JSpinner minutoChegadaSpinner;

    // Controle de simulação
    private JButton pauseButton;
    private JButton continueButton;
    public JButton restartButton;
    public JButton backToConfigButton;
    private JButton saveButton; 
    private JSlider velocidadeSlider;
    private JLabel statusLabel;
    private JLabel velocidadeLabel;

    private Timer adicionarPessoasTimer;
    public Lista<Pessoa> pessoas;

    private JComboBox<Integer> elevadorLogCombo;
    private JTextArea logTextArea;

    public InterfaceGrafica() {
        setTitle("Simulador de Elevadores");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        configPanel = criarPainelConfiguracao();

        simulationPanel = new JPanel(new BorderLayout(10, 10));
        simulationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        predioScrollPane = new JScrollPane(predioPanel);
        predioScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        predioScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        predioScrollPane.setBorder(BorderFactory.createEmptyBorder());

        statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120)),
                "Estatísticas", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(50, 50, 50)));

        pessoasTextArea = new JTextArea();
        pessoasTextArea.setEditable(false);
        pessoasTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pessoasTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(pessoasTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120)),
                "Pessoas", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(50, 50, 50)));

        JPanel pessoasPanel = new JPanel(new GridLayout(2, 1));
        pessoasPanel.setPreferredSize(new Dimension(450, 600));

        JPanel listaPessoasPanel = new JPanel(new BorderLayout());
        listaPessoasPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel logElevadorPanel = new JPanel(new BorderLayout());

        elevadorLogCombo = new JComboBox<>();
        elevadorLogCombo.setPreferredSize(new Dimension(50, elevadorLogCombo.getPreferredSize().height));
        elevadorLogCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                atualizarLogElevador();
            }
        });
        JPanel elevadorLogPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        elevadorLogPanel.add(new JLabel("Selecionar Elevador:"));
        elevadorLogPanel.add(elevadorLogCombo);
        logElevadorPanel.add(elevadorLogPanel, BorderLayout.NORTH);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 120)),
                "Log do Elevador", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(50, 50, 50)));
        logElevadorPanel.add(logScrollPane, BorderLayout.CENTER);

        pessoasPanel.add(listaPessoasPanel);
        pessoasPanel.add(logElevadorPanel);

        controlPanel = criarPainelControle();

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setPreferredSize(new Dimension(700, 600));
        westPanel.add(statsPanel, BorderLayout.NORTH);
        westPanel.add(predioScrollPane, BorderLayout.CENTER);
        westPanel.add(controlPanel, BorderLayout.SOUTH);

        simulationPanel.add(westPanel, BorderLayout.CENTER);
        simulationPanel.add(pessoasPanel, BorderLayout.EAST);

        mainPanel.add(configPanel, "Config");
        mainPanel.add(simulationPanel, "Simulation");
        add(mainPanel);

        cardLayout.show(mainPanel, "Config");
    }

    private JPanel criarPainelConfiguracao() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(90, 400, 90, 400));

        JLabel titleLabel = new JLabel("Configuração da Simulação", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {
                "Andares (mín. 5):",
                "Elevadores (mín. 1):",
                "Capacidade por Elevador:",
                "Velocidade (ms):",
                "Viagem em Pico (min):",
                "Viagem Fora Pico (min):",
                "Heurística:",
                "Tipo de Painel:",
                "Pessoas:"
        };

        JTextField[] fields = {
            andaresField = new JTextField("11", 10),
            elevadoresField = new JTextField("10", 10),
            capacidadeField = new JTextField("8", 10),
            velocidadeField = new JTextField("1000", 10),
            tempoPicoField = new JTextField("2", 10),
            tempoForaPicoField = new JTextField("1", 10),
            pessoasField = new JTextField("300", 10)
        };

        for (JTextField field : fields) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    field.getBorder(),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        }

        String[] heuristicas = {"1 - Ordem de Chegada", "2 - Otimização de Tempo", "3 - Otimização de Energia"};
        heuristicaCombo = new JComboBox<>(heuristicas);
        heuristicaCombo.setSelectedIndex(0);

        String[] paineis = {"UNICO_BOTAO", "DOIS_BOTOES", "PAINEL_NUMERICO"};
        painelCombo = new JComboBox<>(paineis);
        painelCombo.setSelectedIndex(0);

        int row = 0;
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 13));
            label.setHorizontalAlignment(SwingConstants.RIGHT);

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            gbc.anchor = GridBagConstraints.EAST;
            fieldsPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.anchor = GridBagConstraints.CENTER;
            if (i == 6) {
                heuristicaCombo.setPreferredSize(new Dimension(180, 25));
                fieldsPanel.add(heuristicaCombo, gbc);
            } else if (i == 7) {
                painelCombo.setPreferredSize(new Dimension(180, 25));
                fieldsPanel.add(painelCombo, gbc);
            } else if (i == 8) {
                pessoasField.setHorizontalAlignment(JTextField.CENTER);
                fieldsPanel.add(pessoasField, gbc);
            } else {
                fields[i].setHorizontalAlignment(JTextField.CENTER);
                fieldsPanel.add(fields[i], gbc);
            }
            row++;
        }

        // Agrupando os campos adicionais em um painel horizontal
        JPanel extraPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        JLabel prioridadeLabel = new JLabel("Prioridade (%):");
        prioridadeSpinner = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
        JLabel minutoChegadaLabel = new JLabel("Minuto máx. chegada:");
        minutoChegadaSpinner = new JSpinner(new SpinnerNumberModel(120, 1, 1440, 1));
        prioridadeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        minutoChegadaLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        prioridadeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        minutoChegadaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        ((JSpinner.DefaultEditor) prioridadeSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        ((JSpinner.DefaultEditor) minutoChegadaSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        extraPanel.add(prioridadeLabel);
        extraPanel.add(prioridadeSpinner);
        extraPanel.add(minutoChegadaLabel);
        extraPanel.add(minutoChegadaSpinner);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        fieldsPanel.add(extraPanel, gbc);
        row++;
        gbc.gridwidth = 1;

        // Botões centralizados
        JButton startButton = criarBotao("Iniciar Simulação", new Color(60, 130, 200));
        startButton.setForeground(new Color(60, 130, 200));
        startButton.addActionListener(e -> {
            if (validarParametros()) {
                iniciarSimulacao();
                cardLayout.show(mainPanel, "Simulation");
            }
        });

        JButton loadButton = criarBotao("Carregar Simulação", new Color(240, 240, 240));
        loadButton.setForeground(new Color(60, 130, 200));
        loadButton.setVisible(true);
        loadButton.addActionListener(e -> carregarSimulacao());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(loadButton);

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

        pauseButton = criarBotao("Pausar", new Color(240, 240, 240));
        pauseButton.setForeground(new Color(60, 130, 200));
        pauseButton.setVisible(false);
        pauseButton.addActionListener(e -> {
            if (simulador != null) {
                simulador.pausar();
                pauseButton.setVisible(false);
                continueButton.setText("Continuar");
                continueButton.setVisible(true);
                restartButton.setVisible(true);
                backToConfigButton.setVisible(true);
                updateControlButtons(false);
                velocidadeLabel.setVisible(false);
                velocidadeSlider.setVisible(false);
            }
        });

        continueButton = criarBotao("Iniciar", new Color(240, 240, 240));
        continueButton.setForeground(new Color(60, 130, 200));
        continueButton.addActionListener(e -> {
            if (simulador != null) {
                if (continueButton.getText().equals("Iniciar")) {
                    simulador.iniciar();
                    continueButton.setVisible(false);
                    pauseButton.setVisible(true);
                    updateControlButtons(true);
                    velocidadeLabel.setVisible(true);
                    velocidadeSlider.setVisible(true);
                } else {
                    simulador.continuar();
                    continueButton.setVisible(false);
                    pauseButton.setVisible(true);
                    restartButton.setVisible(false);
                    backToConfigButton.setVisible(false);
                    updateControlButtons(true);
                    velocidadeLabel.setVisible(true);
                    velocidadeSlider.setVisible(true);
                }
            }
        });

        restartButton = criarBotao("Reiniciar Simulação", new Color(240, 240, 240));
        restartButton.setForeground(new Color(60, 130, 200));
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> {
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
                velocidadeLabel.setVisible(false);
                velocidadeSlider.setVisible(false);
            }
        });

        backToConfigButton = criarBotao("Voltar para Configuração", new Color(240, 240, 240));
        backToConfigButton.setForeground(new Color(60, 130, 200));
        backToConfigButton.setVisible(false);
        backToConfigButton.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(InterfaceGrafica.this,
                    "Deseja realmente voltar para a configuração? A simulação atual será perdida.",
                    "Voltar para Configuração",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (resposta == JOptionPane.YES_OPTION) {
                if (simulador != null) {
                    simulador.pausar();
                    adicionarPessoasTimer.cancel();
                }
                cardLayout.show(mainPanel, "Config");
                pauseButton.setVisible(false);
                continueButton.setText("Iniciar");
                continueButton.setVisible(true);
                restartButton.setVisible(false);
                backToConfigButton.setVisible(false);
                updateControlButtons(false);
                velocidadeLabel.setVisible(false);
                velocidadeSlider.setVisible(false);
            }
        });

        saveButton = criarBotao("Salvar Simulação", new Color(240, 240, 240));
        saveButton.setForeground(new Color(60, 130, 200));
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> salvarSimulacao());

        panel.add(continueButton);
        panel.add(pauseButton);
        panel.add(restartButton);
        panel.add(backToConfigButton);
        panel.add(saveButton);

        velocidadeLabel = new JLabel("Velocidade:");
        velocidadeSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, 1000); 
        velocidadeSlider.setMajorTickSpacing(400); 
        velocidadeSlider.setMinorTickSpacing(100);
        velocidadeSlider.setPaintTicks(true);
        velocidadeSlider.addChangeListener(e -> { 
            if (!velocidadeSlider.getValueIsAdjusting()) { 
                if (simulador != null) { 
                    int min = velocidadeSlider.getMinimum();
                    int max = velocidadeSlider.getMaximum();
                    int valorSlider = velocidadeSlider.getValue();
                    int valorInvertido = max + min - valorSlider;
                    simulador.setVelocidadeSimulacao(valorInvertido);
                }
            }
        });

        panel.add(velocidadeLabel);
        panel.add(velocidadeSlider);

        statusLabel = new JLabel("Simulação: Parada");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(statusLabel);

        return panel;
    }

    public void salvarSimulacao() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("simSalvos"));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".sim");
            }
            @Override
            public String getDescription() {
                return "Arquivos de Simulação (*.sim)";
            }
        });

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".sim")) {
                file = new File(file.getAbsolutePath() + ".sim");
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                SimulationState state = new SimulationState(
                        predio,
                        simulador,
                        pessoas,
                        heuristicaCombo.getSelectedIndex() + 1,
                        TipoPainel.valueOf((String) painelCombo.getSelectedItem())
                );
                oos.writeObject(state);
                JOptionPane.showMessageDialog(this, "Simulação salva com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar simulação: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void carregarSimulacao() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("simSalvos"));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".sim");
            }
            @Override
            public String getDescription() {
                return "Arquivos de Simulação (*.sim)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                SimulationState state = (SimulationState) ois.readObject();
                predio = state.getPredio();
                simulador = state.getSimulador();
                pessoas = state.getPessoas();

                // Atualizar os campos da interface gráfica
                atualizarCamposInterface(state);
                atualizarEstatisticas();
                atualizarLogElevador();

                // Exibir o painel de simulação
                cardLayout.show(mainPanel, "Simulation");
                predioPanel.repaint();

                // Pausar o simulador e preparar para iniciar
                simulador.setInterfaceGrafica(this);
                simulador.pausar();
                if (adicionarPessoasTimer != null) {
                    adicionarPessoasTimer.cancel();
                }
                agendarAdicaoDePessoas();
                continueButton.setText("Iniciar");
                continueButton.setVisible(true);
                pauseButton.setVisible(false);
                restartButton.setVisible(false);
                backToConfigButton.setVisible(false);
                updateControlButtons(false);
                velocidadeLabel.setVisible(false);
                velocidadeSlider.setVisible(false);
                statusLabel.setText("Simulação pronta para iniciar");
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar simulação: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarCamposInterface(SimulationState state) {
        // Atualizar o combo de heurística
        int heuristica = state.getHeuristica();
        String heuristicaText = switch (heuristica) {
            case 1 -> "1 - Ordem de Chegada";
            case 2 -> "2 - Otimização de Tempo";
            case 3 -> "3 - Otimização de Energia";
            default -> "1 - Ordem de Chegada";
        };
        heuristicaCombo.setSelectedItem(heuristicaText);

        // Atualizar o combo de tipo de painel
        String tipoPainel = state.getTipoPainel().toString();
        painelCombo.setSelectedItem(tipoPainel);

        // Atualizar o número de andares, elevadores, etc.
        andaresField.setText(String.valueOf(predio.getAndares().tamanho()));
        elevadoresField.setText(String.valueOf(predio.getCentral().getElevadores().tamanho()));
        capacidadeField.setText(String.valueOf(((Elevador) predio.getCentral().getElevadores().getInicio().getElemento()).getCapacidadeMaxima()));
        tempoPicoField.setText(String.valueOf(((Elevador) predio.getCentral().getElevadores().getInicio().getElemento()).getTempoViagemPorAndarPico()));
        tempoForaPicoField.setText(String.valueOf(((Elevador) predio.getCentral().getElevadores().getInicio().getElemento()).getTempoViagemPorAndarForaPico()));
        pessoasField.setText(String.valueOf(pessoas.tamanho()));

        // Atualizar o combo de elevadores para logs
        elevadorLogCombo.removeAllItems();
        Ponteiro p = predio.getCentral().getElevadores().getInicio();
        while (p != null && p.isValido()) {
            Elevador elevador = (Elevador) p.getElemento();
            elevadorLogCombo.addItem(elevador.getId());
            p = p.getProximo();
        }
    }

    private JButton criarBotao(String texto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return botao;
    }

    public void updateControlButtons(boolean isRunning) {
        pauseButton.setEnabled(isRunning);
        continueButton.setEnabled(!isRunning);
        saveButton.setVisible(!isRunning);
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
        simulador.setInterfaceGrafica(this);
        predio = simulador.getPredio();

        velocidadeSlider.setValue(velocidade);

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

        predioScrollPane.revalidate();
        predioScrollPane.repaint();

        GerenciadorSimulacao gerenciador = new GerenciadorSimulacao();
        double percentualPrioridade = prioridadeSpinner.getValue() != null ? (Integer) prioridadeSpinner.getValue() : 50;
        int minutoMaxChegada = minutoChegadaSpinner.getValue() != null ? (Integer) minutoChegadaSpinner.getValue() : 60;
        pessoas = gerenciador.gerarListaPessoas(
            quantidadePessoas,
            andares,
            (int) percentualPrioridade,
            minutoMaxChegada
        );

        agendarAdicaoDePessoas();

        atualizarComboBoxElevadores();

        statusLabel.setText("Simulação pronta para iniciar");
        simulador.pausar();
    }

    private void agendarAdicaoDePessoas() {
        if (adicionarPessoasTimer != null) {
            adicionarPessoasTimer.cancel();
        }

        adicionarPessoasTimer = new Timer();
        InterfaceGrafica gui = this;
        Simulador simuladorLocal = this.simulador;

        TimerTask adicionarPessoasTask = new TimerTask() {
            @Override
            public void run() {
                Lista<Pessoa> pessoasRemover = new Lista<>();

                Ponteiro p = pessoas.getInicio();
                while (p != null && p.isValido()) {
                    Pessoa pessoa = (Pessoa) p.getElemento();
                    if (!pessoa.isChegouAoDestino() && simuladorLocal.deveAdicionarPessoa(pessoa) && !predio.pessoaJaNoSistema(pessoa)) {
                        Andar andar = getAndarPorNumero(predio, pessoa.getAndarOrigem());
                        if (andar != null) {
                            andar.adicionarPessoa(pessoa);
                            System.out.println("Pessoa " + pessoa.getId() + " adicionada ao andar " + andar.getNumero() + " no minuto " + pessoa.getMinutoChegada());
                        }
                    }
                    if (pessoa.isChegouAoDestino()) {
                        pessoasRemover.inserirFim(pessoa);
                    }
                    p = p.getProximo();
                }

                p = pessoasRemover.getInicio();
                while (p != null && p.isValido()) {
                    Pessoa pessoa = (Pessoa) p.getElemento();
                    pessoas.remover(pessoa);
                    p = p.getProximo();
                }

                SwingUtilities.invokeLater(() -> gui.atualizar());
            }
        };

        adicionarPessoasTimer.scheduleAtFixedRate(adicionarPessoasTask, 0, simulador.getVelocidadeEmMs());
    }

    private void renderizarPredio(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int quantidadeAndares = predio.getAndares().tamanho();
        int quantidadeElevadores = predio.getCentral().getElevadores().tamanho();

        int andarHeight = 50;
        int elevadorWidth = 60;
        int espacoEntreElevadores = 10;

        int margemEsquerda = 80;
        int margemSuperior = 30;
        int margemDireita = 80;
        int margemInferior = 30;

        int larguraPredio = margemEsquerda + (quantidadeElevadores * (elevadorWidth + espacoEntreElevadores)) + margemDireita - espacoEntreElevadores;
        int alturaPredio = margemSuperior + (quantidadeAndares * andarHeight) + margemInferior;

        predioPanel.setPreferredSize(new Dimension(larguraPredio, alturaPredio));

        int yBase = alturaPredio - margemInferior;

        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRect(0, 0, larguraPredio, alturaPredio);
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawRect(0, 0, larguraPredio - 1, alturaPredio - 1);

        Ponteiro p = predio.getAndares().getInicio();
        int andarIndex = 0;

        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            int y = yBase - (andarIndex * andarHeight);

            g2d.setColor(new Color(220, 220, 220));
            g2d.fillRect(margemEsquerda, y - andarHeight, larguraPredio - margemEsquerda - margemDireita, andarHeight);
            g2d.setColor(new Color(150, 150, 150));
            g2d.drawRect(margemEsquerda, y - andarHeight, larguraPredio - margemEsquerda - margemDireita, andarHeight);

            if (andarIndex > 0) {
                g2d.setColor(new Color(120, 120, 120));
                g2d.drawLine(margemEsquerda, y, larguraPredio - margemDireita, y);
            }

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            g2d.drawString("Andar " + andar.getNumero(), 10, y - andarHeight / 2 + 5);

            int pessoasAguardando = andar.getPessoasAguardando().tamanho();
            if (pessoasAguardando > 0) {
                g2d.setColor(new Color(70, 70, 70));
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString(pessoasAguardando + " pessoas", margemEsquerda + 10, y - andarHeight / 2 + 5);

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
                g2d.setColor(painel.isBotaoSubirAtivado() ? Color.RED : Color.GRAY);
                int[] xPontosSubir = {xPainel + 50, xPainel + 56, xPainel + 62};
                int[] yPontosSubir = {yPainel - 8, yPainel - 14, yPainel - 8};
                g2d.fillPolygon(xPontosSubir, yPontosSubir, 3);

                g2d.setColor(painel.isBotaoDescerAtivado() ? Color.RED : Color.GRAY);
                int[] xPontosDescer = {xPainel + 50, xPainel + 56, xPainel + 62};
                int[] yPontosDescer = {yPainel + 2, yPainel + 8, yPainel + 2};
                g2d.fillPolygon(xPontosDescer, yPontosDescer, 3);
            }

            andarIndex++;
            p = p.getProximo();
        }

        Ponteiro pElevadores = predio.getCentral().getElevadores().getInicio();
        int elevadorIndex = 0;
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            int x = margemEsquerda + (elevadorIndex * (elevadorWidth + espacoEntreElevadores));
            int elevadorY = yBase - (elevador.getAndarAtual() * andarHeight) - andarHeight;

            Color corElevador = cores[elevador.getId() % cores.length];

            g2d.setColor(corElevador);
            g2d.fillRoundRect(x, elevadorY + 5, elevadorWidth - 10, andarHeight - 10, 10, 10);

            g2d.setColor(corElevador.darker());
            g2d.drawRoundRect(x, elevadorY + 5, elevadorWidth - 10, andarHeight - 10, 10, 10);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String elevId = "E" + elevador.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(elevId);
            g2d.drawString(elevId, x + (elevadorWidth - 10 - textWidth) / 2, elevadorY + andarHeight / 2 + 5);

            int pessoasNoElevador = elevador.getPessoasNoElevador().tamanho();
            if (pessoasNoElevador > 0) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String pessoasText = pessoasNoElevador + "p";
                g2d.drawString(pessoasText, x + (elevadorWidth - 10) / 2 - 5, elevadorY + andarHeight - 10);
            }

            g2d.setColor(new Color(200, 200, 200));
            g2d.drawLine(x - 3, margemSuperior - 5, x - 3, yBase);
            g2d.drawLine(x + elevadorWidth - 7, margemSuperior - 5, x + elevadorWidth - 7, yBase);

            elevadorIndex++;
            pElevadores = pElevadores.getProximo();
        }

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
        atualizarComboBoxElevadores();
        atualizarLogElevador();
    }

    private void atualizarListaPessoas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de Pessoas:\n");
        sb.append("\n");
        sb.append("   ID       Posição Atual         Destino       Chegada      \n");
        sb.append("\n");

        Ponteiro pAndares = predio.getAndares().getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            Ponteiro pPessoas = andar.getPessoasAguardando().getPonteiroInicio();
            while (pPessoas != null && pPessoas.isValido()) {
                Pessoa pessoa = (Pessoa) pPessoas.getElemento();
                if (simulador.getMinutoSimulado() >= pessoa.getMinutoChegada()) {
                    sb.append(String.format("  P%-3d      Andar %-2d %-9s      Andar %-3d      %-12d  \n",
                            pessoa.getId(),
                            andar.getNumero(),
                            pessoa.isPrioritaria() ? "(PRIOR.)" : "(Aguard.)",
                            pessoa.getAndarDestino(),
                            pessoa.getMinutoChegada()));
                }
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
                if (simulador.getMinutoSimulado() >= pessoa.getMinutoChegada()) {
                    sb.append(String.format("  P%-3d      Elevador %-1d (A%-2d)        Andar %-3d      %-12d  \n",
                            pessoa.getId(),
                            elevador.getId(),
                            elevador.getAndarAtual(),
                            pessoa.getAndarDestino(),
                            pessoa.getMinutoChegada()));
                }
                pPessoas = pPessoas.getProximo();
            }
            pElevadores = pElevadores.getProximo();
        }

        Ponteiro p = pessoas.getInicio();
        while (p != null && p.isValido()) {
            Pessoa pessoa = (Pessoa) p.getElemento();
            if (pessoa.isChegouAoDestino()) {
                sb.append(String.format("  P%-3d      Andar %-2d (Destino)      Andar %-3d      %-12d  \n",
                        pessoa.getId(),
                        pessoa.getPosicaoAtual(),
                        pessoa.getAndarDestino(),
                        pessoa.getMinutoChegada()));
            }
            p = p.getProximo();
        }

        pessoasTextArea.setText(sb.toString());
    }

    private void atualizarEstatisticas() {
        statsPanel.removeAll();
        Estatisticas stats = simulador.getEstatisticas();

        JLabel tempoMedioLabel = new JLabel("Tempo Médio de Espera: " + String.format("%.2f", stats.getTempoMedioEspera()) + " min");
        JLabel chamadasAtendidasLabel = new JLabel("Chamadas Atendidas: " + stats.getChamadasAtendidas());
        JLabel energiaConsumidaLabel = new JLabel("Energia Consumida: " + String.format("%.2f", stats.getEnergiaConsumida()));
        JLabel pessoasTransportadasLabel = new JLabel("Pessoas Transportadas: " + stats.getTotalPessoasTransportadas());
        String heuristicaText = (String) heuristicaCombo.getSelectedItem();
        String tipoPainelText = (String) painelCombo.getSelectedItem();
        JLabel heuristicaLabel = new JLabel("Heurística: " + heuristicaText);
        JLabel tipoPainelLabel = new JLabel("Tipo de Painel: " + tipoPainelText);

        statsPanel.add(tempoMedioLabel);
        statsPanel.add(chamadasAtendidasLabel);
        statsPanel.add(energiaConsumidaLabel);
        statsPanel.add(pessoasTransportadasLabel);
        statsPanel.add(heuristicaLabel);
        statsPanel.add(tipoPainelLabel);

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void atualizarComboBoxElevadores() {
        Integer selectedElevadorId = (Integer) elevadorLogCombo.getSelectedItem();

        elevadorLogCombo.removeAllItems();
        if (predio != null && predio.getCentral() != null) {
            Lista<Elevador> elevadores = predio.getCentral().getElevadores();
            Ponteiro p = elevadores.getInicio();
            while (p != null && p.isValido()) {
                Elevador elevador = (Elevador) p.getElemento();
                elevadorLogCombo.addItem(elevador.getId());
                p = p.getProximo();
            }
        }

        if (selectedElevadorId != null) {
            elevadorLogCombo.setSelectedItem(selectedElevadorId);
        }
    }

    private void atualizarLogElevador() {
        logTextArea.setText("");
        Integer selectedElevadorId = (Integer) elevadorLogCombo.getSelectedItem();
        if (selectedElevadorId != null && predio != null && predio.getCentral() != null) {
            Lista elevadores = predio.getCentral().getElevadores();
            Ponteiro p = elevadores.getInicio();
            while (p != null && p.isValido()) {
                Elevador elevador = (Elevador) p.getElemento();
                if (elevador.getId() == selectedElevadorId) {
                    Lista<LogElevador> logs = elevador.getLogs();
                    Ponteiro pLog = logs.getInicio();
                    StringBuilder logText = new StringBuilder();
                    while (pLog != null && pLog.isValido()) {
                        LogElevador log = (LogElevador) pLog.getElemento();
                        logText.append(log.toString()).append("\n");
                        pLog = pLog.getProximo();
                    }
                    logTextArea.setText(logText.toString());
                    break;
                }
                p = p.getProximo();
            }
        }
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
        simulador.setInterfaceGrafica(this);
        predio = simulador.getPredio();

        velocidadeSlider.setValue(velocidade);

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

        predioScrollPane.revalidate();
        predioScrollPane.repaint();

        GerenciadorSimulacao gerenciador = new GerenciadorSimulacao();
        double percentualPrioridade = prioridadeSpinner.getValue() != null ? (Integer) prioridadeSpinner.getValue() : 50;
        int minutoMaxChegada = minutoChegadaSpinner.getValue() != null ? (Integer) minutoChegadaSpinner.getValue() : 60;
        pessoas = gerenciador.gerarListaPessoas(
            quantidadePessoas,
            andares,
            (int) percentualPrioridade,
            minutoMaxChegada
        );

        agendarAdicaoDePessoas();

        atualizarComboBoxElevadores();

        statusLabel.setText("Simulação pronta para iniciar");
        simulador.pausar();
    }

    public void resetarVelocidadeSlider() {
        velocidadeSlider.setValue(1000);
    }
}
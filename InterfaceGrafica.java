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
    private JSlider velocidadeSlider;
    private JLabel statusLabel;

    public InterfaceGrafica() {
        setTitle("Simulador de Elevadores");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

        // Painel de controle de simulação
        controlPanel = criarPainelControle();

        // Montagem dos painéis
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setPreferredSize(new Dimension(700, 600));
        westPanel.add(statsPanel, BorderLayout.NORTH);
        westPanel.add(predioPanel, BorderLayout.CENTER);
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
        andaresField = new JTextField("5", 10);
        elevadoresField = new JTextField("2", 10);
        capacidadeField = new JTextField("8", 10);
        velocidadeField = new JTextField("1000", 10);
        tempoPicoField = new JTextField("2", 10);
        tempoForaPicoField = new JTextField("1", 10);
        pessoasField = new JTextField("10", 10);

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
        JButton startButton = new JButton("Iniciar Simulação");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(60, 130, 200));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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
        pauseButton = new JButton("Pausar");
        pauseButton.setBackground(new Color(240, 240, 240));
        pauseButton.setFocusPainted(false);
        pauseButton.addActionListener(e -> {
            if (simulador != null) {
                simulador.pausar();
                updateControlButtons(false);
            }
        });

        // Botão de continuar
        continueButton = new JButton("Continuar");
        continueButton.setBackground(new Color(240, 240, 240));
        continueButton.setFocusPainted(false);
        continueButton.setEnabled(false);
        continueButton.addActionListener(e -> {
            if (simulador != null) {
                simulador.continuar();
                updateControlButtons(true);
            }
        });

        // Slider de velocidade
        JLabel velocidadeLabel = new JLabel("Velocidade: ");
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

        // Label de status
        statusLabel = new JLabel("Pronto para iniciar");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        // Adicionar componentes ao painel
        panel.add(pauseButton);
        panel.add(continueButton);
        panel.add(new JSeparator(JSeparator.VERTICAL));
        panel.add(velocidadeLabel);
        panel.add(velocidadeSlider);
        panel.add(new JSeparator(JSeparator.VERTICAL));
        panel.add(statusLabel);

        return panel;
    }

    private void updateControlButtons(boolean isRunning) {
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

        GerenciadorSimulacao gerenciador = new GerenciadorSimulacao();
        List<Pessoa> pessoas = gerenciador.gerarListaPessoas(quantidadePessoas, andares, null);
        for (Pessoa pessoa : pessoas) {
            Andar andar = getAndarPorNumero(predio, pessoa.getAndarOrigem());
            if (andar != null) {
                andar.adicionarPessoa(pessoa);
            }
        }

        updateControlButtons(true);
        statusLabel.setText("Simulação iniciada");
        simulador.iniciar();
    }

    private void renderizarPredio(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int quantidadeAndares = predio.getAndares().tamanho();
        int quantidadeElevadores = predio.getCentral().getElevadores().tamanho();

        // Calculando dimensões
        int larguraPredio = predioPanel.getWidth() - 160;
        int alturaPredio = predioPanel.getHeight() - 60;
        int andarHeight = Math.max(50, alturaPredio / quantidadeAndares);
        int elevadorWidth = Math.min(60, (larguraPredio - 40) / quantidadeElevadores);
        int espacoEntreElevadores = 10;

        int margemEsquerda = 80;
        int margemSuperior = 30;
        int yBase = predioPanel.getHeight() - margemSuperior;

        // Desenhando os andares
        Ponteiro p = predio.getAndares().getInicio();
        int andarIndex = 0;

        // Desenha um fundo do prédio
        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRect(margemEsquerda - 10, margemSuperior - 10,
                larguraPredio + 20, alturaPredio + 20);
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawRect(margemEsquerda - 10, margemSuperior - 10,
                larguraPredio + 20, alturaPredio + 20);

        // Loop pelos andares
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            int y = yBase - (andarIndex * andarHeight);

            // Desenho do andar
            g2d.setColor(new Color(220, 220, 220));
            g2d.fillRect(margemEsquerda, y - andarHeight, larguraPredio, andarHeight);
            g2d.setColor(new Color(150, 150, 150));
            g2d.drawRect(margemEsquerda, y - andarHeight, larguraPredio, andarHeight);

            // Linha divisória entre andares
            if (andarIndex > 0) {
                g2d.setColor(new Color(120, 120, 120));
                g2d.drawLine(margemEsquerda, y, margemEsquerda + larguraPredio, y);
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
            int xPainel = margemEsquerda + larguraPredio - 120;
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
            int x = margemEsquerda + 50 + (elevadorIndex * (elevadorWidth + espacoEntreElevadores));
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
    }

    private void atualizarListaPessoas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de Pessoas:\n");
        sb.append("┌──────┬─────────────────────┬───────────┐\n");
        sb.append("│  ID  │    Posição Atual    │  Destino  │\n");
        sb.append("├──────┼─────────────────────┼───────────┤\n");

        Ponteiro pAndares = predio.getAndares().getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            Ponteiro pPessoas = andar.getPessoasAguardando().getPonteiroInicio();
            while (pPessoas != null && pPessoas.isValido()) {
                Pessoa pessoa = (Pessoa) pPessoas.getElemento();
                sb.append(String.format("│ P%-3d │ Andar %-2d %-9s │ Andar %-3d │\n",
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
                sb.append(String.format("│ P%-3d │ Elevador %-1d (A%-2d)   │ Andar %-3d │\n",
                        pessoa.getId(),
                        elevador.getId(),
                        elevador.getAndarAtual(),
                        pessoa.getAndarDestino()));
                pPessoas = pPessoas.getProximo();
            }
            pElevadores = pElevadores.getProximo();
        }

        sb.append("└──────┴─────────────────────┴───────────┘\n");
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
}

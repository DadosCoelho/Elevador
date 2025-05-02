import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JTextField andaresField;
    private JTextField elevadoresField;
    private JTextField capacidadeField;
    private JTextField velocidadeField;
    private JTextField tempoPicoField;
    private JTextField tempoForaPicoField;
    private JComboBox<String> heuristicaCombo;
    private JComboBox<String> painelCombo;
    private JTextField pessoasField;

    public InterfaceGrafica() {
        setTitle("Simulador de Elevadores");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        configPanel = new JPanel(new GridBagLayout());

        simulationPanel = new JPanel(new BorderLayout());
        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (simulador != null && predio != null) {
                    paintSimulation(g);
                }
            }
        };
        drawingPanel.setPreferredSize(new Dimension(600, 600)); // Aumentar para melhor visualização
        pessoasTextArea = new JTextArea();
        pessoasTextArea.setEditable(false);
        pessoasTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(pessoasTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 600));
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setPreferredSize(new Dimension(600, 100));

        simulationPanel.add(drawingPanel, BorderLayout.CENTER);
        simulationPanel.add(scrollPane, BorderLayout.EAST);
        simulationPanel.add(statsPanel, BorderLayout.NORTH);

        setupConfigPanel();

        mainPanel.add(configPanel, "Config");
        mainPanel.add(simulationPanel, "Simulation");
        add(mainPanel);

        cardLayout.show(mainPanel, "Config");
    }

    private void setupConfigPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

        JTextField[] fields = new JTextField[6];
        andaresField = new JTextField("5", 10);
        elevadoresField = new JTextField("2", 10);
        capacidadeField = new JTextField("8", 10);
        velocidadeField = new JTextField("1000", 10);
        tempoPicoField = new JTextField("2", 10);
        tempoForaPicoField = new JTextField("1", 10);
        pessoasField = new JTextField("10", 10);
        fields[0] = andaresField;
        fields[1] = elevadoresField;
        fields[2] = capacidadeField;
        fields[3] = velocidadeField;
        fields[4] = tempoPicoField;
        fields[5] = tempoForaPicoField;

        String[] heuristicas = {"1 - Ordem de Chegada", "2 - Otimização de Tempo", "3 - Otimização de Energia"};
        heuristicaCombo = new JComboBox<>(heuristicas);
        heuristicaCombo.setSelectedIndex(0);

        String[] paineis = {"UNICO_BOTAO", "DOIS_BOTOES", "PAINEL_NUMERICO"};
        painelCombo = new JComboBox<>(paineis);
        painelCombo.setSelectedIndex(0);

        int row = 0;
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = row;
            configPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            if (i == 6) {
                configPanel.add(heuristicaCombo, gbc);
            } else if (i == 7) {
                configPanel.add(painelCombo, gbc);
            } else if (i == 8) {
                configPanel.add(pessoasField, gbc);
            } else {
                configPanel.add(fields[i], gbc);
            }
            row++;
        }

        JButton startButton = new JButton("Iniciar Simulação");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validarParametros()) {
                    iniciarSimulacao();
                    cardLayout.show(mainPanel, "Simulation");
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        configPanel.add(startButton, gbc);
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
                JOptionPane.showMessageDialog(this, "Quantidade de andares deve ser no mínimo 5.");
                return false;
            }
            if (elevadores < 1) {
                JOptionPane.showMessageDialog(this, "Quantidade de elevadores deve ser no mínimo 1.");
                return false;
            }
            if (capacidade < 1) {
                JOptionPane.showMessageDialog(this, "Capacidade máxima deve ser positiva.");
                return false;
            }
            if (velocidade < 1) {
                JOptionPane.showMessageDialog(this, "Velocidade de simulação deve ser positiva.");
                return false;
            }
            if (tempoPico < 1) {
                JOptionPane.showMessageDialog(this, "Tempo de viagem em pico deve ser positivo.");
                return false;
            }
            if (tempoForaPico < 1) {
                JOptionPane.showMessageDialog(this, "Tempo de viagem fora de pico deve ser positivo.");
                return false;
            }
            if (pessoas < 1) {
                JOptionPane.showMessageDialog(this, "Quantidade de pessoas deve ser positiva.");
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos válidos.");
            return false;
        }
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

        GerenciadorSimulacao gerenciador = new GerenciadorSimulacao();
        List<Pessoa> pessoas = gerenciador.gerarListaPessoas(quantidadePessoas, andares, null);
        for (Pessoa pessoa : pessoas) {
            Andar andar = getAndarPorNumero(predio, pessoa.getAndarOrigem());
            if (andar != null) {
                andar.adicionarPessoa(pessoa);
            }
        }

        simulador.iniciar();
    }

    private void paintSimulation(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        int quantidadeAndares = predio.getAndares().tamanho();
        int andarHeight = Math.max(50, getHeight() / (quantidadeAndares + 2)); // Ajuste dinâmico da altura
        int margemSuperior = 20;
        int yBase = getHeight() - margemSuperior; // Base da janela

        Ponteiro p = predio.getAndares().getInicio();
        int andarIndex = 0;
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            int y = yBase - (andarIndex * andarHeight);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(50, y - andarHeight, 100, andarHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(50, y - andarHeight, 100, andarHeight);
            g2d.drawString("Andar " + andar.getNumero(), 10, y - andarHeight / 2 + 5);
            g2d.drawString("Pessoas: " + andar.getPessoasAguardando().tamanho(), 160, y - andarHeight / 2 + 5);
            andarIndex++;
            p = p.getProximo();
        }

        Ponteiro pElevadores = predio.getCentral().getElevadores().getInicio();
        int x = 60;
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            int elevadorY = yBase - (elevador.getAndarAtual() * andarHeight) - andarHeight;
            g2d.setColor(Color.BLUE);
            g2d.fillRect(x, elevadorY, 30, andarHeight - 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString("E" + elevador.getId(), x + 5, elevadorY + (andarHeight - 10) / 2 + 5);
            x += 40;
            pElevadores = pElevadores.getProximo();
        }
    }

    public void atualizar() {
        atualizarListaPessoas();
        atualizarEstatisticas();
        simulationPanel.repaint();
    }

    private void atualizarListaPessoas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de Pessoas:\n");
        sb.append("ID | Posição Atual | Destino\n");
        sb.append("----------------------------\n");

        Ponteiro pAndares = predio.getAndares().getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            NoGenerico pPessoas = andar.getPessoasAguardando().getInicio();
            while (pPessoas != null) {
                Pessoa pessoa = (Pessoa) pPessoas.getElemento();
                sb.append(String.format("P%d | Andar %d (Aguardando) | Andar %d\n",
                        pessoa.getId(), andar.getNumero(), pessoa.getAndarDestino()));
                pPessoas = pPessoas.getProximo();
            }
            pAndares = pAndares.getProximo();
        }

        Ponteiro pElevadores = predio.getCentral().getElevadores().getInicio();
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            NoGenerico pPessoas = elevador.getPessoasNoElevador().getInicio();
            while (pPessoas != null) {
                Pessoa pessoa = (Pessoa) pPessoas.getElemento();
                sb.append(String.format("P%d | Elevador %d (Andar %d) | Andar %d\n",
                        pessoa.getId(), elevador.getId(), elevador.getAndarAtual(), pessoa.getAndarDestino()));
                pPessoas = pPessoas.getProximo();
            }
            pElevadores = pElevadores.getProximo();
        }

        pessoasTextArea.setText(sb.toString());
        if (pessoasTextArea.getText().trim().isEmpty()) {
            System.out.println("Debug: Lista de pessoas está vazia. Verifique a iteração ou os dados em Fila.");
        }
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
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InterfaceGrafica extends JFrame {
    private Predio predio;
    private Simulador simulador;

    public InterfaceGrafica(Simulador simulador) {
        this.simulador = simulador;
        this.predio = simulador.getPredio();
        setTitle("Simulador de Elevadores");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void paint(Graphics g) {
        // Criar um buffer para evitar flickering
        Image bufferImage = createImage(getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) bufferImage.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Definir fonte para melhor legibilidade
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        // Calcular altura total dos andares
        int quantidadeAndares = predio.getAndares().tamanho();
        int andarHeight = 50;
        int margemSuperior = 100; // Espaço para título e estatísticas
        int alturaTotal = quantidadeAndares * andarHeight;

        // Desenhar andares (do térreo na base para o último andar no topo)
        Ponteiro p = predio.getAndares().getInicio();
        int y = getHeight() - andarHeight - 50; // Começar da base da janela
        int andarIndex = 0;
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            // Desenhar retângulo do andar
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(50, y - andarIndex * andarHeight, 100, andarHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(50, y - andarIndex * andarHeight, 100, andarHeight);
            // Desenhar número do andar e quantidade de pessoas
            g2d.drawString("Andar " + andar.getNumero(), 10, y - andarIndex * andarHeight + 20);
            g2d.drawString("Pessoas: " + andar.getPessoasAguardando().tamanho(), 160, y - andarIndex * andarHeight + 20);
            andarIndex++;
            p = p.getProximo();
        }

        // Desenhar elevadores
        Ponteiro pElevadores = predio.getCentral().getElevadores().getInicio();
        int x = 60;
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            // Calcular a posição Y do elevador com base no andar atual
            int elevadorY = y - elevador.getAndarAtual() * andarHeight;
            g2d.setColor(Color.BLUE);
            g2d.fillRect(x, elevadorY, 30, andarHeight - 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString("E" + elevador.getId(), x + 5, elevadorY + 20);
            x += 40;
            pElevadores = pElevadores.getProximo();
        }

        // Desenhar estatísticas
        g2d.setColor(Color.BLACK);
        Estatisticas stats = simulador.getEstatisticas();
        int statsY = 50;
        g2d.drawString("Tempo Médio Espera: " + String.format("%.2f", stats.getTempoMedioEspera()) + " min", 300, statsY);
        g2d.drawString("Energia Consumida: " + String.format("%.2f", stats.getEnergiaConsumida()) + " un", 300, statsY + 20);
        g2d.drawString("Pessoas Transportadas: " + stats.getTotalPessoasTransportadas(), 300, statsY + 40);
        g2d.drawString("Minuto Simulado: " + simulador.getMinutoSimulado(), 300, statsY + 60);

        // Desenhar o buffer na tela
        g.drawImage(bufferImage, 0, 0, this);
        g2d.dispose();
    }

    public void atualizar() {
        repaint();
    }
}
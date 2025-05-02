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
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // Desenhar andares
        Ponteiro p = predio.getAndares().getInicio();
        int y = 50;
        int andarHeight = 50;
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            g2d.drawRect(50, y, 100, andarHeight);
            g2d.drawString("Andar " + andar.getNumero(), 10, y + 20);
            g2d.drawString("Pessoas: " + andar.getPessoasAguardando().tamanho(), 160, y + 20);
            y += andarHeight;
            p = p.getProximo();
        }

        // Desenhar elevadores
        Ponteiro pElevadores = predio.getCentral().getElevadores().getInicio();
        int x = 60;
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            int elevadorY = 50 + (predio.getAndares().tamanho() - elevador.getAndarAtual() - 1) * andarHeight;
            g2d.fillRect(x, elevadorY, 30, andarHeight - 10);
            g2d.drawString("E" + elevador.getId(), x + 5, elevadorY + 20);
            x += 40;
            pElevadores = pElevadores.getProximo();
        }

        // Desenhar estatísticas
        Estatisticas stats = simulador.getEstatisticas();
        g2d.drawString("Tempo Médio Espera: " + stats.getTempoMedioEspera(), 300, 50);
        g2d.drawString("Energia Consumida: " + stats.getEnergiaConsumida(), 300, 70);
        g2d.drawString("Pessoas Transportadas: " + stats.getTotalPessoasTransportadas(), 300, 90);
    }

    public void atualizar() {
        repaint();
    }
}
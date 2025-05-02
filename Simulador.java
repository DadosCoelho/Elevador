import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Simulador implements Serializable {
    private int minutoSimulado;
    private int velocidadeEmMs;
    private transient Timer timer;
    private boolean emExecucao;
    private Predio predio;
    private Estatisticas estatisticas;
    private transient InterfaceGrafica gui;

    public Simulador(int andares, int elevadores, int velocidadeEmMs, int capacidadeMaxima, int tempoViagemPorAndarPico, int tempoViagemPorAndarForaPico, int heuristica, TipoPainel tipoPainel) {
        this.minutoSimulado = 0;
        this.velocidadeEmMs = velocidadeEmMs;
        this.predio = new Predio(andares, elevadores, capacidadeMaxima, tempoViagemPorAndarPico, tempoViagemPorAndarForaPico, heuristica, tipoPainel, this);
        this.estatisticas = new Estatisticas();
        this.gui = new InterfaceGrafica(this);
        this.gui.setVisible(true);
    }

    public Estatisticas getEstatisticas() {
        return estatisticas;
    }

    public Predio getPredio() {
        return predio;
    }

    public int getMinutoSimulado() { // Novo método
        return minutoSimulado;
    }

    public void iniciar() {
        if (emExecucao) return;
        emExecucao = true;
        iniciarTimer();
        System.out.println("Simulação iniciada.");
        gui = new InterfaceGrafica(this);
        gui.setVisible(true);
    }

    public void pausar() {
        if (timer != null) {
            timer.cancel();
            emExecucao = false;
            System.out.println("Simulação pausada.");
        }
    }

    public void continuar() {
        if (!emExecucao) {
            iniciarTimer();
            emExecucao = true;
            System.out.println("Simulação retomada.");
        }
    }

    public void encerrar() {
        if (timer != null) timer.cancel();
        emExecucao = false;
        System.out.println("Simulação encerrada.");
    }

    private void iniciarTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                predio.atualizar(minutoSimulado++);
                gui.atualizar(); // Atualiza a interface gráfica a cada ciclo
            }
        }, 0, velocidadeEmMs);
    }

    // Método para restaurar a GUI após desserialização
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.gui = new InterfaceGrafica(this);
        this.gui.setVisible(true);
    }

    public void gravar(String nomeArquivo) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            out.writeObject(this);
            System.out.println("Simulação gravada em: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Simulador carregar(String nomeArquivo) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            Simulador sim = (Simulador) in.readObject();
            return sim;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isHorarioPico() {
        int horaSimulada = minutoSimulado / 60; // Converte minutos em horas
        return (horaSimulada >= 8 && horaSimulada < 10) || (horaSimulada >= 17 && horaSimulada < 19);
    }


}
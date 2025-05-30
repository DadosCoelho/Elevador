import java.io.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
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
    }

    public void setInterfaceGrafica(InterfaceGrafica gui) {
        this.gui = gui;
    }

    public Estatisticas getEstatisticas() {
        return estatisticas;
    }

    public Predio getPredio() {
        return predio;
    }

    public int getMinutoSimulado() {
        return minutoSimulado;
    }

    public void setMinutoSimulado(int minutoSimulado) {
        this.minutoSimulado = minutoSimulado;
    }

    public int getVelocidadeEmMs() {
        return velocidadeEmMs;
    }

    public void setVelocidadeSimulacao(int novaVelocidade) {
        this.velocidadeEmMs = novaVelocidade;
        if (emExecucao && timer != null) {
            timer.cancel();
            iniciarTimer();
        }
    }

    public long getTempoSimulacaoEmMs() {
        return (long) minutoSimulado * velocidadeEmMs;
    }

    public void iniciar() {
        if (emExecucao) return;
        emExecucao = true;
        iniciarTimer();
        System.out.println("Simulação iniciada.");
    }

    public void pausar() {
        if (timer != null) {
            timer.cancel();
            emExecucao = false;
            setVelocidadeSimulacao(1000);
            System.out.println("Simulação pausada.");
            if (gui != null) {
                SwingUtilities.invokeLater(() -> gui.resetarVelocidadeSlider());
            }
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
        if (gui != null) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(gui, "Simulação finalizada! Todas as pessoas chegaram aos seus destinos.");
                gui.restartButton.setVisible(true);
                gui.backToConfigButton.setVisible(true);
                gui.updateControlButtons(false);
            });
        }
    }

    private void iniciarTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                predio.atualizar(minutoSimulado++);
                if (todasPessoasChegaram()) {
                    encerrar();
                }
                atualizarInterface();
            }
        }, 0, velocidadeEmMs);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.gui = null;
        this.timer = null;
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
        int horaSimulada = minutoSimulado / 60;
        return (horaSimulada >= 8 && horaSimulada < 10) || (horaSimulada >= 17 && horaSimulada < 19);
    }

    public boolean todasPessoasChegaram() {
        if (gui != null && gui.pessoas != null) {
            Ponteiro p = gui.pessoas.getInicio();
            while (p != null && p.isValido()) {
                Pessoa pessoa = (Pessoa) p.getElemento();
                if (!pessoa.isChegouAoDestino()) {
                    return false;
                }
                p = p.getProximo();
            }
        }
        return true;
    }

    public void atualizarInterface() {
        if (gui != null) {
            SwingUtilities.invokeLater(() -> gui.atualizar());
        }
    }

    public boolean deveAdicionarPessoa(Pessoa pessoa) {
        return minutoSimulado >= pessoa.getMinutoChegada();
    }
}
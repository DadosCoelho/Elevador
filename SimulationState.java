import java.io.Serializable;

public class SimulationState implements Serializable {
    private Predio predio;
    private Simulador simulador;
    private Lista<Pessoa> pessoas;
    private int heuristica;
    private TipoPainel tipoPainel;
    private Estatisticas estatisticas;

    public SimulationState(Predio predio, Simulador simulador, Lista<Pessoa> pessoas, int heuristica, TipoPainel tipoPainel) {
        this.predio = predio;
        this.simulador = simulador;
        this.pessoas = pessoas;
        this.heuristica = heuristica;
        this.tipoPainel = tipoPainel;
        this.estatisticas = simulador.getEstatisticas(); // Obtém as estatísticas do simulador
    }

    // Getters
    public Predio getPredio() { return predio; }
    public Simulador getSimulador() { return simulador; }
    public Lista<Pessoa> getPessoas() { return pessoas; }
    public int getHeuristica() { return heuristica; }
    public TipoPainel getTipoPainel() { return tipoPainel; }
    public Estatisticas getEstatisticas() { return estatisticas; }
}
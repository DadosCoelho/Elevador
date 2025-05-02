public class Predio extends EntidadeSimulavel {
    private CentralDeControle central;
    private Lista andares;

    public Predio(int quantidadeAndares, int quantidadeElevadores, int capacidadeMaxima, int tempoViagemPorAndarPico, int tempoViagemPorAndarForaPico, int heuristica, TipoPainel tipoPainel, Simulador simulador) {
        central = new CentralDeControle(quantidadeElevadores, this, capacidadeMaxima, tempoViagemPorAndarPico, tempoViagemPorAndarForaPico, heuristica, simulador);
        andares = new Lista();
        for (int i = 0; i < quantidadeAndares; i++) {
            andares.inserirFim(new Andar(i, tipoPainel));
        }
    }

    public Lista getAndares() {
        return andares;
    }

    @Override
    public void atualizar(int minutoSimulado) {
        central.atualizar(minutoSimulado);
    }

    public CentralDeControle getCentral() {
        return central;
    }
}
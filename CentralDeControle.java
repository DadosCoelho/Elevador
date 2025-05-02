public class CentralDeControle extends EntidadeSimulavel {
    private Lista elevadores;
    private Simulador simulador;

    public CentralDeControle(int quantidadeElevadores, Predio predio, int capacidadeMaxima, int tempoViagemPorAndarPico, int tempoViagemPorAndarForaPico, int heuristica, Simulador simulador) {
        this.simulador = simulador;
        elevadores = new Lista();
        for (int i = 0; i < quantidadeElevadores; i++) {
            elevadores.inserirFim(new Elevador(i + 1, capacidadeMaxima, tempoViagemPorAndarPico, tempoViagemPorAndarForaPico, predio, heuristica));
        }
    }

    @Override
    public void atualizar(int minutoSimulado) {
        Ponteiro p = elevadores.getInicio();
        while (p != null && p.isValido()) {
            Elevador e = (Elevador) p.getElemento();
            e.atualizar(minutoSimulado);
            p = p.getProximo();
        }
    }

    public Lista getElevadores() {
        return elevadores;
    }

    public Simulador getSimulador() {
        return simulador;
    }
}
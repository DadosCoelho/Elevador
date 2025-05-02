public class Predio extends EntidadeSimulavel {
    private CentralDeControle central;
    private Lista andares;

    public Predio(int quantidadeAndares, int quantidadeElevadores, int capacidadeMaxima, int tempoViagemPorAndar) {
        central = new CentralDeControle(quantidadeElevadores, this, capacidadeMaxima, tempoViagemPorAndar);
        andares = new Lista();
        for (int i = 0; i < quantidadeAndares; i++) {
            andares.inserirFim(new Andar(i));
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
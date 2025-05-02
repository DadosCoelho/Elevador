public class CentralDeControle extends EntidadeSimulavel {
    private Lista elevadores;

    public CentralDeControle(int quantidadeElevadores, Predio predio, int capacidadeMaxima, int tempoViagemPorAndar) {
        elevadores = new Lista();
        for (int i = 0; i < quantidadeElevadores; i++) {
            elevadores.inserirFim(new Elevador(i + 1, capacidadeMaxima, tempoViagemPorAndar, predio, 2));
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
}
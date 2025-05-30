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

    public void resetar() {
        // Limpar andares
        Ponteiro pAndares = andares.getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            andar.getPessoasAguardando().limpar();
            andar.getPainel().resetar();
            pAndares = pAndares.getProximo();
        }

        // Limpar elevadores
        Ponteiro pElevadores = getCentral().getElevadores().getInicio();
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            elevador.getPessoasNoElevador().limpar();
            elevador.getDestinos().limpar();
            elevador.setAndarAtual(0);
            pElevadores = pElevadores.getProximo();
        }
    }

    public boolean pessoaJaNoSistema(Pessoa pessoa) {
        // Verificar se a pessoa está em algum andar
        Ponteiro pAndares = andares.getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            Ponteiro pPessoas = andar.getPessoasAguardando().getPonteiroInicio();
            while (pPessoas != null && pPessoas.isValido()) {
                Pessoa pessoaNaFila = (Pessoa) pPessoas.getElemento();
                if (pessoaNaFila.getId() == pessoa.getId()) {
                    return true; // Pessoa encontrada em um andar
                }
                pPessoas = pPessoas.getProximo();
            }
            pAndares = pAndares.getProximo();
        }

        // Verificar se a pessoa está em algum elevador
        Ponteiro pElevadores = central.getElevadores().getInicio();
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            Ponteiro pPessoas = elevador.getPessoasNoElevador().getPonteiroInicio();
            while (pPessoas != null && pPessoas.isValido()) {
                Pessoa pessoaNoElevador = (Pessoa) pPessoas.getElemento();
                if (pessoaNoElevador.getId() == pessoa.getId()) {
                    return true; // Pessoa encontrada em um elevador
                }
                pPessoas = pPessoas.getProximo();
            }
            pElevadores = pElevadores.getProximo();
        }

        return false; // Pessoa não encontrada no sistema
    }
}
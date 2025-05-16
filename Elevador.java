import java.io.Serializable;

public class Elevador extends EntidadeSimulavel {
    private int id;
    private int andarAtual;
    private int capacidadeMaxima;
    private Fila pessoasNoElevador;
    private boolean subindo;
    private Lista destinos;
    private int tempoViagemPorAndar;
    private boolean emMovimento;
    private Predio predio;
    private int heuristica;
    private int minutosRestantesParaMover;
    private int tempoViagemPorAndarPico;
    private int tempoViagemPorAndarForaPico;
    private Lista<LogElevador> logs;

    public Elevador(int id, int capacidadeMaxima, int tempoViagemPorAndarPico, int tempoViagemPorAndarForaPico, Predio predio, int heuristica) {
        this.id = id;
        this.andarAtual = 0;
        this.capacidadeMaxima = capacidadeMaxima;
        this.pessoasNoElevador = new Fila();
        this.subindo = true;
        this.destinos = new Lista();
        this.tempoViagemPorAndarPico = tempoViagemPorAndarPico;
        this.tempoViagemPorAndarForaPico = tempoViagemPorAndarForaPico;
        this.emMovimento = false;
        this.predio = predio;
        this.heuristica = heuristica;
        this.logs = new Lista<>();
    }

    private Andar getAndar(int numeroAndar) {
        Ponteiro p = predio.getAndares().getInicio();
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            if (andar.getNumero() == numeroAndar) {
                return andar;
            }
            p = p.getProximo();
        }
        return null;
    }

    private void desembarcarPessoas() {
        Fila temp = new Fila();
        while (!pessoasNoElevador.isVazia()) {
            Pessoa pessoa = (Pessoa) pessoasNoElevador.desenfileirar();
            if (pessoa.getAndarDestino() == andarAtual) {
                pessoa.sairElevador();
                pessoa.setDentroElevador(false);
                pessoa.setChegouAoDestino(true);
                System.out.println("Pessoa " + pessoa.getId() + " desembarcou no andar " + andarAtual);
                adicionarLog(((Predio)predio).getCentral().getSimulador().getMinutoSimulado(), "Pessoa " + pessoa.getId() + " desembarcou no andar " + andarAtual);
            } else {
                temp.enfileirar(pessoa);
            }
        }
        while (!temp.isVazia()) {
            pessoasNoElevador.enfileirar(temp.desenfileirar());
        }
    }

    private void embarcarPessoas(int andarAtual) {
        Andar andar = getAndar(andarAtual);
        Fila pessoasAguardando = andar.getPessoasAguardando();
        Fila temp = new Fila();
        Simulador simulador = ((Predio) predio).getCentral().getSimulador();

        while (!pessoasAguardando.isVazia() && pessoasNoElevador.tamanho() < capacidadeMaxima) {
            Pessoa pessoa = (Pessoa) pessoasAguardando.desenfileirar();
            if (pessoa.isPrioritaria()) {
                pessoa.entrarElevador();
                pessoasNoElevador.enfileirar(pessoa);
                destinos.inserirFim(pessoa.getAndarDestino());
                int tempoEspera = simulador.getMinutoSimulado() - pessoa.getMinutoChegada();
                simulador.getEstatisticas().registrarEspera(tempoEspera);
                simulador.getEstatisticas().registrarChamadaAtendida();
                simulador.getEstatisticas().registrarPessoaTransportada();
                System.out.println("Pessoa " + pessoa.getId() + " (prioritária) embarcou no andar " + andarAtual);
                adicionarLog(simulador.getMinutoSimulado(), "Pessoa " + pessoa.getId() + " (prioritária) embarcou no andar " + andarAtual);
            } else {
                temp.enfileirar(pessoa);
            }
        }

        while (!temp.isVazia() && pessoasNoElevador.tamanho() < capacidadeMaxima) {
            Pessoa pessoa = (Pessoa) temp.desenfileirar();
            pessoa.entrarElevador();
            pessoasNoElevador.enfileirar(pessoa);
            destinos.inserirFim(pessoa.getAndarDestino());
            int tempoEspera = simulador.getMinutoSimulado() - pessoa.getMinutoChegada();
            simulador.getEstatisticas().registrarEspera(tempoEspera);
            simulador.getEstatisticas().registrarChamadaAtendida();
            simulador.getEstatisticas().registrarPessoaTransportada();
            System.out.println("Pessoa " + pessoa.getId() + " embarcou no andar " + andarAtual);
            adicionarLog(simulador.getMinutoSimulado(), "Pessoa " + pessoa.getId() + " embarcou no andar " + andarAtual);
        }

        while (!temp.isVazia()) {
            pessoasAguardando.enfileirar(temp.desenfileirar());
        }

        PainelElevador painel = andar.getPainel();
        if (pessoasAguardando.isVazia() &&
                (painel.getTipoPainel() != TipoPainel.PAINEL_NUMERICO || painel.getAndaresDestino().isVazia())) {
            painel.resetar();
        }
    }

    @Override
    public void atualizar(int minutoSimulado) {
        Simulador simulador = ((Predio) predio).getCentral().getSimulador();
        int tempoViagemPorAndar = simulador.isHorarioPico() ? tempoViagemPorAndarPico : tempoViagemPorAndarForaPico;

        if (destinos.contem(andarAtual)) {
            simulador.getEstatisticas().registrarEnergia(0.5);
        }
        if (emMovimento && minutosRestantesParaMover == 0 && destinos.getInicio() != null) {
            simulador.getEstatisticas().registrarEnergia(1.0);
        }
        if (emMovimento && minutosRestantesParaMover > 0) {
            minutosRestantesParaMover--;
            return;
        }
        if (emMovimento) {
            Ponteiro p = destinos.getInicio();
            if (p != null && p.isValido()) {
                int proximoDestino = (Integer) p.getElemento();
                if (proximoDestino > andarAtual) {
                    andarAtual++;
                } else if (proximoDestino < andarAtual) {
                    andarAtual--;
                }
                if (andarAtual == proximoDestino) {
                    emMovimento = false;
                } else {
                    minutosRestantesParaMover = tempoViagemPorAndar - 1;
                }
            }
        } else {
            if (destinos.contem(andarAtual)) {
                desembarcarPessoas();
                embarcarPessoas(andarAtual);
                destinos.remover(andarAtual);
            }
            ((Predio) predio).getCentral().escolherProximoDestino(this);
            if (!destinos.isVazia()) {
                emMovimento = true;
                minutosRestantesParaMover = tempoViagemPorAndar;
            }
        }
        System.out.println("Elevador " + id + " no andar " + andarAtual + ", minuto " + minutoSimulado + ", " +
                pessoasNoElevador.tamanho() + " pessoas a bordo");
    }

    public int getId() {
        return id;
    }

    public int getAndarAtual() {
        return andarAtual;
    }

    public void setAndarAtual(int andarAtual) {
        this.andarAtual = andarAtual;
    }

    public Fila getPessoasNoElevador() {
        return pessoasNoElevador;
    }

    public Lista getDestinos() {
        return destinos;
    }

    public void removerPessoa(Pessoa pessoa) {
        Fila novaFila = new Fila();
        Ponteiro p = pessoasNoElevador.getPonteiroInicio();
        while (p != null && p.isValido()) {
            Pessoa pessoaNaFila = (Pessoa) p.getElemento();
            if (pessoaNaFila.getId() != pessoa.getId()) {
                novaFila.enfileirar(pessoaNaFila);
            }
            p = p.getProximo();
        }
        pessoasNoElevador = novaFila;
    }

    public void adicionarLog(int minutoSimulado, String decisao) {
        LogElevador log = new LogElevador(minutoSimulado, andarAtual, decisao, id);
        logs.inserirFim(log);
    }

    public Lista<LogElevador> getLogs() {
        return logs;
    }

    public boolean isSubindo() {
        return subindo;
    }

    public void setSubindo(boolean subindo) {
        this.subindo = subindo;
    }

    public int getHeuristica() {
        return heuristica;
    }

    public Predio getPredio() {
        return predio;
    }
}
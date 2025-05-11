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
    private Lista<LogElevador> logs; // Lista de logs

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
        this.logs = new Lista<>(); // Inicializa a lista de logs
    }

    private void escolherProximoDestino() {
        if (heuristica == 1) { // Modelo 1: Sem heurística, ordem de chegada
            if (!destinos.isVazia()) {
                return;
            }
            Ponteiro p = predio.getAndares().getInicio();
            while (p != null && p.isValido()) {
                Andar andar = (Andar) p.getElemento();
                PainelElevador painel = andar.getPainel();
                if ((painel.getTipoPainel() == TipoPainel.UNICO_BOTAO && painel.isChamadaGeralAtivada()) ||
                        (painel.getTipoPainel() == TipoPainel.DOIS_BOTOES &&
                                ((painel.isBotaoSubirAtivado() && subindo) || (painel.isBotaoDescerAtivado() && !subindo))) ||
                        (painel.getTipoPainel() == TipoPainel.PAINEL_NUMERICO && !painel.getAndaresDestino().isVazia()) ||
                        !andar.getPessoasAguardando().isVazia()) {
                    destinos.inserirFim(andar.getNumero());
                    adicionarLog(((Predio)predio).getCentral().getSimulador().getMinutoSimulado(), "Escolheu andar " + andar.getNumero() + " como próximo destino (Modelo 1)"); // Adiciona log
                    break;
                }
                p = p.getProximo();
            }
        } else if (heuristica == 2) { // Modelo 2: Otimização de tempo
            int maiorFila = 0;
            int andarEscolhido = -1;
            Ponteiro p = predio.getAndares().getInicio();
            while (p != null && p.isValido()) {
                Andar andar = (Andar) p.getElemento();
                int tamanhoFila = andar.getPessoasAguardando().tamanho();
                if (tamanhoFila > maiorFila && (subindo ? andar.getNumero() > andarAtual : andar.getNumero() < andarAtual)) {
                    maiorFila = tamanhoFila;
                    andarEscolhido = andar.getNumero();
                }
                p = p.getProximo();
            }
            if (andarEscolhido != -1) {
                destinos.inserirFim(andarEscolhido);
                adicionarLog(((Predio)predio).getCentral().getSimulador().getMinutoSimulado(), "Escolheu andar " + andarEscolhido + " como próximo destino (Modelo 2)"); // Adiciona log
            }
        } else if (heuristica == 3) { // Modelo 3: Otimização de energia
            int destinoMaisProximo = -1;
            int menorDistancia = Integer.MAX_VALUE;
            Ponteiro p = predio.getAndares().getInicio();
            while (p != null && p.isValido()) {
                Andar andar = (Andar) p.getElemento();
                PainelElevador painel = andar.getPainel();
                boolean temChamada = !andar.getPessoasAguardando().isVazia() ||
                        (painel.getTipoPainel() == TipoPainel.UNICO_BOTAO && painel.isChamadaGeralAtivada()) ||
                        (painel.getTipoPainel() == TipoPainel.DOIS_BOTOES &&
                                (painel.isBotaoSubirAtivado() || painel.isBotaoDescerAtivado())) ||
                        (painel.getTipoPainel() == TipoPainel.PAINEL_NUMERICO && !painel.getAndaresDestino().isVazia());
                if (temChamada) {
                    int distancia = Math.abs(andar.getNumero() - andarAtual);
                    if (distancia < menorDistancia && (subindo ? andar.getNumero() >= andarAtual : andar.getNumero() <= andarAtual)) {
                        menorDistancia = distancia;
                        destinoMaisProximo = andar.getNumero();
                    }
                }
                p = p.getProximo();
            }
            if (destinoMaisProximo != -1) {
                destinos.inserirFim(destinoMaisProximo);
                adicionarLog(((Predio)predio).getCentral().getSimulador().getMinutoSimulado(), "Escolheu andar " + destinoMaisProximo + " como próximo destino (Modelo 3)"); // Adiciona log
            }
        }
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
                pessoa.setDentroElevador(false); // Garante que a pessoa não está mais no elevador
                pessoa.setChegouAoDestino(true); // Marca a pessoa como tendo chegado ao destino
                System.out.println("Pessoa " + pessoa.getId() + " desembarcou no andar " + andarAtual);
                adicionarLog(((Predio)predio).getCentral().getSimulador().getMinutoSimulado(), "Pessoa " + pessoa.getId() + " desembarcou no andar " + andarAtual); // Adiciona log
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
                adicionarLog(simulador.getMinutoSimulado(), "Pessoa " + pessoa.getId() + " (prioritária) embarcou no andar " + andarAtual); // Adiciona log
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
            adicionarLog(simulador.getMinutoSimulado(), "Pessoa " + pessoa.getId() + " embarcou no andar " + andarAtual); // Adiciona log
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
            escolherProximoDestino();
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

    // Método para adicionar um log à lista de logs
    public void adicionarLog(int minutoSimulado, String decisao) {
        LogElevador log = new LogElevador(minutoSimulado, andarAtual, decisao, id);
        logs.inserirFim(log);
    }

    // Método para obter a lista de logs
    public Lista<LogElevador> getLogs() {
        return logs;
    }
}
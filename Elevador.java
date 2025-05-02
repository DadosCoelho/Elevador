public class Elevador extends EntidadeSimulavel {
    private int id;
    private int andarAtual; // Andar onde o elevador está
    private int capacidadeMaxima; // Número máximo de pessoas
    private Fila pessoasNoElevador; // Pessoas atualmente no elevador
    private boolean subindo; // Direção atual (true = subindo, false = descendo)
    private Lista destinos; // Lista de andares destino (chamadas internas e externas)
    private int tempoViagemPorAndar; // Tempo (em minutos simulados) por andar
    private boolean emMovimento; // Elevador está se movendo ou parado
    private Predio predio;
    private int heuristica; // Heurística de controle
    private int minutosRestantesParaMover;

    public Elevador(int id, int capacidadeMaxima, int tempoViagemPorAndar, Predio predio, int heuristica) {
        this.id = id;
        this.andarAtual = 0;
        this.capacidadeMaxima = capacidadeMaxima;
        this.pessoasNoElevador = new Fila();
        this.subindo = true;
        this.destinos = new Lista();
        this.tempoViagemPorAndar = tempoViagemPorAndar;
        this.emMovimento = false;
        this.predio = predio;
        this.heuristica = heuristica;
    }

    private void escolherProximoDestino() {
        if (heuristica == 2) { // Otimização de tempo
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
            }
        }
        // Implementar Modelo 1 e 3
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
        Fila temp = new Fila(); // Fila temporária para manter pessoas que não desembarcam
        while (!pessoasNoElevador.isVazia()) {
            Pessoa pessoa = (Pessoa) pessoasNoElevador.desenfileirar();
            if (pessoa.getAndarDestino() == andarAtual) {
                pessoa.sairElevador();
                System.out.println("Pessoa " + pessoa.getId() + " desembarcou no andar " + andarAtual);
            } else {
                temp.enfileirar(pessoa);
            }
        }
        // Restaura pessoas que não desembarcaram
        while (!temp.isVazia()) {
            pessoasNoElevador.enfileirar(temp.desenfileirar());
        }
    }

    private void embarcarPessoas(int andarAtual) {
        Andar andar = getAndar(andarAtual); // Método auxiliar para obter o andar
        Fila pessoasAguardando = andar.getPessoasAguardando();
        Fila temp = new Fila(); // Fila temporária para manter pessoas que não embarcam

        // Primeiro, embarca pessoas prioritárias
        while (!pessoasAguardando.isVazia() && pessoasNoElevador.tamanho() < capacidadeMaxima) {
            Pessoa pessoa = (Pessoa) pessoasAguardando.desenfileirar();
            if (pessoa.isPrioritaria()) {
                pessoa.entrarElevador();
                pessoasNoElevador.enfileirar(pessoa);
                destinos.inserirFim(pessoa.getAndarDestino());
                System.out.println("Pessoa " + pessoa.getId() + " (prioritária) embarcou no andar " + andarAtual);
            } else {
                temp.enfileirar(pessoa);
            }
        }

        // Depois, embarca pessoas não prioritárias
        while (!temp.isVazia() && pessoasNoElevador.tamanho() < capacidadeMaxima) {
            Pessoa pessoa = (Pessoa) temp.desenfileirar();
            pessoa.entrarElevador();
            pessoasNoElevador.enfileirar(pessoa);
            destinos.inserirFim(pessoa.getAndarDestino());
            System.out.println("Pessoa " + pessoa.getId() + " embarcou no andar " + andarAtual);
        }

        // Restaura pessoas que não embarcaram
        while (!temp.isVazia()) {
            pessoasAguardando.enfileirar(temp.desenfileirar());
        }

        // Reseta o painel após atender chamadas
        if (pessoasAguardando.isVazia()) {
            andar.getPainel().resetar();
        }
    }

    @Override
    public void atualizar(int minutoSimulado) {
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

    // Novos getters para corrigir os erros
    public int getId() {
        return id;
    }

    public int getAndarAtual() {
        return andarAtual;
    }

    public Fila getPessoasNoElevador() {
        return pessoasNoElevador;
    }
}
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar parâmetros
        System.out.print("Digite a quantidade de andares (mínimo 5): ");
        int quantidadeAndares = scanner.nextInt();
        if (quantidadeAndares < 5) {
            System.out.println("Erro: Mínimo de 5 andares.");
            return;
        }

        System.out.print("Digite a quantidade de elevadores (mínimo 1): ");
        int quantidadeElevadores = scanner.nextInt();
        if (quantidadeElevadores < 1) {
            System.out.println("Erro: Mínimo de 1 elevador.");
            return;
        }

        System.out.print("Digite a capacidade máxima por elevador: ");
        int capacidadeMaxima = scanner.nextInt();
        if (capacidadeMaxima < 1) {
            System.out.println("Erro: Capacidade máxima deve ser positiva.");
            return;
        }

        System.out.print("Digite a velocidade de simulação (ms): ");
        int velocidadeSimulacaoMs = scanner.nextInt();
        if (velocidadeSimulacaoMs < 1) {
            System.out.println("Erro: Velocidade deve ser positiva.");
            return;
        }

        System.out.print("Digite o tempo de viagem por andar em horário de pico (minutos): ");
        int tempoViagemPico = scanner.nextInt();
        if (tempoViagemPico < 1) {
            System.out.println("Erro: Tempo de viagem em pico deve ser positivo.");
            return;
        }

        System.out.print("Digite o tempo de viagem por andar fora de pico (minutos): ");
        int tempoViagemForaPico = scanner.nextInt();
        if (tempoViagemForaPico < 1) {
            System.out.println("Erro: Tempo de viagem fora de pico deve ser positivo.");
            return;
        }

        System.out.print("Digite a heurística (1: Ordem de Chegada, 2: Otimização de Tempo, 3: Otimização de Energia): ");
        int heuristica = scanner.nextInt();
        if (heuristica < 1 || heuristica > 3) {
            System.out.println("Erro: Heurística deve ser 1, 2 ou 3.");
            return;
        }

        System.out.print("Digite o tipo de painel (UNICO_BOTAO, DOIS_BOTOES, PAINEL_NUMERICO): ");
        String tipoPainelStr = scanner.next();
        TipoPainel tipoPainel;
        try {
            tipoPainel = TipoPainel.valueOf(tipoPainelStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: Tipo de painel inválido.");
            return;
        }

        System.out.print("Digite a quantidade de pessoas: ");
        int quantidadePessoas = scanner.nextInt();
        if (quantidadePessoas < 1) {
            System.out.println("Erro: Quantidade de pessoas deve ser positiva.");
            return;
        }

        // Criar simulador
        Simulador sim = new Simulador(
                quantidadeAndares,
                quantidadeElevadores,
                velocidadeSimulacaoMs,
                capacidadeMaxima,
                tempoViagemPico,
                tempoViagemForaPico,
                heuristica,
                tipoPainel
        );
        Predio predio = sim.getPredio();

        // Gerar lista de pessoas
        GerenciadorSimulacao gerenciador = new GerenciadorSimulacao();
        List<Pessoa> pessoas = gerenciador.gerarListaPessoas(quantidadePessoas, quantidadeAndares, scanner);

        // Adicionar pessoas aos andares
        for (Pessoa pessoa : pessoas) {
            Andar andar = getAndarPorNumero(predio, pessoa.getAndarOrigem());
            if (andar == null) {
                System.out.println("Erro: Andar " + pessoa.getAndarOrigem() + " não encontrado.");
                continue;
            }
            andar.adicionarPessoa(pessoa);
        }
        System.out.println("Pessoas adicionadas aos andares.");

        // Listar estado inicial dos andares
        System.out.println("\nEstado inicial dos andares:");
        Ponteiro pAndares = predio.getAndares().getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            System.out.println("Andar " + andar.getNumero() + ":");
            andar.listarPessoasAguardando();
            pAndares = pAndares.getProximo();
        }

        // Iniciar simulação
        System.out.println("\nIniciando simulação...");
        sim.iniciar();

        // Executar até todas as pessoas chegarem aos destinos
        while (!sim.todasPessoasChegaram()) {
            try {
                Thread.sleep(velocidadeSimulacaoMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Encerrar simulação
        sim.encerrar();
        System.out.println("Simulação encerrada.");

        // Mostrar estatísticas
        Estatisticas stats = sim.getEstatisticas();
        System.out.println("\nEstatísticas da Simulação:");
        System.out.println("Tempo médio de espera: " + stats.getTempoMedioEspera() + " minutos");
        System.out.println("Chamadas atendidas: " + stats.getChamadasAtendidas());
        System.out.println("Energia consumida: " + stats.getEnergiaConsumida() + " unidades");
        System.out.println("Pessoas transportadas: " + stats.getTotalPessoasTransportadas());

        // Mostrar estado final dos andares
        System.out.println("\nEstado final dos andares:");
        pAndares = predio.getAndares().getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            System.out.println("Andar " + andar.getNumero() + ":");
            andar.listarPessoasAguardando();
            pAndares = pAndares.getProximo();
        }

        scanner.close();
    }

    private static Andar getAndarPorNumero(Predio predio, int numero) {
        Ponteiro p = predio.getAndares().getInicio();
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            if (andar.getNumero() == numero) {
                return andar;
            }
            p = p.getProximo();
        }
        return null;
    }
}
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Teste implements Serializable {
    public static void main(String[] args) {
        // Parâmetros configuráveis
        int quantidadeAndares = 5; // Número de andares
        int quantidadeElevadores = 2; // Número de elevadores
        int capacidadeMaxima = 8; // Capacidade máxima por elevador
        int velocidadeSimulacaoMs = 1000; // Velocidade da simulação em milissegundos
        int tempoViagemPico = 2; // Tempo de viagem por andar em horário de pico
        int tempoViagemForaPico = 1; // Tempo de viagem por andar fora de pico

        // Lista de pessoas para adicionar (ID, origem, destino, prioritária, minuto chegada)
        List<PessoaConfig> pessoas = new ArrayList<>();
        pessoas.add(new PessoaConfig(1, 0, 3, false, 0));
        pessoas.add(new PessoaConfig(2, 0, 2, true, 0));
        pessoas.add(new PessoaConfig(3, 2, 4, false, 0));
        pessoas.add(new PessoaConfig(4, 4, 1, true, 0));

        // Heurísticas e tipos de painel
        int[] heuristicas = {1, 2, 3};
        String[] heuristicaNomes = {"Modelo 1 (Ordem de Chegada)", "Modelo 2 (Otimização de Tempo)", "Modelo 3 (Otimização de Energia)"};
        TipoPainel[] tiposPaineis = {TipoPainel.UNICO_BOTAO, TipoPainel.DOIS_BOTOES, TipoPainel.PAINEL_NUMERICO};
        String[] painelNomes = {"Único Botão", "Dois Botões", "Painel Numérico"};

        // Testar cada combinação de heurística e tipo de painel
        for (int i = 0; i < heuristicas.length; i++) {
            for (int j = 0; j < tiposPaineis.length; j++) {
                int heuristica = heuristicas[i];
                TipoPainel tipoPainel = tiposPaineis[j];
                System.out.println("\n=== Testando " + heuristicaNomes[i] + " com " + painelNomes[j] + " ===");

                // Criar e configurar o simulador
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
                System.out.println("Simulador criado com " + predio.getAndares().tamanho() + " andares, " +
                        predio.getCentral().getElevadores().tamanho() + " elevadores e painel " + painelNomes[j] + ".");

                // Adicionar pessoas aos andares
                for (PessoaConfig config : pessoas) {
                    Andar andar = getAndarPorNumero(predio, config.andarOrigem);
                    if (andar == null) {
                        System.out.println("Erro: Andar " + config.andarOrigem + " não encontrado.");
                        continue;
                    }
                    Pessoa pessoa = new Pessoa(
                            config.id,
                            config.andarOrigem,
                            config.andarDestino,
                            config.prioritaria,
                            config.minutoChegada
                    );
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

                // Iniciar a simulação
                System.out.println("\nIniciando simulação para " + heuristicaNomes[i] + " com " + painelNomes[j]);
                sim.iniciar();
                try {
                    Thread.sleep(5000); // Simular por 5 segundos
                    sim.pausar();
                    System.out.println("Simulação pausada após 5 segundos.");

                    // Mostrar estado dos elevadores após pausa
                    System.out.println("\nEstado dos elevadores após pausa:");
                    CentralDeControle central = predio.getCentral();
                    Ponteiro pElevadores = central.getElevadores().getInicio();
                    while (pElevadores != null && pElevadores.isValido()) {
                        Elevador elevador = (Elevador) pElevadores.getElemento();
                        System.out.println("Elevador " + elevador.getId() + ": Andar atual=" + elevador.getAndarAtual() +
                                ", Pessoas a bordo=" + elevador.getPessoasNoElevador().tamanho());
                        pElevadores = pElevadores.getProximo();
                    }

                    Thread.sleep(2000); // Pausa por 2 segundos
                    sim.continuar();
                    System.out.println("Simulação retomada.");
                    Thread.sleep(3000); // Simular por mais 3 segundos
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

                    // Testar serialização
                    System.out.println("\nTestando serialização...");
                    String nomeArquivo = "simulacao_heuristica_" + heuristica + "_painel_" + tipoPainel.name() + ".dat";
                    sim.gravar(nomeArquivo);
                    Simulador simCarregado = Simulador.carregar(nomeArquivo);
                    if (simCarregado != null) {
                        System.out.println("Simulação carregada com sucesso: " +
                                simCarregado.getPredio().getAndares().tamanho() + " andares.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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

    // Classe auxiliar para configurar pessoas
    private static class PessoaConfig {
        int id;
        int andarOrigem;
        int andarDestino;
        boolean prioritaria;
        int minutoChegada;

        PessoaConfig(int id, int andarOrigem, int andarDestino, boolean prioritaria, int minutoChegada) {
            this.id = id;
            this.andarOrigem = andarOrigem;
            this.andarDestino = andarDestino;
            this.prioritaria = prioritaria;
            this.minutoChegada = minutoChegada;
        }
    }
}

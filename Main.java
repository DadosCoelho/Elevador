import java.io.Serializable;

public class Main implements Serializable {
    public static void main(String[] args) {
        // Testar cada heurística e tipo de painel
        int[] heuristicas = {1, 2, 3};
        String[] heuristicaNomes = {"Modelo 1 (Ordem de Chegada)", "Modelo 2 (Otimização de Tempo)", "Modelo 3 (Otimização de Energia)"};
        TipoPainel[] tiposPaineis = {TipoPainel.UNICO_BOTAO, TipoPainel.DOIS_BOTOES, TipoPainel.PAINEL_NUMERICO};
        String[] painelNomes = {"Único Botão", "Dois Botões", "Painel Numérico"};

        for (int i = 0; i < heuristicas.length; i++) {
            for (int j = 0; j < tiposPaineis.length; j++) {
                int heuristica = heuristicas[i];
                TipoPainel tipoPainel = tiposPaineis[j];
                System.out.println("\n=== Testando " + heuristicaNomes[i] + " com " + painelNomes[j] + " ===");

                // Criar e configurar o simulador
                Simulador sim = new Simulador(5, 2, 1000, 8, 1, heuristica, tipoPainel);
                Predio predio = sim.getPredio();
                System.out.println("Simulador criado com " + predio.getAndares().tamanho() + " andares, " +
                        predio.getCentral().getElevadores().tamanho() + " elevadores e painel " + painelNomes[j] + ".");

                // Acessar andares e adicionar pessoas
                Andar andar0 = getAndarPorNumero(predio, 0);
                Andar andar2 = getAndarPorNumero(predio, 2);
                Andar andar4 = getAndarPorNumero(predio, 4);

                if (andar0 == null || andar2 == null || andar4 == null) {
                    System.out.println("Erro: Não foi possível encontrar os andares especificados.");
                    return;
                }

                // Criar pessoas (normais e prioritárias)
                Pessoa p1 = new Pessoa(1, 0, 3, false, sim.getMinutoSimulado());
                Pessoa p2 = new Pessoa(2, 0, 2, true, sim.getMinutoSimulado());
                Pessoa p3 = new Pessoa(3, 2, 4, false, sim.getMinutoSimulado());
                Pessoa p4 = new Pessoa(4, 4, 1, true, sim.getMinutoSimulado());

                // Adicionar pessoas aos andares
                andar0.adicionarPessoa(p1);
                andar0.adicionarPessoa(p2);
                andar2.adicionarPessoa(p3);
                andar4.adicionarPessoa(p4);
                System.out.println("Pessoas adicionadas aos andares 0, 2 e 4.");

                // Listar pessoas aguardando em cada andar
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
                    Estatisticas stats = sim.getEstatisticas();
                    System.out.println("\nEstatísticas da Simulação:");
                    System.out.println("Tempo médio de espera: " + stats.getTempoMedioEspera() + " minutos");
                    System.out.println("Chamadas atendidas: " + stats.getChamadasAtendidas());
                    System.out.println("Energia consumida: " + stats.getEnergiaConsumida() + " unidades");
                    System.out.println("Pessoas transportadas: " + stats.getTotalPessoasTransportadas());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
}
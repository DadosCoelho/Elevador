import java.io.Serializable;

public class Main implements Serializable {
    public static void main(String[] args) {
        // Testar cada heurística (Modelo 1, 2 e 3)
        int[] heuristicas = {1, 2, 3};
        String[] heuristicaNomes = {"Modelo 1 (Ordem de Chegada)", "Modelo 2 (Otimização de Tempo)", "Modelo 3 (Otimização de Energia)"};

        for (int i = 0; i < heuristicas.length; i++) {
            int heuristica = heuristicas[i];
            System.out.println("\n=== Testando " + heuristicaNomes[i] + " ===");

            // 1. Criar e configurar o simulador
            Simulador sim = new Simulador(5, 2, 1000, 8, 1, heuristica); // 5 andares, 2 elevadores, 1000ms/minuto, capacidade 8, 1 minuto/andar, heurística
            Predio predio = sim.getPredio();
            System.out.println("Simulador criado com " + predio.getAndares().tamanho() + " andares e " +
                    predio.getCentral().getElevadores().tamanho() + " elevadores.");

            // 2. Acessar andares e adicionar pessoas
            Andar andar0 = getAndarPorNumero(predio, 0);
            Andar andar2 = getAndarPorNumero(predio, 2);
            Andar andar4 = getAndarPorNumero(predio, 4);

            if (andar0 == null || andar2 == null || andar4 == null) {
                System.out.println("Erro: Não foi possível encontrar os andares especificados.");
                return;
            }

            // Criar pessoas (normais e prioritárias)
            Pessoa p1 = new Pessoa(1, 0, 3, false); // Normal, de andar 0 para 3
            Pessoa p2 = new Pessoa(2, 0, 2, true);  // Prioritária, de andar 0 para 2
            Pessoa p3 = new Pessoa(3, 2, 4, false); // Normal, de andar 2 para 4
            Pessoa p4 = new Pessoa(4, 4, 1, true);  // Prioritária, de andar 4 para 1

            // Adicionar pessoas aos andares
            andar0.adicionarPessoa(p1);
            andar0.adicionarPessoa(p2);
            andar2.adicionarPessoa(p3);
            andar4.adicionarPessoa(p4);
            System.out.println("Pessoas adicionadas aos andares 0, 2 e 4.");

            // 3. Listar pessoas aguardando em cada andar
            System.out.println("\nEstado inicial dos andares:");
            Ponteiro pAndares = predio.getAndares().getInicio();
            while (pAndares != null && pAndares.isValido()) {
                Andar andar = (Andar) pAndares.getElemento();
                System.out.println("Andar " + andar.getNumero() + ":");
                andar.listarPessoasAguardando();
                pAndares = pAndares.getProximo();
            }

            // 4. Iniciar a simulação
            System.out.println("\nIniciando simulação para " + heuristicaNomes[i]);
            sim.iniciar();
            try {
                Thread.sleep(5000); // Simular por 5 segundos
                sim.pausar();
                System.out.println("Simulação pausada após 5 segundos.");

                // 5. Mostrar estado dos elevadores após pausa
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 6. Mostrar estado final dos andares
            System.out.println("\nEstado final dos andares:");
            pAndares = predio.getAndares().getInicio();
            while (pAndares != null && pAndares.isValido()) {
                Andar andar = (Andar) pAndares.getElemento();
                System.out.println("Andar " + andar.getNumero() + ":");
                andar.listarPessoasAguardando();
                pAndares = pAndares.getProximo();
            }

            // 7. Testar serialização
            System.out.println("\nTestando serialização...");
            sim.gravar("simulacao_heuristica_" + heuristica + ".dat");
            Simulador simCarregado = Simulador.carregar("simulacao_heuristica_" + heuristica + ".dat");
            if (simCarregado != null) {
                System.out.println("Simulação carregada com sucesso: " +
                        simCarregado.getPredio().getAndares().tamanho() + " andares.");
            }
        }
    }

    // Método auxiliar para obter andar por número
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
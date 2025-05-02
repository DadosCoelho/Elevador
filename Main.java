import java.io.Serializable;

public class Main implements Serializable {
    public static void main(String[] args) {
        // 1. Criar e configurar o simulador
        System.out.println("=== Inicializando Simulador ===");
        Simulador sim = new Simulador(5, 2, 1000, 8, 1); // 5 andares, 2 elevadores, 1000ms/minuto, capacidade 8, 1 minuto/andar
        Predio predio = sim.getPredio();
        System.out.println("Simulador criado com " + predio.getAndares().tamanho() + " andares e " +
                predio.getCentral().getElevadores().tamanho() + " elevadores.");

        // 2. Acessar andares e adicionar pessoas
        System.out.println("\n=== Configurando Andares e Pessoas ===");
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

        // 3. Demonstrar métodos de Pessoa
        System.out.println("\n=== Métodos de Pessoa ===");
        System.out.println("Pessoa 1: ID=" + p1.getId() + ", Origem=" + p1.getAndarOrigem() +
                ", Destino=" + p1.getAndarDestino() + ", Prioritária=" + p1.isPrioritaria() +
                ", No elevador=" + p1.estaDentroDoElevador());
        p1.entrarElevador();
        System.out.println("Pessoa 1 entrou no elevador: No elevador=" + p1.estaDentroDoElevador());
        p1.sairElevador();
        System.out.println("Pessoa 1 saiu do elevador: No elevador=" + p1.estaDentroDoElevador());

        // 4. Demonstrar métodos de PainelElevador
        System.out.println("\n=== Métodos de PainelElevador ===");
        PainelElevador painelAndar0 = andar0.getPainel();
        System.out.println("Painel andar 0: Botão subir=" + painelAndar0.isBotaoSubirAtivado() +
                ", Botão descer=" + painelAndar0.isBotaoDescerAtivado());
        painelAndar0.pressionarSubir();
        painelAndar0.pressionarAndar(3);
        System.out.println("Pressionado subir e andar 3 no painel do andar 0.");
        System.out.println("Painel andar 0 atualizado: Botão subir=" + painelAndar0.isBotaoSubirAtivado() +
                ", Destinos=" + painelAndar0.getAndaresDestino().tamanho());
        painelAndar0.resetar();
        System.out.println("Painel resetado: Botão subir=" + painelAndar0.isBotaoSubirAtivado() +
                ", Destinos=" + painelAndar0.getAndaresDestino().tamanho());

        // 5. Demonstrar métodos de Fila e FilaPrioridade
        System.out.println("\n=== Métodos de Fila/FilaPrioridade ===");
        Fila filaTeste = new FilaPrioridade();
        filaTeste.enfileirar(new Pessoa(5, 0, 1, false));
        filaTeste.enfileirar(new Pessoa(6, 0, 2, true));
        System.out.println("Fila teste: Tamanho=" + filaTeste.tamanho() + ", Vazia=" + filaTeste.isVazia());
        Pessoa pessoaDesenfileirada = (Pessoa) filaTeste.desenfileirar();
        System.out.println("Desenfileirada pessoa ID=" + pessoaDesenfileirada.getId() + " (prioritária)");
        System.out.println("Fila teste após desenfileirar: Tamanho=" + filaTeste.tamanho());

        // 6. Demonstrar iteração com Ponteiro em Lista e Fila
        System.out.println("\n=== Iteração com Ponteiro ===");
        System.out.println("Andares do prédio:");
        Ponteiro pAndares = predio.getAndares().getInicio();
        while (pAndares != null && pAndares.isValido()) {
            Andar andar = (Andar) pAndares.getElemento();
            System.out.println(" - Andar " + andar.getNumero() + ": " +
                    andar.getPessoasAguardando().tamanho() + " pessoas aguardando");
            pAndares.avancar(); // Usando o método aprimorado
        }

        System.out.println("Pessoas aguardando no andar 0:");
        Ponteiro pPessoas = andar0.getPessoasAguardando().getPonteiroInicio();
        while (pPessoas != null && pPessoas.isValido()) {
            Pessoa pessoa = (Pessoa) pPessoas.getElemento();
            System.out.println(" - Pessoa ID=" + pessoa.getId() + ", Destino=" +
                    pessoa.getAndarDestino() + ", Prioritária=" + pessoa.isPrioritaria());
            pPessoas = pPessoas.getProximo();
        }

        // 7. Demonstrar métodos de Elevador e CentralDeControle
        System.out.println("\n=== Métodos de CentralDeControle e Elevador ===");
        CentralDeControle central = predio.getCentral();
        Ponteiro pElevadores = central.getElevadores().getInicio();
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador elevador = (Elevador) pElevadores.getElemento();
            System.out.println("Elevador " + elevador.getId() + ": Andar atual=" + elevador.getAndarAtual() +
                    ", Pessoas a bordo=" + elevador.getPessoasNoElevador().tamanho());
            pElevadores = pElevadores.getProximo();
        }

        // 8. Iniciar a simulação
        System.out.println("\n=== Iniciando Simulação ===");
        sim.iniciar();
        try {
            Thread.sleep(5000); // Simular por 5 segundos
            sim.pausar();
            System.out.println("Simulação pausada após 5 segundos.");
            Thread.sleep(2000); // Pausa por 2 segundos
            sim.continuar();
            System.out.println("Simulação retomada.");
            Thread.sleep(3000); // Simular por mais 3 segundos
            sim.encerrar();
            System.out.println("Simulação encerrada.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 9. Demonstrar serialização
        System.out.println("\n=== Serialização ===");
        sim.gravar("simulacao.dat");
        Simulador simCarregado = Simulador.carregar("simulacao.dat");
        if (simCarregado != null) {
            System.out.println("Simulação carregada com sucesso: " +
                    simCarregado.getPredio().getAndares().tamanho() + " andares.");
        }

        // 10. Demonstrar métodos de Lista
        System.out.println("\n=== Métodos de Lista ===");
        Lista listaTeste = new Lista();
        listaTeste.inserirInicio("Item1");
        listaTeste.inserirFim("Item2");
        System.out.println("Lista teste: Tamanho=" + listaTeste.tamanho() + ", Vazia=" + listaTeste.isVazia());
        System.out.println("Contém 'Item1'? " + listaTeste.contem("Item1"));
        listaTeste.remover("Item1");
        System.out.println("Lista após remover 'Item1': Tamanho=" + listaTeste.tamanho());
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
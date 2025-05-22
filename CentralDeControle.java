public class CentralDeControle extends EntidadeSimulavel {
    private Lista elevadores;
    private Simulador simulador;

    public CentralDeControle(int quantidadeElevadores, Predio predio, int capacidadeMaxima, int tempoViagemPorAndarPico, int tempoViagemPorAndarForaPico, int heuristica, Simulador simulador) {
        this.simulador = simulador;
        elevadores = new Lista();
        for (int i = 0; i < quantidadeElevadores; i++) {
            elevadores.inserirFim(new Elevador(i + 1, capacidadeMaxima, tempoViagemPorAndarPico, tempoViagemPorAndarForaPico, predio, heuristica));
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

    public void escolherProximoDestino(Elevador elevador) {
        // Verifica os destinos já atribuídos a outros elevadores
        Lista destinosAtribuidos = new Lista();
        Ponteiro pElevadores = elevadores.getInicio();
        while (pElevadores != null && pElevadores.isValido()) {
            Elevador outroElevador = (Elevador) pElevadores.getElemento();
            if (outroElevador != elevador) {
                Ponteiro pDestinos = outroElevador.getDestinos().getInicio();
                while (pDestinos != null && pDestinos.isValido()) {
                    destinosAtribuidos.inserirFim(pDestinos.getElemento());
                    pDestinos = pDestinos.getProximo();
                }
            }
            pElevadores = pElevadores.getProximo();
        }

        // Verifica se há destinos internos (pessoas no elevador)
        Ponteiro pDestinosInternos = elevador.getDestinos().getInicio();
        while (pDestinosInternos != null && pDestinosInternos.isValido()) {
            int destino = (Integer) pDestinosInternos.getElemento();
            if (destino != elevador.getAndarAtual()) {
                return; // Já tem destinos pendentes
            }
            pDestinosInternos = pDestinosInternos.getProximo();
        }

        // Determina a direção do elevador
        boolean temChamadasNaDirecao = false;
        Ponteiro p = elevador.getPredio().getAndares().getInicio();
        while (p != null && p.isValido()) {
            Andar andar = (Andar) p.getElemento();
            if (destinosAtribuidos.contem(andar.getNumero())) {
                p = p.getProximo();
                continue;
            }
            PainelElevador painel = andar.getPainel();
            boolean temChamada = !andar.getPessoasAguardando().isVazia() ||
                    (painel.getTipoPainel() == TipoPainel.UNICO_BOTAO && painel.isChamadaGeralAtivada()) ||
                    (painel.getTipoPainel() == TipoPainel.DOIS_BOTOES &&
                            ((painel.isBotaoSubirAtivado() && elevador.isSubindo()) ||
                                    (painel.isBotaoDescerAtivado() && !elevador.isSubindo()))) ||
                    (painel.getTipoPainel() == TipoPainel.PAINEL_NUMERICO && !painel.getAndaresDestino().isVazia());
            if (temChamada && ((elevador.isSubindo() && andar.getNumero() > elevador.getAndarAtual()) ||
                    (!elevador.isSubindo() && andar.getNumero() < elevador.getAndarAtual()))) {
                temChamadasNaDirecao = true;
                break;
            }
            p = p.getProximo();
        }
        if (!temChamadasNaDirecao) {
            elevador.setSubindo(!elevador.isSubindo()); // Inverte a direção se não houver chamadas
        }

        if (elevador.getHeuristica() == 1) { // Modelo 1: Ordem de chegada otimizada
            // Coleta andares com chamadas válidas
            int[] andaresComChamadas = new int[elevador.getPredio().getAndares().tamanho()];
            int contador = 0;
            p = elevador.getPredio().getAndares().getInicio();
            while (p != null && p.isValido()) {
                Andar andar = (Andar) p.getElemento();
                if (destinosAtribuidos.contem(andar.getNumero())) {
                    p = p.getProximo();
                    continue;
                }
                PainelElevador painel = andar.getPainel();
                boolean temChamada = !andar.getPessoasAguardando().isVazia() ||
                        (painel.getTipoPainel() == TipoPainel.UNICO_BOTAO && painel.isChamadaGeralAtivada()) ||
                        (painel.getTipoPainel() == TipoPainel.DOIS_BOTOES &&
                                ((painel.isBotaoSubirAtivado() && elevador.isSubindo()) ||
                                        (painel.isBotaoDescerAtivado() && !elevador.isSubindo()))) ||
                        (painel.getTipoPainel() == TipoPainel.PAINEL_NUMERICO && !painel.getAndaresDestino().isVazia());
                if (temChamada && ((elevador.isSubindo() && andar.getNumero() > elevador.getAndarAtual()) ||
                        (!elevador.isSubindo() && andar.getNumero() < elevador.getAndarAtual()))) {
                    andaresComChamadas[contador++] = andar.getNumero();
                }
                p = p.getProximo();
            }

            // Ordena andares (crescente se subindo, decrescente se descendo)
            for (int i = 0; i < contador - 1; i++) {
                for (int j = i + 1; j < contador; j++) {
                    if (elevador.isSubindo() ? andaresComChamadas[i] > andaresComChamadas[j] : andaresComChamadas[i] < andaresComChamadas[j]) {
                        int temp = andaresComChamadas[i];
                        andaresComChamadas[i] = andaresComChamadas[j];
                        andaresComChamadas[j] = temp;
                    }
                }
            }

            // Adiciona o primeiro andar ordenado como destino
            if (contador > 0) {
                elevador.getDestinos().inserirFim(andaresComChamadas[0]);
                elevador.adicionarLog(simulador.getMinutoSimulado(), "Escolheu andar " + andaresComChamadas[0] + " como próximo destino (Modelo 1)");
            }
        } else if (elevador.getHeuristica() == 2) { // Modelo 2: Otimização de tempo
            int melhorPontuacao = -1;
            int andarEscolhido = -1;
            p = elevador.getPredio().getAndares().getInicio();
            while (p != null && p.isValido()) {
                Andar andar = (Andar) p.getElemento();
                if (destinosAtribuidos.contem(andar.getNumero())) {
                    p = p.getProximo();
                    continue;
                }
                PainelElevador painel = andar.getPainel();
                int tamanhoFila = andar.getPessoasAguardando().tamanho();
                int chamadasPainel = 0;
                if (painel.getTipoPainel() == TipoPainel.UNICO_BOTAO && painel.isChamadaGeralAtivada()) {
                    chamadasPainel += 1;
                } else if (painel.getTipoPainel() == TipoPainel.DOIS_BOTOES) {
                    if (painel.isBotaoSubirAtivado() && elevador.isSubindo()) chamadasPainel += 1;
                    if (painel.isBotaoDescerAtivado() && !elevador.isSubindo()) chamadasPainel += 1;
                } else if (painel.getTipoPainel() == TipoPainel.PAINEL_NUMERICO) {
                    chamadasPainel += painel.getAndaresDestino().tamanho();
                }
                int pontuacao = tamanhoFila + chamadasPainel;
                int distancia = Math.abs(andar.getNumero() - elevador.getAndarAtual());
                if (distancia > 0) {
                    pontuacao = pontuacao * 10 / distancia; // Pondera por distância
                }
                if (pontuacao > melhorPontuacao && ((elevador.isSubindo() && andar.getNumero() > elevador.getAndarAtual()) ||
                        (!elevador.isSubindo() && andar.getNumero() < elevador.getAndarAtual()))) {
                    melhorPontuacao = pontuacao;
                    andarEscolhido = andar.getNumero();
                }
                p = p.getProximo();
            }
            if (andarEscolhido != -1) {
                elevador.getDestinos().inserirFim(andarEscolhido);
                elevador.adicionarLog(simulador.getMinutoSimulado(), "Escolheu andar " + andarEscolhido + " como próximo destino (Modelo 2)");
            }
        } else if (elevador.getHeuristica() == 3) { // Modelo 3: Otimização de energia
            int destinoMaisProximo = -1;
            int menorDistancia = Integer.MAX_VALUE;
            int maiorFila = -1;
            p = elevador.getPredio().getAndares().getInicio();
            while (p != null && p.isValido()) {
                Andar andar = (Andar) p.getElemento();
                if (destinosAtribuidos.contem(andar.getNumero())) {
                    p = p.getProximo();
                    continue;
                }
                PainelElevador painel = andar.getPainel();
                boolean temChamada = !andar.getPessoasAguardando().isVazia() ||
                        (painel.getTipoPainel() == TipoPainel.UNICO_BOTAO && painel.isChamadaGeralAtivada()) ||
                        (painel.getTipoPainel() == TipoPainel.DOIS_BOTOES &&
                                ((painel.isBotaoSubirAtivado() && elevador.isSubindo()) ||
                                        (painel.isBotaoDescerAtivado() && !elevador.isSubindo()))) ||
                        (painel.getTipoPainel() == TipoPainel.PAINEL_NUMERICO && !painel.getAndaresDestino().isVazia());
                if (temChamada && ((elevador.isSubindo() && andar.getNumero() > elevador.getAndarAtual()) ||
                        (!elevador.isSubindo() && andar.getNumero() < elevador.getAndarAtual()))) {
                    int distancia = Math.abs(andar.getNumero() - elevador.getAndarAtual());
                    int tamanhoFila = andar.getPessoasAguardando().tamanho();
                    if (distancia < menorDistancia || (distancia == menorDistancia && tamanhoFila > maiorFila)) {
                        menorDistancia = distancia;
                        destinoMaisProximo = andar.getNumero();
                        maiorFila = tamanhoFila;
                    }
                }
                p = p.getProximo();
            }
            if (destinoMaisProximo != -1) {
                elevador.getDestinos().inserirFim(destinoMaisProximo);
                elevador.adicionarLog(simulador.getMinutoSimulado(), "Escolheu andar " + destinoMaisProximo + " como próximo destino (Modelo 3)");
            }
        }
    }

    public Lista getElevadores() {
        return elevadores;
    }

    public Simulador getSimulador() {
        return simulador;
    }
}
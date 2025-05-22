# Simulador de Elevadores

Bem-vindo ao **Simulador de Elevadores**, um projeto em Java que simula o funcionamento de elevadores em um prédio, com uma interface gráfica interativa e funcionalidades avançadas. Este projeto foi desenvolvido para modelar o comportamento de elevadores, considerando diferentes tipos de painéis de controle, heurísticas de movimentação, horários de pico e fora de pico, além de coletar métricas detalhadas sobre o desempenho do sistema.

## Funcionalidades

- **Interface Gráfica Interativa**: Interface desenvolvida com Swing, permitindo configurar parâmetros da simulação (número de andares, elevadores, capacidade, etc.) e visualizar o prédio, elevadores, pessoas e estatísticas em tempo real.
- **Tipos de Painéis de Controle**:
  - **Único Botão**: Um botão geral para chamar o elevador.
  - **Dois Botões**: Botões separados para subir e descer.
  - **Painel Numérico**: Permite selecionar diretamente o andar de destino.
- **Heurísticas de Movimentação**:
  - **Ordem de Chegada**: Atende chamadas na ordem em que são recebidas.
  - **Otimização de Tempo**: Prioriza andares com maior número de pessoas aguardando.
  - **Otimização de Energia**: Escolhe o andar mais próximo para reduzir o consumo de energia.
- **Horários de Pico e Fora de Pico**: Ajusta o tempo de viagem dos elevadores com base no horário, simulando maior demanda em períodos de pico.
- **Gerenciamento de Pessoas**: Suporte a pessoas com prioridade (ex.: idosos, PCDs), que são atendidas primeiro.
- **Coleta de Métricas**: Registra estatísticas como tempo médio de espera, chamadas atendidas, energia consumida e número de pessoas transportadas.
- **Logs de Elevadores**: Cada elevador mantém um registro de suas ações (embarques, desembarques, escolhas de destino), exibido na interface.
- **Controle da Simulação**: Botões para pausar, continuar, reiniciar ou voltar à configuração, além de um slider para ajustar a velocidade da simulação.

## Estrutura do Projeto

O projeto é organizado em classes que modelam os componentes do sistema:

- **Andar.java**: Representa um andar do prédio, gerenciando pessoas aguardando e o painel de controle.
- **Elevador.java**: Modela um elevador, com lógica para embarque/desembarque, movimentação e escolha de destinos com base na heurística selecionada.
- **CentralDeControle.java**: Coordena múltiplos elevadores, atualizando seus estados a cada minuto simulado.
- **InterfaceGrafica.java**: Implementa a interface gráfica com Swing, incluindo painéis de configuração, visualização do prédio, estatísticas e logs.
- **Estatisticas.java**: Coleta e calcula métricas da simulação.
- **Fila.java e FilaPrioridade.java**: Estruturas de dados para gerenciar filas de pessoas, com suporte a prioridade.
- **GerenciadorSimulacao.java**: Gera pessoas com atributos aleatórios (origem, destino, prioridade, minuto de chegada).
- **EntidadeSimulavel.java**: Classe abstrata para entidades que evoluem com o tempo na simulação.

## Requisitos

- **Java**: JDK 23 ou superior.
- **IDE**: Recomenda-se IntelliJ IDEA para facilitar a configuração do projeto.
- **Bibliotecas**: Utiliza apenas bibliotecas padrão do Java (Swing, AWT, etc.), sem dependências externas.

## Como Executar

1. **Clone baixado**: O repositório já foi clonado, como indicado no log do Git (`clone: from https://github.com/ricardosekeff/Elevador.git`).
2. **Configurar o Projeto**:
   - Abra o projeto na sua IDE (ex.: IntelliJ IDEA).
   - Certifique-se de que o JDK 23 está configurado (conforme `misc.xml`).
   - O arquivo `Elevador.iml` já define o módulo Java.
3. **Executar**:
   - Localize a classe `Main.java` (não fornecida nos arquivos, mas mencionada em `workspace.xml`).
   - Execute a aplicação a partir da classe `Main`.
4. **Configurar a Simulação**:
   - Na interface gráfica, insira os parâmetros desejados (andares, elevadores, capacidade, etc.).
   - Selecione a heurística e o tipo de painel.
   - Clique em "Iniciar Simulação".
5. **Interagir com a Simulação**:
   - Use os botões de pausa, continuação, reinício ou voltar à configuração.
   - Ajuste a velocidade da simulação com o slider.
   - Visualize o prédio, as estatísticas e os logs dos elevadores.

## Exemplo de Configuração

- **Andares**: 12
- **Elevadores**: 3
- **Capacidade Máxima**: 5 pessoas por elevador
- **Tempo de Viagem (Pico)**: 2 minutos por andar
- **Tempo de Viagem (Fora de Pico)**: 1 minuto por andar
- **Heurística**: Otimização de Energia
- **Painel**: Painel Numérico
- **Pessoas**: 300 (geradas aleatoriamente)

## Histórico de Desenvolvimento

O projeto foi desenvolvido em várias etapas, conforme os commits no Git:

- **V1**: Implementação inicial com heurísticas e tipos de painel.
- **Vswing**: Adição da interface gráfica com Swing.
- **Vswing-1**: Ajustes na lógica de pessoas e logs dos elevadores.
- **Último Commit** (7d9d293, 2025-05-14): Melhorias nos logs dos elevadores.

Consulte `.git/logs/` para mais detalhes sobre os commits.

## Contribuições

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do repositório.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`).
3. Commit suas alterações (`git commit -m "Adiciona nova funcionalidade"`).
4. Envie para o repositório remoto (`git push origin feature/nova-funcionalidade`).
5. Abra um Pull Request.

## Licença

Este projeto é de código aberto e está sob a licença MIT (a ser confirmada com o autor original).

## Contato

Para dúvidas ou sugestões, entre em contato com o autor:
- **Reinaldo Coelho**: [GitHub](https://github.com/DadosCoelho)

---

Desenvolvido com 💻 e ☕ por Reinaldo Coelho.
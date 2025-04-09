# Simulador de Elevadores - Mockup Orientado a Objetos

Este repositório contém um esqueleto de projeto em Java desenvolvido com foco no ensino de Programação Orientada a Objetos e Estruturas de Dados Dinâmicas. O projeto simula um sistema de elevadores em um prédio, com entidades que reagem a eventos temporais.

## 🎯 Objetivo

Fornecer um ponto de partida para alunos implementarem suas próprias lógicas de simulação de elevadores, explorando conceitos como:
- Abstração
- Herança
- Estruturas encadeadas (listas, filas, pilhas)
- Serialização de objetos
- Simulação baseada em tempo

---

## ▶️ Como usar

1. Clone este repositório ou baixe o `.zip`:
   ```bash
   git clone https://github.com/seunome/simulador-elevadores.git
   ```

2. Compile todos os arquivos `.java`:
   ```bash
   javac *.java
   ```

3. Execute a classe principal (caso deseje criar uma `Main.java`):
   ```bash
   java Main
   ```

4. Você pode gravar e carregar o estado da simulação utilizando os métodos `gravar()` e `carregar()` da classe `Simulador`.

---

## 🧱 Estrutura e Responsabilidades

| Classe                | Responsabilidade Principal |
|------------------------|---------------------------|
| `Simulador`            | Controla o tempo da simulação, gerencia o ciclo de vida da execução e faz a serialização do estado. |
| `EntidadeSimulavel`    | Classe abstrata para entidades que reagem ao tempo. Define o método `atualizar(minutoSimulado)`. |
| `Predio`               | Representa o prédio. Contém a central de controle e os andares. Atualiza suas entidades internas a cada ciclo. |
| `CentralDeControle`    | Gerencia todos os elevadores. Atualiza cada elevador a cada minuto simulado. |
| `Elevador`             | Representa um elevador. Os alunos devem implementar a lógica de movimentação aqui. |
| `Andar`                | Representa um andar do prédio. Contém um painel de chamadas e uma fila de pessoas aguardando. |
| `Pessoa`               | Representa um passageiro. Contém dados sobre origem, destino e se está dentro do elevador. |
| `PainelElevador`       | Painel de botões para chamar o elevador para subir ou descer. |
| `Lista`, `Fila`, etc.  | Estruturas de dados dinâmicas. Supõe-se que já foram implementadas pelos alunos ou professores. |

---

## 🛠️ Sugestões de Extensão

- Implementar lógica de movimentação dos elevadores.
- Criar chamadas automáticas de passageiros por minuto.
- Adicionar restrições de capacidade dos elevadores.
- Implementar logs de eventos da simulação.

---

## 📄 Licença

Este projeto é de uso acadêmico. Sinta-se livre para adaptá-lo conforme necessário.
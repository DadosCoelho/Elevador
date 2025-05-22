import java.util.Random;

public class GerenciadorSimulacao {
    public Lista<Pessoa> gerarListaPessoas(int quantidadePessoas, int quantidadeAndares, int percentualPrioridade, int minutoMaxChegada) {
        Random random = new Random();
        Lista<Pessoa> pessoas = new Lista<>();

        for (int i = 1; i <= quantidadePessoas; i++) {
            // Gerar andar de origem aleatoriamente
            int andarOrigem = random.nextInt(quantidadeAndares);

            // Gerar andar de destino aleatoriamente, garantindo que seja diferente da origem
            int andarDestino;
            do {
                andarDestino = random.nextInt(quantidadeAndares);
            } while (andarDestino == andarOrigem);

            // Gerar prioridade com base no percentual informado
            boolean prioritaria = random.nextInt(100) < percentualPrioridade;

            // Gerar minuto de chegada aleatoriamente, considerando o minuto máximo informado
            int minutoChegada = random.nextInt(minutoMaxChegada + 1);

            // Criar pessoa e adicionar à lista
            Pessoa pessoa = new Pessoa(i, andarOrigem, andarDestino, prioritaria, minutoChegada);
            pessoas.inserirFim(pessoa);
        }

        return pessoas;
    }
}
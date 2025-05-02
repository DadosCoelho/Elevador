import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GerenciadorSimulacao {
    public List<Pessoa> gerarListaPessoas(int quantidadePessoas, int quantidadeAndares, Scanner scanner) {
        Random random = new Random();
        List<Pessoa> pessoas = new ArrayList<>();

        for (int i = 1; i <= quantidadePessoas; i++) {
            // Gerar andar de origem aleatoriamente
            int andarOrigem = random.nextInt(quantidadeAndares);

            // Gerar andar de destino aleatoriamente, garantindo que seja diferente da origem
            int andarDestino;
            do {
                andarDestino = random.nextInt(quantidadeAndares);
            } while (andarDestino == andarOrigem);

            // Gerar prioridade aleatoriamente (50% de chance de ser prioritária)
            boolean prioritaria = random.nextBoolean();

            // Gerar minuto de chegada aleatoriamente (0 a 60 minutos)
            int minutoChegada = random.nextInt(61); // 0 a 60 minutos

            // Criar pessoa e adicionar à lista
            Pessoa pessoa = new Pessoa(i, andarOrigem, andarDestino, prioritaria, minutoChegada);
            pessoas.add(pessoa);
        }

        return pessoas;
    }
}
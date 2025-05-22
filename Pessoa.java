import java.io.Serializable;

public class Pessoa implements Serializable {
    private int id;
    private int andarOrigem;
    private int andarDestino;
    private boolean dentroElevador;
    private boolean prioritaria; // Cadeirante ou idoso
    private int minutoChegada;
    private boolean chegouAoDestino; // Novo atributo

    public Pessoa(int id, int origem, int destino, boolean prioritaria, int minutoChegada) {
        this.id = id;
        this.andarOrigem = origem;
        this.andarDestino = destino;
        this.dentroElevador = false;
        this.prioritaria = prioritaria;
        this.minutoChegada = minutoChegada;
        this.chegouAoDestino = false; // Inicialmente, a pessoa não chegou ao destino
    }

    public int getMinutoChegada() {
        return minutoChegada;
    }

    public int getId() {
        return id;
    }

    public int getAndarOrigem() {
        return andarOrigem;
    }

    public int getAndarDestino() {
        return andarDestino;
    }

    public boolean estaDentroDoElevador() {
        return dentroElevador;
    }

    public void entrarElevador() {
        this.dentroElevador = true;
    }

    public void sairElevador() {
        this.dentroElevador = false;
    }

    public boolean isPrioritaria() {
        return prioritaria;
    }

    public void setDentroElevador(boolean dentroElevador) {
        this.dentroElevador = dentroElevador;
    }

    public boolean isChegouAoDestino() {
        return chegouAoDestino;
    }

    public void setChegouAoDestino(boolean chegouAoDestino) {
        this.chegouAoDestino = chegouAoDestino;
    }

    public int getPosicaoAtual() {
        if (chegouAoDestino) {
            return andarDestino; // Retorna o andar de destino como posição atual
        } else if (dentroElevador) {
            return -1; // Retorna -1 para indicar que está dentro do elevador
        } else {
            return andarOrigem; // Retorna o andar de origem como posição atual
        }
    }
}
import java.io.Serializable;

public class Pessoa implements Serializable {
    private int id;
    private int andarOrigem;
    private int andarDestino;
    private boolean dentroElevador;
    private boolean prioritaria; // Cadeirante ou idoso
    private int minutoChegada;

    public Pessoa(int id, int origem, int destino, boolean prioritaria, int minutoChegada) {
        this.id = id;
        this.andarOrigem = origem;
        this.andarDestino = destino;
        this.dentroElevador = false;
        this.prioritaria = prioritaria;
        this.minutoChegada = minutoChegada;
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
}
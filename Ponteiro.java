import java.io.Serializable;

public class Ponteiro implements Serializable {
    private NoGenerico atual;

    public Ponteiro(NoGenerico no) {
        this.atual = no;
    }

    public Object getElemento() {
        return isValido() ? atual.getElemento() : null;
    }

    public Ponteiro getProximo() {
        return isValido() ? new Ponteiro(atual.getProximo()) : null;
    }

    public boolean isValido() {
        return atual != null;
    }

    // Novo método para avançar o ponteiro sem criar nova instância
    public void avancar() {
        if (isValido()) {
            atual = atual.getProximo();
        }
    }

    // Novo método para verificar se há próximo nó
    public boolean temProximo() {
        return isValido() && atual.getProximo() != null;
    }
}
import java.io.Serializable;

public class Lista<T> implements Serializable {
    public class No implements NoGenerico<T> {
        private T elemento;
        private No proximo;

        No(T elemento) {
            this.elemento = elemento;
            this.proximo = null;
        }

        @Override
        public T getElemento() {
            return elemento;
        }

        @Override
        public NoGenerico<T> getProximo() {
            return proximo;
        }

        @Override
        public void setProximo(NoGenerico<T> proximo) {
            this.proximo = (No) proximo;
        }
    }

    private No inicio;
    private int tamanho;

    public Lista() {
        this.inicio = null;
        this.tamanho = 0;
    }

    public void inserirFim(T elemento) {
        No novoNo = new No(elemento);
        if (inicio == null) {
            inicio = novoNo;
        } else {
            No atual = inicio;
            while (atual.proximo != null) {
                atual = atual.proximo;
            }
            atual.proximo = novoNo;
        }
        tamanho++;
    }

    public void inserirInicio(T elemento) {
        No novoNo = new No(elemento);
        novoNo.proximo = inicio;
        inicio = novoNo;
        tamanho++;
    }

    public Ponteiro getInicio() {
        return new Ponteiro(inicio);
    }

    public boolean isVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean contem(T elemento) {
        No atual = inicio;
        while (atual != null) {
            if (atual.elemento == null && elemento == null || atual.elemento != null && atual.elemento.equals(elemento)) {
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }

    public void remover(T elemento) {
        if (inicio == null) return;
        if (inicio.elemento != null && inicio.elemento.equals(elemento)) {
            inicio = inicio.proximo;
            tamanho--;
            return;
        }
        No atual = inicio;
        while (atual.proximo != null) {
            if (atual.proximo.elemento != null && atual.proximo.elemento.equals(elemento)) {
                atual.proximo = atual.proximo.proximo;
                tamanho--;
                return;
            }
            atual = atual.proximo;
        }
    }

    public void limpar() {
        inicio = null;
        tamanho = 0;
    }
}
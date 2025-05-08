import java.io.Serializable;

public class Fila implements Serializable {
    public class No implements NoGenerico {
        private Object elemento;
        private No proximo;

        No(Object elemento) {
            this.elemento = elemento;
            this.proximo = null;
        }

        @Override
        public Object getElemento() {
            return elemento;
        }

        @Override
        public NoGenerico getProximo() {
            return proximo;
        }

        @Override
        public void setProximo(NoGenerico proximo) {
            this.proximo = (No) proximo;
        }
    }

    protected No inicio;
    protected No fim;
    protected int tamanho;

    public Fila() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    public void enfileirar(Object elemento) {
        No novoNo = new No(elemento);
        if (isVazia()) {
            inicio = novoNo;
            fim = novoNo;
        } else {
            fim.setProximo(novoNo);
            fim = novoNo;
        }
        tamanho++;
    }

    public Object desenfileirar() {
        if (isVazia()) {
            return null;
        }
        Object elemento = inicio.getElemento();
        inicio = (No) inicio.getProximo();
        tamanho--;
        if (isVazia()) {
            fim = null;
        }
        return elemento;
    }

    public boolean isVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }

    protected No getInicio() {
        return inicio;
    }

    protected void setInicio(No inicio) {
        this.inicio = inicio;
    }

    protected void incrementaTamanho() {
        this.tamanho++;
    }

    // Novo método para suportar iteração com Ponteiro
    public Ponteiro getPonteiroInicio() {
        return new Ponteiro(inicio);
    }

    public void limpar() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }
}
import java.io.Serializable;

public class FilaPrioridade extends Fila implements Serializable {
    @Override
    public void enfileirar(Object elemento) {
        if (elemento instanceof Pessoa && ((Pessoa) elemento).isPrioritaria()) {
            No novoNo = new No(elemento);
            if (isVazia()) {
                super.enfileirar(elemento);
            } else {
                novoNo.setProximo(getInicio());
                setInicio(novoNo);
                incrementaTamanho();
            }
        } else {
            super.enfileirar(elemento);
        }
    }
}
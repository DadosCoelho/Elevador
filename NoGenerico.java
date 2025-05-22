import java.io.Serializable;

public interface NoGenerico<T> extends Serializable {
    T getElemento();
    NoGenerico<T> getProximo();
    void setProximo(NoGenerico<T> proximo);
}
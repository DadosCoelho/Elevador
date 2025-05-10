import java.io.Serializable;

public interface NoGenerico extends Serializable {
    Object getElemento();
    NoGenerico getProximo();
    void setProximo(NoGenerico proximo);
}
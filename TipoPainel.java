import java.io.Serializable;

public enum TipoPainel implements Serializable {
    UNICO_BOTAO, DOIS_BOTOES, PAINEL_NUMERICO;

    // Método estático para obter a constante pelo índice
    public static TipoPainel getByIndice(int indice) {
        TipoPainel[] valores = values();
        if (indice >= 0 && indice < valores.length) {
            return valores[indice];
        }
        throw new IllegalArgumentException("Índice inválido: " + indice);
    }
}
import java.io.Serializable;

public class PainelElevador implements Serializable {
    private boolean botaoSubirAtivado;
    private boolean botaoDescerAtivado;
    private boolean chamadaGeralAtivada;
    private Lista andaresDestino;
    private TipoPainel tipoPainel;

    public PainelElevador(TipoPainel tipoPainel) {
        this.tipoPainel = tipoPainel;
        this.botaoSubirAtivado = false;
        this.botaoDescerAtivado = false;
        this.chamadaGeralAtivada = false;
        this.andaresDestino = new Lista();
    }

    public void pressionarChamadaGeral() {
        if (tipoPainel == TipoPainel.UNICO_BOTAO) {
            chamadaGeralAtivada = true;
        }
    }

    public void pressionarSubir() {
        if (tipoPainel == TipoPainel.DOIS_BOTOES) {
            botaoSubirAtivado = true;
        }
    }

    public void pressionarDescer() {
        if (tipoPainel == TipoPainel.DOIS_BOTOES) {
            botaoDescerAtivado = true;
        }
    }

    public void pressionarAndar(int andar) {
        if (tipoPainel == TipoPainel.PAINEL_NUMERICO) {
            andaresDestino.inserirFim(andar);
        }
    }

    public void resetar() {
        botaoSubirAtivado = false;
        botaoDescerAtivado = false;
        chamadaGeralAtivada = false;
        if (tipoPainel != TipoPainel.PAINEL_NUMERICO) {
            andaresDestino = new Lista();
        }
    }

    public boolean isBotaoSubirAtivado() {
        return botaoSubirAtivado;
    }

    public boolean isBotaoDescerAtivado() {
        return botaoDescerAtivado;
    }

    public boolean isChamadaGeralAtivada() {
        return chamadaGeralAtivada;
    }

    public Lista getAndaresDestino() {
        return andaresDestino;
    }

    public TipoPainel getTipoPainel() {
        return tipoPainel;
    }
}
import java.io.Serializable;

public class PainelElevador implements Serializable {
    private boolean botaoSubirAtivado;
    private boolean botaoDescerAtivado;
    private Lista andaresDestino; // Para painel numérico

    public PainelElevador() {
        this.botaoSubirAtivado = false;
        this.botaoDescerAtivado = false;
        this.andaresDestino = new Lista();
    }

    public Lista getAndaresDestino() {
        return andaresDestino;
    }

    public void pressionarAndar(int andar) {
        andaresDestino.inserirFim(andar);
    }

    public void pressionarSubir() {
        botaoSubirAtivado = true;
    }

    public void pressionarDescer() {
        botaoDescerAtivado = true;
    }

    public void resetar() {
        botaoSubirAtivado = false;
        botaoDescerAtivado = false;
    }

    public boolean isBotaoSubirAtivado() {
        return botaoSubirAtivado;
    }

    public boolean isBotaoDescerAtivado() {
        return botaoDescerAtivado;
    }
}
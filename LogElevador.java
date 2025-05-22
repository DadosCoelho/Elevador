import java.io.Serializable;

public class LogElevador implements Serializable {
    private int minutoSimulado;
    private int andarAtual;
    private String decisao;
    private int idElevador;

    public LogElevador(int minutoSimulado, int andarAtual, String decisao, int idElevador) {
        this.minutoSimulado = minutoSimulado;
        this.andarAtual = andarAtual;
        this.decisao = decisao;
        this.idElevador = idElevador;
    }

    public int getMinutoSimulado() {
        return minutoSimulado;
    }

    public int getAndarAtual() {
        return andarAtual;
    }

    public String getDecisao() {
        return decisao;
    }

    public int getIdElevador() {
        return idElevador;
    }

    @Override
    public String toString() {
        return "Minuto: " + minutoSimulado +
                ", Elevador: " + idElevador +
                ", Andar: " + andarAtual +
                "\nDecisão: " + decisao + "\n__________________________________________________________";
    }
}
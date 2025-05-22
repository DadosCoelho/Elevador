import java.io.Serializable;
import java.util.ArrayList;

public class Estatisticas implements Serializable {
    private ArrayList<Integer> temposEspera;
    private int chamadasAtendidas;
    private double energiaConsumida;
    private int totalPessoasTransportadas;

    public Estatisticas() {
        temposEspera = new ArrayList<>();
        chamadasAtendidas = 0;
        energiaConsumida = 0.0;
        totalPessoasTransportadas = 0;
    }

    public void registrarEspera(int tempoEspera) {
        temposEspera.add(tempoEspera);
    }

    public void registrarChamadaAtendida() {
        chamadasAtendidas++;
    }

    public void registrarEnergia(double energia) {
        energiaConsumida += energia;
    }

    public void registrarPessoaTransportada() {
        totalPessoasTransportadas++;
    }

    public double getTempoMedioEspera() {
        if (temposEspera.isEmpty()) return 0.0;
        return temposEspera.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public int getChamadasAtendidas() {
        return chamadasAtendidas;
    }

    public double getEnergiaConsumida() {
        return energiaConsumida;
    }

    public int getTotalPessoasTransportadas() {
        return totalPessoasTransportadas;
    }

    public void zerar() {
        temposEspera.clear();
        chamadasAtendidas = 0;
        energiaConsumida = 0.0;
        totalPessoasTransportadas = 0;
    }
}
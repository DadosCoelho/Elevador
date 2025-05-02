import java.io.Serializable;

public class Andar implements Serializable {
    private int numero;
    private Fila pessoasAguardando;
    private PainelElevador painel;

    public Andar(int numero) {
        this.numero = numero;
        this.pessoasAguardando = new FilaPrioridade();
        this.painel = new PainelElevador();
    }

    public void adicionarPessoa(Pessoa pessoa) {
        pessoasAguardando.enfileirar(pessoa);
        if (pessoa.getAndarDestino() > numero) {
            painel.pressionarSubir();
        } else if (pessoa.getAndarDestino() < numero) {
            painel.pressionarDescer();
        }
    }

    public void listarPessoasAguardando() {
        Ponteiro p = pessoasAguardando.getPonteiroInicio();
        while (p != null && p.isValido()) {
            Pessoa pessoa = (Pessoa) p.getElemento();
            System.out.println("Pessoa " + pessoa.getId() + " aguardando no andar " + numero);
            p = p.getProximo();
        }
    }

    public int getNumero() {
        return numero;
    }

    public Fila getPessoasAguardando() {
        return pessoasAguardando;
    }

    public PainelElevador getPainel() {
        return painel;
    }
}
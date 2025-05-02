import java.io.Serializable;

public class Andar implements Serializable {
    private int numero;
    private Fila pessoasAguardando;
    private PainelElevador painel;

    public Andar(int numero, TipoPainel tipoPainel) {
        this.numero = numero;
        this.pessoasAguardando = new FilaPrioridade();
        this.painel = new PainelElevador(tipoPainel);
    }

    public void adicionarPessoa(Pessoa pessoa) {
        pessoasAguardando.enfileirar(pessoa);
        if (painel.getTipoPainel() == TipoPainel.UNICO_BOTAO) {
            painel.pressionarChamadaGeral();
        } else if (painel.getTipoPainel() == TipoPainel.DOIS_BOTOES) {
            if (pessoa.getAndarDestino() > numero) {
                painel.pressionarSubir();
            } else if (pessoa.getAndarDestino() < numero) {
                painel.pressionarDescer();
            }
        } else if (painel.getTipoPainel() == TipoPainel.PAINEL_NUMERICO) {
            painel.pressionarAndar(pessoa.getAndarDestino());
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
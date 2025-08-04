package deteccao_imagens_ia.utils;

public class FormatadorDecimal {

    public static final int SINAL_E_SEPARADOR_DECIMAL_TAMANHO = 2;
    private final long expoente;
    private final double toleranciaArredodamento;
    private final StringBuilder stringBuilderDecimal;

    public FormatadorDecimal(int precisao) {
        expoente = (long) Math.pow(10, precisao);
        toleranciaArredodamento = Math.pow(10, -precisao);
        stringBuilderDecimal = new StringBuilder(precisao * 2 + SINAL_E_SEPARADOR_DECIMAL_TAMANHO);
    }

    public String formatar(double valor) {
        if (deveFormatarComoInteiro(valor)) return formatarComoInteiro(valor);
        stringBuilderDecimal.setLength(0);
        valor = tratarValorNegativo(valor);
        montarPartesDoNumero(valor);
        removerZerosADireita();
        return stringBuilderDecimal.toString();
    }

    private boolean deveFormatarComoInteiro(double valor) {
        return Math.abs(valor - (long) valor) < toleranciaArredodamento;
    }

    private String formatarComoInteiro(double valor) {
        return (valor < 0 ? "-" : "") + (long) valor;
    }

    private double tratarValorNegativo(double valor) {
        if (valor < 0) {
            stringBuilderDecimal.append('-');
            return -valor;
        }
        return valor;
    }

    private void montarPartesDoNumero(double valorAbsoluto) {
        long valorFormatado = arredondar(valorAbsoluto * expoente);
        long parteInteira = valorFormatado / expoente;
        long parteDecimal = valorFormatado % expoente;
        montarParteDecimal(parteInteira, parteDecimal);
    }

    private long arredondar(double valor) {
        return (long) (valor + 0.5);
    }

    private void montarParteDecimal(long parteInteira, long parteDecimal) {
        stringBuilderDecimal.append(parteInteira).append('.');
        for (long div = expoente / 10; parteDecimal < div; div /= 10)
            stringBuilderDecimal.append('0');
        stringBuilderDecimal.append(parteDecimal);
    }

    private StringBuilder removerZerosADireita() {
        int end = stringBuilderDecimal.length();
        while (stringBuilderDecimal.charAt(end - 1) == '0') end--;
        stringBuilderDecimal.setLength(end);
        return stringBuilderDecimal;
    }
}
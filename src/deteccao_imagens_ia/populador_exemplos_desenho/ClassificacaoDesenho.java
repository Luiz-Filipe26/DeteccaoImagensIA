package deteccao_imagens_ia.populador_exemplos_desenho;

public enum ClassificacaoDesenho {
    BONECO_DE_PALITO,
    OUTROS_DESENHOS;

    public static ClassificacaoDesenho deTexto(String texto) {
        return texto.equalsIgnoreCase("boneco_de_palito") ? BONECO_DE_PALITO : OUTROS_DESENHOS;
    }

    public String paraTexto() {
        return name().toLowerCase();
    }
}

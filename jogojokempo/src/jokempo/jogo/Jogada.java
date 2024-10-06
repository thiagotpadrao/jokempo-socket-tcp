package jokempo.jogo;

public enum Jogada {
    PEDRA,
    PAPEL,
    TESOURA;

    // Método para determinar qual jogada vence qual
    public boolean vence(Jogada adversario) {
        switch (this) {
            case PEDRA:
                return adversario == TESOURA;
            case PAPEL:
                return adversario == PEDRA;
            case TESOURA:
                return adversario == PAPEL;
            default:
                return false;
        }
    }
}

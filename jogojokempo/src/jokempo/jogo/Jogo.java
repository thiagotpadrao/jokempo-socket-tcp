package jokempo.jogo;

//classe que cuida da lógica principal do jogo (decidir vencedores, validar jogadas, etc.).
public class Jogo {
	private int pontosplayer1;
	private int pontosplayer2;
	
	public Jogo () {
		this.pontosplayer1 = 0;
		this.pontosplayer2 = 0;
	}
	
	public Rodada jogar (Jogada jogadaplayer1, Jogada jogadaplayer2) {
        if (jogadaplayer1 == jogadaplayer2) {
            return Rodada.EMPATE;
        } else if (jogadaplayer1.vence(jogadaplayer2)) {
            pontosplayer1++;
            return Rodada.VITORIA;
        } else {
            pontosplayer2++;
            return Rodada.DERROTA;
        }
	}
	
	public int getPontosplayer1() {
		return pontosplayer1;
	}
	
	public int getPontosplayer2() {
		return pontosplayer2;
	}
}

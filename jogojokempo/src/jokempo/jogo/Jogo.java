package jokempo.jogo;

//classe que cuida da lógica principal do jogo 
public class Jogo {
	private int pontosplayer1;	//quantidade de pontos do player 1
	private int pontosplayer2;	//quantidade de pontos do player 2
	private int numeroRodada;	//numero da rodada atual
	
	public Jogo () {
		this.pontosplayer1 = 0;
		this.pontosplayer2 = 0;
		this.numeroRodada = 0;
	}
	
	//método que retorna o resultado da rodada, dadas as jogadas dos players
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

	//método que verifica se o alguém venceu o jogo
	public boolean isGameOver() {
        return pontosplayer1 >= 3 || pontosplayer2 >= 3;
    }
	
	public int getPontosplayer1() {
		return pontosplayer1;
	}
	
	public int getPontosplayer2() {
		return pontosplayer2;
	}

	public int getNumeroRodada() {
        return numeroRodada;
    }

    public void incrementarRodada() {
        numeroRodada++;
    }
}

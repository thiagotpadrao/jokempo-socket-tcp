package jokempo.rede;

import java.io.*;
import java.net.*;

import jokempo.jogo.Jogo;
import jokempo.jogo.Jogada;
import jokempo.jogo.Rodada;

//classe para gerenciar cada cliente conectado em uma thread separada, possibilitando múltiplos jogadores
public class GerenciaClientes implements Runnable{
	private Socket clienteSocket;
	private BufferedReader in;
	private PrintWriter out;
	private int playerid;
	private static Jogo jogo = new Jogo();
	private static Jogada jogadaplayer1;
	private static Jogada jogadaplayer2;
	private static PrintWriter msgplayer1;
	private static PrintWriter msgplayer2;
	
	public GerenciaClientes (Socket socket) {
		this.clienteSocket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            out = new PrintWriter(clienteSocket.getOutputStream(), true);
            
            synchronized (jogo) {
                if (msgplayer1 == null) {
                	msgplayer1 = out;
                	playerid = 1;
                	msgplayer1.println("\nAguardando um oponente se conectar para começar...");
                } else if (msgplayer2 == null) {
                	msgplayer2 = out;
                	playerid = 2;
                	msgplayer2.println("\nSeu adversário já estava te esperando. O jogo vai começar!");
                	msgplayer1.println("Oponente conectado. O jogo vai começar!");
                	
                	solicitarJogada();
                }
            }
		}catch (IOException e) {
            System.err.println("Erro ao configurar streams: " + e.getMessage());
        }
	}
	
	public void run() {
		try {
			String input;
            while ((input = in.readLine()) != null) {
            	try {
                    Jogada jogadaplayer = Jogada.valueOf(input.toUpperCase());

                    synchronized (jogo) {
                        if (playerid == 1 && jogadaplayer1 == null) {
                        	jogadaplayer1 = jogadaplayer;
                            if (jogadaplayer2 == null) msgplayer1.println("Aguardando jogada do oponente...");
                            if (msgplayer2 != null) {
                                if (jogadaplayer2 == null) msgplayer2.println("Seu adversário jogou.");
                            }
                        } else if (playerid == 2 && jogadaplayer2 == null) {
                        	jogadaplayer2 = jogadaplayer;
                            if(jogadaplayer1 == null) msgplayer2.println("Aguardando jogada do oponente...");
                            if (msgplayer1 != null) {
                            	if (jogadaplayer1 == null) msgplayer1.println("Seu adversário jogou.");
                            }
                        }
                        
                        if (jogadaplayer1 != null && jogadaplayer2 != null) {
                        	
                        	Rodada resultadoplayer1 = jogo.jogar(jogadaplayer1, jogadaplayer2);
                        	Rodada resultadoplayer2 = (resultadoplayer1 == Rodada.VITORIA) ? Rodada.DERROTA : (resultadoplayer1 == Rodada.DERROTA ? Rodada.VITORIA : Rodada.EMPATE);
                        	
                            enviaResultado(msgplayer1, resultadoplayer1);
                            enviaResultado(msgplayer2, resultadoplayer2);

                            if (jogo.isGameOver()) {
                                encerrarJogo();
                                break;
                            }
                            
                            jogadaplayer1 = null;
                            jogadaplayer2 = null;
                            
                            solicitarJogada();
                        }
                    }
                } catch (IllegalArgumentException e) {
                    out.println("Jogada inválida. Use: PEDRA, PAPEL ou TESOURA.");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro na comunicação com o cliente: " + e.getMessage());
        } finally {
            try {
                clienteSocket.close();
                ServidorJokempo.removeClient(clienteSocket);
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
		}     
	}
	
	private void enviaResultado(PrintWriter msgplayer, Rodada resultado) {
    	if (resultado == Rodada.VITORIA) {
            msgplayer.println("Você venceu a rodada!");
        } else if (resultado == Rodada.DERROTA) {
            msgplayer.println("Você perdeu a rodada!");
        } else {
            msgplayer.println("A rodada foi empate!");
        }
	}
	
    private void solicitarJogada() {
        
        jogo.incrementarRodada();

        int rodadaAtual = jogo.getNumeroRodada();
        
        int placarPlayer1 = jogo.getPontosplayer1();
        int placarPlayer2 = jogo.getPontosplayer2();

        if(placarPlayer1 == 3){

        }

        if (msgplayer1 != null) {
            msgplayer1.println("\n-------------------- Rodada " + rodadaAtual + " --------------------\n");
            msgplayer1.println("Placar: Você " + placarPlayer1 + " x " + placarPlayer2 + " Oponente\n");
            msgplayer1.println("Insira sua jogada (PEDRA, PAPEL ou TESOURA): ");
        }
        if (msgplayer2 != null) {
            msgplayer2.println("\n-------------------- Rodada " + rodadaAtual + " --------------------\n");
            msgplayer2.println("Placar: Você " + placarPlayer2 + " x " + placarPlayer1 + " Oponente\n");
            msgplayer2.println("Insira sua jogada (PEDRA, PAPEL ou TESOURA): ");
        }
    }

    private void encerrarJogo() {
        int placarPlayer1 = jogo.getPontosplayer1();
        int placarPlayer2 = jogo.getPontosplayer2();

        if (placarPlayer1 >= 3) {
            msgplayer1.println("\nVocê venceu o jogo!\n");
            msgplayer1.println("Placar Final: Você " + placarPlayer1 + " x " + placarPlayer2 + " Oponente\n");
            msgplayer2.println("Você perdeu o jogo.");
            msgplayer2.println("Placar Final: Você " + placarPlayer2 + " x " + placarPlayer1 + " Oponente\n");
        } else if (placarPlayer2 >= 3) {
            msgplayer2.println("Você venceu o jogo!");
            msgplayer2.println("Placar Final: Você " + placarPlayer2 + " x " + placarPlayer1 + " Oponente\n");
            msgplayer1.println("Você perdeu o jogo.");
            msgplayer1.println("Placar Final: Você " + placarPlayer1 + " x " + placarPlayer2 + " Oponente\n");
        }

        // Fechar conexões
        try {
            clienteSocket.close();
            if (msgplayer1 != null) msgplayer1.close();
            if (msgplayer2 != null) msgplayer2.close();
        } catch (IOException e) {
            System.err.println("Erro ao encerrar o jogo: " + e.getMessage());
        }

        System.out.println("Jogo encerrado.");
    }
	
	public void sendMessage(String message) {
		out.println(message);
	}
}

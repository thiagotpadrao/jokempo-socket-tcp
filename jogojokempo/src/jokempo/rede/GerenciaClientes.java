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
                    msgplayer1.println("\nConexão aceita. Aguardando um oponente se conectar para começar...");
                } else if (msgplayer2 == null) {
                    msgplayer2 = out;
                    playerid = 2;
                    msgplayer2.println("\nConexão aceita. Seu adversário já estava te esperando. O jogo vai começar!");
                    msgplayer1.println("\nOponente conectado. O jogo vai começar!");
                    
                    System.out.println("\n------------ Ambos os clientes conectaram. O jogo foi iniciado. ------------");
                    solicitarJogada();
                }
            }
        } catch (IOException e) {
            System.err.println("\nErro ao configurar streams: " + e.getMessage());
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
                            if (jogadaplayer2 == null) msgplayer1.println("\nAguardando jogada do oponente...");
                            if (msgplayer2 != null) {
                                if (jogadaplayer2 == null) msgplayer2.println("\nSeu adversário jogou.");
                            }
                        } else if (playerid == 2 && jogadaplayer2 == null) {
                            jogadaplayer2 = jogadaplayer;
                            if(jogadaplayer1 == null) msgplayer2.println("\nAguardando jogada do oponente...");
                            if (msgplayer1 != null) {
                                if (jogadaplayer1 == null) msgplayer1.println("\nSeu adversário jogou.");
                            }
                        }
    
                        if (jogadaplayer1 != null && jogadaplayer2 != null) {
                        	
                        	Rodada resultadoplayer1 = jogo.jogar(jogadaplayer1, jogadaplayer2);
                            Rodada resultadoplayer2 = (resultadoplayer1 == Rodada.VITORIA) ? Rodada.DERROTA : (resultadoplayer1 == Rodada.DERROTA ? Rodada.VITORIA : Rodada.EMPATE);
                            
                            if (resultadoplayer1 == Rodada.VITORIA) {
                            	System.out.println("\nO cliente 1 jogou " + jogadaplayer1 + " e o cliente 2 jogou " + jogadaplayer2 + ". O cliente 1 ganhou a rodada. (Placar: Cliente 1 - " + jogo.getPontosplayer1() + " X " + jogo.getPontosplayer2() + " - Cliente 2)");
                            } else if (resultadoplayer2 == Rodada.VITORIA) {
                            	System.out.println("\nO cliente 1 jogou " + jogadaplayer1 + " e o cliente 2 jogou " + jogadaplayer2 + ". O cliente 2 ganhou a rodada. (Placar: Cliente 1 - " + jogo.getPontosplayer1() + " X " + jogo.getPontosplayer2() + " - Cliente 2)");
                            } else {
                            	System.out.println("\nO cliente 1 jogou " + jogadaplayer1 + " e o cliente 2 jogou " + jogadaplayer2 + ". A rodada empatou. (Placar: Cliente 1 - " + jogo.getPontosplayer1() + " X " + jogo.getPontosplayer2() + " - Cliente 2)");
                            }
    
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
                    out.println("\nJogada inválida. Use: PEDRA, PAPEL ou TESOURA.");
                }
            }
        } catch (IOException e) {
            if (!clienteSocket.isClosed()) {
                System.err.println("\nErro na comunicação com o cliente: " + e.getMessage());
            }
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                System.err.println("\nErro ao fechar socket: " + e.getMessage());
            }
            ServidorJokempo.removeClient(clienteSocket);
        }
    }
	
	private void enviaResultado(PrintWriter msgplayer, Rodada resultado) {
    	if (resultado == Rodada.VITORIA) {
            msgplayer.println("\nVocê venceu a rodada!");
        } else if (resultado == Rodada.DERROTA) {
            msgplayer.println("\nVocê perdeu a rodada!");
        } else {
            msgplayer.println("\nA rodada foi empate!");
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
            msgplayer1.println("\n-------------------- Rodada " + rodadaAtual + " --------------------");
            msgplayer1.println("\nPlacar: Você " + placarPlayer1 + " x " + placarPlayer2 + " Oponente");
            msgplayer1.println("\nInsira sua jogada (PEDRA, PAPEL ou TESOURA): ");
        }
        if (msgplayer2 != null) {
            msgplayer2.println("\n-------------------- Rodada " + rodadaAtual + " --------------------");
            msgplayer2.println("\nPlacar: Você " + placarPlayer2 + " x " + placarPlayer1 + " Oponente");
            msgplayer2.println("\nInsira sua jogada (PEDRA, PAPEL ou TESOURA): ");
        }
    }

    private void encerrarJogo() {
        int placarPlayer1 = jogo.getPontosplayer1();
        int placarPlayer2 = jogo.getPontosplayer2();
    
        if (placarPlayer1 >= 3) {
            msgplayer1.println("\n--------------- Você venceu o jogo! ---------------");
            msgplayer1.println("\nPlacar Final: Você " + placarPlayer1 + " x " + placarPlayer2 + " Oponente");
            msgplayer2.println("\n--------------- Você perdeu o jogo. ---------------");
            msgplayer2.println("\nPlacar Final: Você " + placarPlayer2 + " x " + placarPlayer1 + " Oponente");
        } else if (placarPlayer2 >= 3) {
            msgplayer2.println("\n--------------- Você venceu o jogo! ---------------");
            msgplayer2.println("\nPlacar Final: Você " + placarPlayer2 + " x " + placarPlayer1 + " Oponente");
            msgplayer1.println("\n--------------- Você perdeu o jogo. ---------------");
            msgplayer1.println("\nPlacar Final: Você " + placarPlayer1 + " x " + placarPlayer2 + " Oponente");
        }
    
        msgplayer1.println("\nO jogo acabou, você será desconectado.");
        msgplayer2.println("\nO jogo acabou, você será desconectado.");
        
        System.out.println("\n---------------- Foi decretado um vencedor. Jogo encerrado. ----------------");

        ServidorJokempo.removeBothClients();
    }
    
	public void sendMessage(String message) {
		out.println(message);
	}
}

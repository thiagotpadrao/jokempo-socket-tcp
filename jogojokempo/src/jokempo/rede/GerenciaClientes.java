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
	
	private static Jogo jogo = new Jogo();
	private static Jogada jogadaplayer1;
	private static Jogada jogadaplayer2;
	
	public GerenciaClientes (Socket socket) {
		this.clienteSocket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            out = new PrintWriter(clienteSocket.getOutputStream(), true);
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
                        if (jogadaplayer1 == null) {
                        	jogadaplayer1 = jogadaplayer;
                            out.println("Aguardando jogada do oponente...");
                        } else if (jogadaplayer2 == null) {
                        	jogadaplayer2 = jogadaplayer;
                        	
                        	Rodada resultadoplayer1 = jogo.jogar(jogadaplayer1, jogadaplayer2);
                        	
                        	if (resultadoplayer1 == Rodada.VITORIA) {
                                out.println("Você venceu a rodada!");
                            } else if (resultadoplayer1 == Rodada.DERROTA) {
                                out.println("Você perdeu a rodada!");
                            } else {
                                out.println("A rodada foi empate!");
                            }
                        	
                        	jogadaplayer1 = null;
                        	jogadaplayer2 = null;
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
	
	public void sendMessage(String message) {
		out.println(message);
	}
}

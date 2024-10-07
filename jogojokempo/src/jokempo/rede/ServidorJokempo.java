package jokempo.rede;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

//classe que inicia o servidor e gerencia conexões
public class ServidorJokempo {
	private final int porta;
	private static final int MAXCLIENTES = 2;
	private static ConcurrentHashMap<Socket, GerenciaClientes> clients = new ConcurrentHashMap<>();
	
	public ServidorJokempo(int porta) {
		this.porta = porta;
	}
	
	public void start() {
		try (ServerSocket servidorSocket = new ServerSocket(porta)){
			System.out.println("Servidor Jokempo iniciado na porta " + porta);
			
			while(true) {
				if (clients.size()<MAXCLIENTES) {
					Socket clienteSocket = servidorSocket.accept();
					System.out.println("Cliente conectado: " + clienteSocket.getRemoteSocketAddress());
              
					GerenciaClientes gerenciador = new GerenciaClientes(clienteSocket);
					clients.put(clienteSocket, gerenciador);
					new Thread(gerenciador).start();
				} else {
                    System.out.println("Servidor cheio. Número máximo de clientes conectados.");
                    try (Socket clienteSocket = servidorSocket.accept()) {
                        PrintWriter out = new PrintWriter(clienteSocket.getOutputStream(), true);
                        out.println("Sala cheia. Tente novamente mais tarde.");
                        clienteSocket.close();
                        removeClient(clienteSocket);
                    }
				}
			}
		} catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
	}
	
	public static void removeClient(Socket removerSocket) {
		clients.remove(removerSocket);
		System.out.println("Cliente removido: " + removerSocket.getRemoteSocketAddress());
	}
}
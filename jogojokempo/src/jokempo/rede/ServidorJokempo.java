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
					System.out.println("\nCliente conectado: " + clienteSocket.getRemoteSocketAddress());
              
					GerenciaClientes gerenciador = new GerenciaClientes(clienteSocket);
					clients.put(clienteSocket, gerenciador);
					new Thread(gerenciador).start();
				} else {
                    try (Socket clienteSocket = servidorSocket.accept()) {
                        System.out.println("\nUm terceiro cliente tentou se juntar ao jogo, mas o servidor já está cheio. Número máximo de clientes conectados.");
                        PrintWriter out = new PrintWriter(clienteSocket.getOutputStream(), true);
                        out.println("\nSala cheia. Tente novamente mais tarde.");
                        clienteSocket.close();
                        removeClient(clienteSocket);
                    }
				}
			}
		} catch (IOException e) {
            System.err.println("\nErro no servidor: " + e.getMessage());
        }
	}
	
	public static void removeClient(Socket removerSocket) {
		GerenciaClientes cliente = clients.remove(removerSocket);
		if (cliente != null) {
			try {
				removerSocket.close();
			} catch (IOException e) {
				System.err.println("\nErro ao fechar socket: " + e.getMessage());
			}
			System.out.println("\nCliente removido: " + removerSocket.getRemoteSocketAddress());
		}
	}
	
	public static void removeBothClients() {
		for (Socket socket : clients.keySet()) {
			if (!socket.isClosed()) {
				removeClient(socket);
			}
		}
	}
}
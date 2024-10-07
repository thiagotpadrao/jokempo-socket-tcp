package jokempo.rede;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import jokempo.utils.Mensagens;

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
			System.out.println(Mensagens.ABRESERVIDOR + porta);
			
			while(true) {
				if (clients.size()<MAXCLIENTES) {
					Socket clienteSocket = servidorSocket.accept();
					System.out.println(Mensagens.CONECTACLIENTE + clienteSocket.getRemoteSocketAddress());  
					GerenciaClientes gerenciador = new GerenciaClientes(clienteSocket);
					clients.put(clienteSocket, gerenciador);
					new Thread(gerenciador).start();
				} else {
                    try (Socket clienteSocket = servidorSocket.accept()) {
                        System.out.println(Mensagens.CONECINVALIDA);
                        PrintWriter out = new PrintWriter(clienteSocket.getOutputStream(), true);
                        out.println(Mensagens.SALACHEIA);
                        clienteSocket.close();
                        removeClient(clienteSocket);
                    }
				}
			}
		} catch (IOException e) {
            System.err.println(Mensagens.X_SERVIDOR + e.getMessage());
        }
	}
	
	public static void removeClient(Socket removerSocket) {
		GerenciaClientes cliente = clients.remove(removerSocket);
		if (cliente != null) {
			try {
				removerSocket.close();
			} catch (IOException e) {
				System.err.println(Mensagens.X_SOCKET + e.getMessage());
			}
			System.out.println(Mensagens.REMOVECLIENTE + removerSocket.getRemoteSocketAddress());
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
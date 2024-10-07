package jokempo.rede;

import java.io.*;
import java.net.*;

//classe que cuida da comunicação com o servidor para o cliente.
public class ClienteJokempo {
	private final int portaservidor;
	private final String endereco;
	
	public ClienteJokempo (String endereco, int portaservidor) {
		this.endereco = endereco;
		this.portaservidor = portaservidor;
	}
	
	public void start() {
		try (Socket socket = new Socket(endereco, portaservidor); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))){
			
			System.out.println("Tentando conectar ao servidor Jokempo.");
			
			new Thread(() -> {
				try {
					String response;
					while((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.err.println("\nConexão com o servidor perdida.");
                }
            }).start();
			
            String input;
            while ((input = userInput.readLine()) != null) {
                out.println(input);
            }
        } catch (IOException e) {
            System.err.println("\nErro ao conectar ao servidor: " + e.getMessage());
        }
	}
}

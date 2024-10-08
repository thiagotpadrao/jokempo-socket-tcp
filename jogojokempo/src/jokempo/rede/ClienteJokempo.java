package jokempo.rede;

import java.io.*;
import java.net.*;
import jokempo.utils.Mensagens;

//classe que cuida da comunicação com o servidor para o cliente.
public class ClienteJokempo {
	private final int portaservidor;	//porta que o cliente vai se conectar
	private final String endereco;		//endereco que o cliente vai se conectar
	
	public ClienteJokempo (String endereco, int portaservidor) {
		this.endereco = endereco;
		this.portaservidor = portaservidor;
	}
	
	//método que inicia a conexão do cliente com o servidor e manda algumas mensagens
	public void start() {
		try (Socket socket = new Socket(endereco, portaservidor); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))){
			System.out.println(Mensagens.TENTACONECTAR);
			new Thread(() -> {
				try {
					String response;
					while((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.err.println(Mensagens.PERDECONEXAO);
                }
            }).start();
            String input;
            while ((input = userInput.readLine()) != null) {
                out.println(input);
            }
        } catch (IOException e) {
            System.err.println(Mensagens.X_CONEXAO + e.getMessage());
        }
	}
}

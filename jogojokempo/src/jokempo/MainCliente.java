package jokempo;

import jokempo.rede.ClienteJokempo;

//classe que inicia o cliente
public class MainCliente {
    public static void main(String[] args) {
    	int PORTA_SERVIDOR = 12345;	//porta do servidor que o cliente vai se conectar
        String enderecoServidor = "localhost";	//endereco do servidor

        ClienteJokempo cliente = new ClienteJokempo(enderecoServidor, PORTA_SERVIDOR);
        cliente.start();
    }
}

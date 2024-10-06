package jokempo;

import jokempo.rede.ClienteJokempo;

//classe que inicia o cliente
public class MainCliente {
    public static void main(String[] args) {
    	int PORTA_SERVIDOR = 12345;
        String enderecoServidor = "localhost";

        ClienteJokempo cliente = new ClienteJokempo(enderecoServidor, PORTA_SERVIDOR);
        cliente.start();
    }
}

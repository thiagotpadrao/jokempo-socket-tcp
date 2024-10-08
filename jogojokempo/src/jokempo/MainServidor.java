package jokempo;

import jokempo.rede.ServidorJokempo;

//classe para iniciar o servidor
public class MainServidor {
	public static void main (String args[]) {
		int PORTA = 12345;	//porta do servidor
	
		ServidorJokempo servidor = new ServidorJokempo(PORTA);
		servidor.start();
	}
}

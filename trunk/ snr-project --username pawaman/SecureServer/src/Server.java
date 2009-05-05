import java.net.ServerSocket;


public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int listen_port = 2009;

		final String[][] utenti = { 
									{"utente1", "password1"},
									{"utente2", "password2"},
									{"utente3", "password3"}
		};
		
		ServerSocket ssocket;
		boolean listening = true; 
		try {
		    ssocket = new ServerSocket(listen_port);
			while (listening){
				System.out.println("Attending new connections");
				new ServerThread(ssocket.accept(), utenti).start();
			}
	        ssocket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
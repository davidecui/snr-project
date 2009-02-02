import java.net.ServerSocket;


public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int listen_port = 2009;
		
		
		ServerSocket ssocket;
		boolean listening = true; 
		try {
		    ssocket = new ServerSocket(listen_port);
			while (listening) new ServerThread(ssocket.accept()).start();
	        ssocket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
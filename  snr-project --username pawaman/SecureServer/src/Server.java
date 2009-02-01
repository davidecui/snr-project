import java.net.ServerSocket;


public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket ssocket;
		boolean listening = true; 
		try {
		    ssocket = new ServerSocket(2009);
			while (listening)
	    	    new ServerThread(ssocket.accept()).start();
	            ssocket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}

import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
    private Socket socket = null;

    public ServerThread(Socket socket) {
    	this.socket = socket;
    }

    public void run() {
		try {
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
		    String outputLine = null;
		    
		    System.out.println("Waiting");
	
		    do {
		    	outputLine = in.readLine();	
		    	out.println(outputLine);
		    } while (!outputLine.equals("Bye")); 

		    out.close();
		    in.close();
		    socket.close();
	
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    System.out.println("Exiting");

    }
}


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
	    BufferedReader in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine, outputLine = "a";
	    out.println(outputLine);

	    while ((inputLine = in.readLine()) != null) {
		out.println(outputLine);
		if (outputLine.equals("Bye"))
		    break;
	    }
	    out.close();
	    in.close();
	    socket.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}

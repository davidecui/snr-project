

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {

    	final int port = 2009;
    	final String host = "localhost";

    	Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput = null;
        String response = null;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.err.println("Exception: "+ e);
            System.exit(1);
        }
        System.out.println("All ok");

        do {
        	System.out.print("word: ");
        	userInput = stdIn.readLine();
        	out.println(userInput);
        	response = in.readLine();
        	System.out.println("echo: " + response);
        } while (!response.equals("Bye"));

        out.close();
        in.close();
        stdIn.close();
        socket.close();
        System.out.println("Exiting");
    }
}

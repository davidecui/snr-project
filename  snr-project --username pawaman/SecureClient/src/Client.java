

import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    	SecureConnection connection = new SecureConnection();
    	connection.initialize();
        String userInput = null;
        String response;
        do {
        	System.out.print("word: ");
        	userInput = stdIn.readLine();
        	connection.secureSend(userInput);
        	response = (String) connection.secureReceive();
        	System.out.println("echo: " + response);
        } while (!response.equals("Bye"));

        stdIn.close();
        System.out.println("Exiting");
    }
}

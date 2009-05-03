

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.Random;

public class Client {
    public static void main(String[] args) throws IOException {

    	final int port = 2009;
    	final String host = "localhost";

    	Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput = null;
        String response;
        
        MagicBox mb = new MagicBox("../SecureClient/etc/publicKey.jks", null, true);
        Random rand;
        byte[] nonce = new byte[16];
        try {
            socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            rand = SecureRandom.getInstance ("SHA1PRNG");
            rand.nextBytes(nonce);
        } catch (Exception e) {
            System.err.println("Exception: "+ e);
            System.exit(1);
        }
        System.out.println("Connection Initializated");
        
// PASSO 1: C -> S | C, S, Es(C, S, P, Na, Ks)
        String message = 	"FROM: client\n" +
        					"TO: server\n" +
        					"PSSW: password\n" +
        					"NA: " + nonce;
        byte[] plainMessage = mb.concat(mb.getSessionKey(), message.getBytes());
        System.out.println("Message: " + plainMessage);
        byte[] encMessage = mb.RSAEncode(plainMessage);
        System.out.println("Encrypted Message: " + encMessage);
        out.write(encMessage);

// PASSO 2: S -> C | C, S, Ks(C, S, Na, Nb)
    	byte[] byteResp = new byte[128];
    	for (int i = 0; i < byteResp.length; i++) byteResp[i] = (byte) 0x00;
    	in.read(byteResp);
    	System.out.println("original response: " + byteResp);
    	byte[] respNoPad = mb.subPad(byteResp);
    	respNoPad = mb.SessionDecode(respNoPad);
		response = mb.getStringFromByte(respNoPad);
    	System.out.println("Decoded Message:\n-------\n" + response + "\n-------\n");

// PASSO 3: C -> S | C, S, Es(C, S, P, Na, Nb)
        message = 	"FROM: client\n" +
					"TO: server\n" +
					"PSSW: password\n" +
					"NA: 12345 \n" +
					"NB: 54321";			
        plainMessage = message.getBytes();
        System.out.println("Message: " + plainMessage);
        encMessage = mb.RSAEncode(plainMessage);
        System.out.println("Encrypted Message: " + encMessage);
        out.write(encMessage);


        do {
        	System.out.print("word: ");
        	userInput = stdIn.readLine();
        	byte[] word = mb.SessionEncode(userInput.getBytes());
        	out.write(word);
        	in.read(word);
        	response = mb.getStringFromByte(mb.SessionDecode(word));
        	System.out.println("echo: " + response);
        } while (!response.equals("Bye"));

        out.close();
        in.close();
        stdIn.close();
        socket.close();
        System.out.println("Exiting");
    }
}

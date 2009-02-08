
import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
    private Socket socket = null;
    MagicBox mb;

    public ServerThread(Socket socket) {
    	this.socket = socket;
    	mb = new MagicBox("../SecureServer/etc/publicKey.jks", "../SecureServer/etc/privateKey.jks", false);
    }

    public void run() {
		try {
			DataOutputStream out = new DataOutputStream (socket.getOutputStream());
			DataInputStream in = new DataInputStream (socket.getInputStream());
	
		    byte[] clientLine = new byte[128];
 // PASSO 1: C -> S | C, S, Es(C, S, P, Na, Ks)
	    	in.read(clientLine);
	    	System.out.println("Encoded Message: " + clientLine);
	    	byte[] clientByte = mb.RSADecode(clientLine);
	    	int messageLength = clientByte.length - 16;
	    	byte[] sessionKey = new byte[16];
	    	byte[] plainMessage = new byte[messageLength];
	    	for (int i = 0; i < 16; i++) sessionKey[i] = clientByte[i];
	    	for (int j = 16; j < clientByte.length; j++) plainMessage[j-16] = clientByte[j]; 
	    	String plainString = mb.getStringFromByte(plainMessage);
	    	System.out.println("Decoded Message:\n-------\n" + plainString + "\n-------\n");
	    	
// PASSO 2: S -> C | C, S, Ks(C, S, Na, Nb)
	    	mb.setSessionKey(sessionKey);
	        String message =  	"FROM: server\n" +
								"TO: client\n" +
								"NA: 12345 \n" +
								"NB: 54321";			
	        plainMessage = message.getBytes();
	        byte[] encMessage = mb.SessionEncode(plainMessage);
	        System.out.println("Encrypted Message: " + encMessage);
	        out.write(encMessage);

// PASSO 3: C -> S | C, S, Es(C, S, P, Na, Nb)
	    	in.read(clientLine);
	    	System.out.println("Encoded Message: " + clientLine);
	    	clientByte = mb.RSADecode(clientLine);
	    	plainString = mb.getStringFromByte(clientByte);
	    	System.out.println("Decoded Message:\n-------\n" + plainString + "\n-------\n");
	    	
		    
		    System.out.println("Waiting");
		    String r = null;
	    	int newlenght = 0;
	    	byte[] respNoPad = new byte[newlenght];
		    do {
		    	for (int i = 0; i < clientLine.length; i++) clientLine[i] = (byte) 0x00;
		    	in.read(clientLine);
		    	newlenght = 0;
		    	for (int i = clientLine.length - 1; i > 0; i--) 
		    		if (clientLine[i] != (byte) 0x00){
		    			newlenght = i+1;
		    			break;
		    		}
		    	respNoPad = new byte[newlenght];
		    	for (int i = 0; i < respNoPad.length; i++) respNoPad[i] = clientLine[i];
		    	clientLine = mb.SessionDecode(respNoPad);
		    	r = mb.getStringFromByte(clientLine);
		    	System.out.println("Client input: " + r);
		    	clientLine = mb.SessionEncode(clientLine);
		    	out.write(clientLine);
		    } while (!r.equals("Bye")); 

		    out.close();
		    in.close();
		    socket.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    System.out.println("Exiting");
    }
}


import java.net.*;
import java.util.Random;
import java.io.*;

public class ServerThread extends Thread {
    private Socket socket = null;
    String uname_1, uname_2;
    String upssw_1, upssw_2;
    String na_1, na_2, nb_test;
    Double nb;
    Random rand = new Random();
    String[][] utenti;
    MagicBox mb;

    public ServerThread(Socket socket, String[][] utenti) {
    	this.socket = socket;
    	this.utenti = utenti;
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
	    	String [] data = plainString.split("\n");
	    	uname_1 = data[0].subSequence(6, data[0].length()).toString();
	    	upssw_1 = data[2].subSequence(6, data[2].length()).toString();
	    	na_1 = data[3].subSequence(4, data[3].length()).toString();

	    	if (verify(uname_1, upssw_1, utenti)){
	    		
// PASSO 2: S -> C | C, S, Ks(C, S, Na, Nb)
	    		nb = rand.nextDouble();
		    	mb.setSessionKey(sessionKey);
		        String message =  	"FROM: server\n" +
									"TO: client\n" +
									"NA: "+na_1+"\n" +
									"NB: "+nb;			
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
		    	
		    	data = plainString.split("\n");
		    	uname_2 = data[0].subSequence(6, data[0].length()).toString();
		    	upssw_2 = data[2].subSequence(6, data[2].length()).toString();
		    	na_2 = data[3].subSequence(4, data[3].length()).toString();
		    	nb_test = data[4].subSequence(4, data[4].length()).toString();
		    	
		    	if (uname_1.equals(uname_2)
		    			&& upssw_1.equals(upssw_2)
		    				&& Double.parseDouble(nb_test) == nb
		    					&& na_1.equals(na_2)){
		    	
				    System.out.println("Waiting");
				    String r = null;
			    	int newlenght = 0;
			    	byte[] respNoPad = new byte[newlenght];
			    	String s = new String();
			    	do {
			    		clientLine = new byte[153600];
			        	try {
			    			in.read(clientLine);
			    		} catch (IOException e) {
			    			// TODO Auto-generated catch block
			    			e.printStackTrace();
			    		}
				    	respNoPad = mb.subPad(clientLine);
				    	clientLine = mb.SessionDecode(respNoPad);
				    	r = mb.getStringFromByte(clientLine);
				    	System.out.println("Client input: " + r);
				    	/*----------------echo
				    	clientLine = mb.SessionEncode(clientLine);
				    	out.write(clientLine);
				    	------------------echo*/
				    	FileInputStream fis = new FileInputStream("../SecureServer/etc/" + r + ".jpg");
				    	byte[] buffer = new byte[fis.available()];
				    	fis.read(buffer);
				    	byte[] aa = mb.SessionEncode(buffer);
				    	System.out.println(aa.length);
				    	out.write(aa);
				    	System.out.println(r+".jpg sent");
				    } while (!r.equals("Bye"));
		    	}
	    	}
	    	else {}
		    out.close();
		    in.close();
		    socket.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    System.out.println("Exiting");
    }
    
    boolean verify(String name, String pssw, String[][] utenti){
    	boolean result = false;
    	int i;
    	for (i = 0; i < utenti.length; i++){
    		if (utenti[i][0].equals(name)){
    	    	result = utenti[i][1].equals(pssw);
    			break;
    		}
    	}
    	return result;
   	}

}

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;


public class SecureConnection {
	private final int port = 2009;
	private final String host = "localhost";
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private String response;
   	private MagicBox mb;
	private Random rand;
	private byte[] nonce = new byte[16];

	public SecureConnection() {
        try {
        	mb = new MagicBox("../SecureClient/etc/publicKey.jks", null, true);
			socket = new Socket(host, port);
	        out = new DataOutputStream(socket.getOutputStream());
	        in = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void initialize(){
		// PASSO 1: C -> S | C, S, Es(C, S, P, Na, Ks)
        String message = 	"FROM: client\n" +
        					"TO: server\n" +
        					"PSSW: password\n" +
        					"NA: " + nonce;
        byte[] plainMessage = mb.concat(mb.getSessionKey(), message.getBytes());
        byte[] encMessage = mb.RSAEncode(plainMessage);
        try {
			out.write(encMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

// PASSO 2: S -> C | C, S, Ks(C, S, Na, Nb)
    	byte[] byteResp = new byte[128];
    	for (int i = 0; i < byteResp.length; i++) byteResp[i] = (byte) 0x00;
    	try {
			in.read(byteResp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	int newlenght = 0;
    	for (int i = byteResp.length - 1; i > 0; i--) 
    		if (byteResp[i] != (byte) 0x00){
    			newlenght = i+1;
    			break;
    		}
    	byte[] respNoPad = new byte[newlenght];
    	for (int i = 0; i < respNoPad.length; i++) respNoPad[i] = byteResp[i];
    	respNoPad = mb.SessionDecode(respNoPad);
		response = mb.getStringFromByte(respNoPad);

// PASSO 3: C -> S | C, S, Es(C, S, P, Na, Nb)
        message = 	"FROM: client\n" +
					"TO: server\n" +
					"PSSW: password\n" +
					"NA: 12345 \n" +
					"NB: 54321";			
        plainMessage = message.getBytes();
        encMessage = mb.RSAEncode(plainMessage);
        try {
			out.write(encMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void secureSend(){
		
	}
	
	public void secureReceive(){
		
	}

}

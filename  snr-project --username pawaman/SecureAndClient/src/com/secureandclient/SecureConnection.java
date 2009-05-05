package com.secureandclient;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	private Random rand = new Random();
	private double na;
	private String nb;

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
		na = rand.nextDouble();
        String message = 	"FROM: utente1\n" +
        					"TO: server\n" +
        					"PSSW: password1\n" +
        					"NA: " + na;
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
    	byte[] respNoPad = mb.subPad(byteResp);
    	respNoPad = mb.SessionDecode(respNoPad);
		response = mb.getStringFromByte(respNoPad);
		
    	String [] d = response.split("\n");
    	String na_test = d[2].subSequence(4, d[2].length()).toString();
    	nb = d[3].subSequence(4, d[3].length()).toString();

    	if (Double.parseDouble(na_test) == na){
// PASSO 3: C -> S | C, S, Es(C, S, P, Na, Nb)
	        message = 	"FROM: utente1\n" +
						"TO: server\n" +
						"PSSW: password1\n" +
						"NA: "+na+"\n" +
						"NB: "+nb;			
	        plainMessage = message.getBytes();
	        encMessage = mb.RSAEncode(plainMessage);
	        try {
				out.write(encMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	
	public void secureSend(String mess){
    	byte[] word = mb.SessionEncode(mess.getBytes());
    	try {
			out.write(word);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public Object secureReceive(){
		byte[] word = new byte[153600];
    	try {
			in.read(word);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return mb.getStringFromByte(mb.SessionDecode(mb.subPad(word)));
	}

}

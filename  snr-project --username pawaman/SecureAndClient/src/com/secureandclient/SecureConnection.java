package com.secureandclient;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Gli oggetti di questa classe forniscono gli strumenti per avviare una comunicazione sicura col server e di trasmettere/ricevere
 * dati cifrati
 * @author Davide Cui
 *
 */

public class SecureConnection {
	private final int port = 2009;
	private String host = "131.114.152.235";
	private String uname, pssw;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private String response;
   	private MagicBox mb;
	private Random rand = new Random();
	private double na;
	private String nb;
	private byte[] buffer = new byte[102400];

	/**
	 * 
	 */
	public SecureConnection(String h, String u, String p) {
		if (h != null) host = h;
		uname = u;
		pssw = p;
        try {
        	mb = new MagicBox("/data/data/com.secureandclient/lib/publicKey.jks", null, true); //creo una magicbox per le operazioni di cifratuta/decifratura
			socket = new Socket(host, port);  //apro il socket
	        out = new DataOutputStream(socket.getOutputStream()); //apro gli stream di input/output
	        in = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bytefill(buffer, (byte) 0x00);

	}
	
	/**
	 * avvia il protocollo di comunicazione sicura. al temine della chiamata del metodo sara' possibile inviare e ricevere dati cifrati attraverso i metodi secureSend() e secureReceive()
	 */
	public boolean initialize(){

// PASSO 1: C -> S | C, S, Es(C, S, P, Na, Ks)
		na = rand.nextDouble();
        String message = 	"FROM: "+uname+"\n" +
        					"TO: server\n" +
        					"PSSW: "+pssw+"\n" +
        					"NA: " + na;
        byte[] plainMessage = mb.concat(mb.getSessionKey(), message.getBytes()); //lego la chiave di sessione generata dalla magicBox al messaggio
        byte[] encMessage = mb.RSAEncode(plainMessage); //codifico il messaggio con la chiave pubbica del server
        try {
			out.write(encMessage); //invio il messaggio cifrato
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

// PASSO 2: S -> C | C, S, Ks(C, S, Na, Nb)
    	byte[] byteResp = new byte[128];
    	bytefill(byteResp, (byte) 0x00);
    	int readed = 0;
    	try {
			readed = in.read(byteResp); //ricevo il messaggio cifrato
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	byte[] respNoPad = new byte[readed];
    	System.arraycopy(byteResp, 0, respNoPad, 0, respNoPad.length); //rimuovo il padding del buffer in ricezione
    	respNoPad = mb.SessionDecode(respNoPad); //decifro
		response = mb.getStringFromByte(respNoPad); //ottengo la stringa risultante
		
    	String [] d = response.split("\n");
    	String na_test = d[2].subSequence(4, d[2].length()).toString();
    	nb = d[3].subSequence(4, d[3].length()).toString(); //ottengo il nonce del server per reinviarlo

    	if (Double.parseDouble(na_test) == na){//verifico che il nonce ricevuto sia uguale a quello inviato
// PASSO 3: C -> S | C, S, Es(C, S, P, Na, Nb)
	        message = 	"FROM: "+uname+"\n" +
						"TO: server\n" +
						"PSSW: "+pssw+"\n" +
						"NA: "+na+"\n" +
						"NB: "+nb;			
	        plainMessage = message.getBytes();
	        encMessage = mb.RSAEncode(plainMessage);
	        try {
				out.write(encMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
    	}
    	else return false;
    	return true;
	}
	
	/**
	 * effettua un invio sicuro utilizzando la chiave di sessione
	 * @param mess la stringa in chiaro
	 */
	public void secureSend(String mess){
    	byte[] word = mb.SessionEncode(mess.getBytes());
    	try {
			out.write(word);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * effettua una ricezione sicura utilizzando la chiave di sessione
	 * @return un oggetto contenete l'immagine ricevuta
	 */
	public Object secureReceive(){
		int bufferLength = 0;
		try {
			System.gc();
			bufferLength = in.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("SECURECONNECTION", "readed: " + bufferLength);
		byte temp[] = new byte[bufferLength];
	    System.arraycopy(buffer, 0, temp, 0, temp.length);
		bytefill(buffer, (byte) 0x00);
		byte[] decoded = mb.SessionDecode(temp);
    	return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
    }
	
	/**
	 * inserisce all'interno di un array di byte lo stesso valore per tutte le posizioni (usato per resettare i buffer)
	 * @param array array da riempire
	 * @param value valore da utilizzare
	 */
	public static void bytefill(byte[] array, byte value) {
	   int len = array.length;
	   if (len > 0)
	   array[0] = value;
	   for (int i = 1; i < len; i += i)
	       System.arraycopy( array, 0, array, i, ((len - i) < i) ? (len - i) : i);
   }
}

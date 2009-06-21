import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import javax.imageio.ImageIO;

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
	private byte[] buffer = new byte[102400];

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
		bytefill(buffer, (byte) 0x00);
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
    	int readed = 0;
    	try {
			readed = in.read(byteResp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	byte[] respNoPad = new byte[readed];
    	System.arraycopy(byteResp, 0, respNoPad, 0, respNoPad.length);
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
	
	public void SecureSend(Image img){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
    		ImageIO.write((RenderedImage) img, "JPG", out);
    		byte[] imageBytes = mb.SessionEncode(out.toByteArray()); 
			out.write(imageBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public Object secureReceive(){
		int bufferLength = 0;
    	try {
			bufferLength = in.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte temp[] = new byte[bufferLength];
	    System.arraycopy(buffer, 0, temp, 0, temp.length);
		bytefill(buffer, (byte) 0x00);
		byte[] decoded = mb.SessionDecode(temp);
    	return mb.getStringFromByte(decoded);
	}

	public static void bytefill(byte[] array, byte value) {
		   int len = array.length;
		   if (len > 0)
		   array[0] = value;
		   for (int i = 1; i < len; i += i)
		       System.arraycopy( array, 0, array, i, ((len - i) < i) ? (len - i) : i);
	   }

}

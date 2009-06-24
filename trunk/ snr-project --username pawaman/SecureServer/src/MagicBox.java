
	import java.security.*;
	import java.security.spec.*;
	import javax.crypto.*;
	import javax.crypto.spec.SecretKeySpec;
	import java.io.*;

/**
 * questa classe contiene tutti i metodi utilizzati per manipolare i dati da inviare e ricevere 
 * utilizzati nel protocollo e nelle comunicazioni
 * @author Davide Cui
 *
 */
	
public class MagicBox {
	private FileInputStream fis; //usato per leggere la chiave
	private ByteArrayOutputStream baos; 
	private byte[] publicKeyBytes; //array di byte della chiave pubblica
	private byte[] privateKeyBytes; //array di bye dela chiave privata
	private byte[] rawKey; //array di byte della chiave di sessione
	private X509EncodedKeySpec ksx509; //chiave pubblica in forma trasparente
	private PKCS8EncodedKeySpec kspkcs8; //chiave privata in forma trasparente
	private SecretKeySpec sessionKeySpec; //chiave di sessione in forma trasparente
	private PublicKey publicKey; //chiave pubblica in forma opaca
	private PrivateKey privateKey; //chiave privata in forma opaca
	private SecretKey sessionKey; //chiave di sessione in forma opaca
	private KeyFactory kf;
	private KeyGenerator kg;
	private Cipher c;
	byte[] error = {-1};

/**
 * Istanzia l'oggetto MagicBox
 * @param pathPub percorso della chiave pubblica del server (obbligatorio)
 * @param pathPri percorso della chiave privata del server (puo' essere null)
 * @param sessionKey_enabled se true genererà una chiave di sessione
 */
	public MagicBox(String pathPub, String pathPri, boolean sessionKey_enabled){
		int i;
		try {
			kf = KeyFactory.getInstance("RSA"); //per la generazione degli oggetti che mi rappresenteranno la chiave pubblica e privata
			fis = new FileInputStream(pathPub); //accedo alla chiave pubblica
			baos = new ByteArrayOutputStream();
			i = 0;
			//iserisco il contenuto del file all'interno di un buffer
			while((i = fis.read()) != -1) {
				baos.write(i);
			}
			fis.close();
			publicKeyBytes = baos.toByteArray(); //sposto il contenuto del buffer in un array
			baos.close();
			ksx509 = new X509EncodedKeySpec(publicKeyBytes); // creo la chiave trasparente
			publicKey = kf.generatePublic(ksx509); //creo la chiave pubblica opaca
			if (pathPri != null){ //se il percorso della chiave privata non è null
				fis = new FileInputStream(pathPri);
				baos = new ByteArrayOutputStream();
				i = 0;
				while((i = fis.read()) != -1) {
					baos.write(i);
				}
				fis.close();
				privateKeyBytes = baos.toByteArray();
				baos.close();
				kspkcs8 = new PKCS8EncodedKeySpec(privateKeyBytes);
				privateKey = kf.generatePrivate(kspkcs8); // ottengo l'oggetto che mi rappresenta la chiave privata
			}
			if (sessionKey_enabled){ //se viene richiesta la generazione immediata della chiave di sessione
				kg = KeyGenerator.getInstance("AES");
				kg.init(128); //la chiave sarà a 128 bit
				sessionKey = kg.generateKey(); //genero la chiave
				rawKey = sessionKey.getEncoded(); 
				sessionKeySpec = new SecretKeySpec(rawKey, "AES");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * genera una chiave AES di sessione partendo da un array di byte
	 * @param rawKey 
	 */
	public void setSessionKey(byte[] rawKey){
		sessionKeySpec = new SecretKeySpec(rawKey, "AES");
	}

	/**
	 * restituisce la chiave di sessione		
	 * @return
	 */
	public byte[] getSessionKey(){
		return sessionKeySpec.getEncoded();
	}

	/**
	 * cifra con la chiave pubblica del server	
	 * @param plainContent dati da cifrare
	 * @return i dati cifrati
	 */
	public byte[] RSAEncode(byte[] plainContent){
		byte[] encryptContent = null;
		
		try {
			c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptContent = c.doFinal(plainContent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (encryptContent != null) return encryptContent;
		return error;

	}
	
	/**
	 * cifra con la chiave pubblica del server	
	 * @param plainText testo in chiaro da cifrare
	 * @return i dati cifrati
	 */
	public byte[] RSAEncode(String plainText){
		char[] plainChar = plainText.toCharArray();
		byte[] plainByte = new byte[plainChar.length];
		
		for (int i = 0; i < plainChar.length; i++) plainByte[i] = (byte) plainChar[i];
		
		return RSAEncode(plainByte);
	}
	
	/**
	 * decritta con la chiave privata del server
	 * @param encryptContent dati cifrati
	 * @return dati decrittati
	 */
	public byte[] RSADecode(byte[] encryptContent){
		byte[] plainContent = null;
		try {
			c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c.init(Cipher.DECRYPT_MODE, privateKey);
			plainContent = c.doFinal(encryptContent);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (plainContent != null) return plainContent;
		return error;
	}

	/**
	 * cifra con la chiave di sessione
	 * @param plainContent dati in chiaro
	 * @return dati cifrati
	 */
	public byte[] SessionEncode(byte[] plainContent){
		byte[] encryptContent = null;
		try {
			c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, sessionKeySpec);
			encryptContent = c.doFinal(plainContent);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (encryptContent != null) return encryptContent;
		return error;
	}
	
	
	/**
	 * cifra con la chiave di sessione
	 * @param plainText testo in chiaro
	 * @return dati crittati
	 */
	public byte[] SessionEncode(String plainText){
		char[] plainChar = plainText.toCharArray();
		byte[] plainByte = new byte[plainChar.length];
		
		for (int i = 0; i < plainChar.length; i++) plainByte[i] = (byte) plainChar[i];
		
		return SessionEncode(plainByte);
		
	}
	
	/**
	 * decifra con la chiave di sessione
	 * @param encryptContent dati cifrati
	 * @return dati in chiaro
	 */
	public byte[] SessionDecode(byte[] encryptContent){
		byte[] plainContent = null;
		try {
			c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, sessionKeySpec);
			plainContent = c.doFinal(encryptContent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (plainContent != null) return plainContent;
		return error;
		
	}
	
	/**
	 * restituisce una stringa da un array di byte
	 * @param b array di byte
	 * @return stringa corrispondente
	 */
	public String getStringFromByte(byte[] b){
		char[] c = new char[b.length];
		for(int i = 0; i < b.length; i++) c[i] = (char) b[i]; 
		return String.copyValueOf(c);

	}
	
	/**
	 * concatena due array di byte
	 * @param A primo array di byte
	 * @param B secondo array di byte
	 * @return concatenazione dei due array
	 */
	public byte[] concat(byte[] A, byte[] B) {
		   byte[] C= new byte[A.length+B.length];
		   System.arraycopy(A, 0, C, 0, A.length);
		   System.arraycopy(B, 0, C, A.length, B.length);
		   return C;
		}

}

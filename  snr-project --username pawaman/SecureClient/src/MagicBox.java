	import java.security.*;
	import java.security.spec.*;

	import javax.crypto.*;

import java.io.*;


public class MagicBox {
	private FileInputStream fis;
	private ByteArrayOutputStream baos;
	private byte[] publicKeyBytes;
	private byte[] privateKeyBytes;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private X509EncodedKeySpec ksx509;
	private PKCS8EncodedKeySpec kspkcs8;
	private KeyFactory kf;
	private Cipher c;

	public MagicBox(String pathPub, String pathPri){
		try {
			kf = KeyFactory.getInstance("RSA");
			if (pathPub != null){
				fis = new FileInputStream(pathPub);
				baos = new ByteArrayOutputStream();
				int i = 0;
				while((i = fis.read()) != -1) {
					baos.write(i);
				}
				fis.close();
				publicKeyBytes = baos.toByteArray();
				baos.close();
				ksx509 = new X509EncodedKeySpec(publicKeyBytes);
				publicKey = kf.generatePublic(ksx509);
			}
			if (pathPri != null){
				fis = new FileInputStream(pathPri);
				baos = new ByteArrayOutputStream();
				int i = 0;
				while((i = fis.read()) != -1) {
					baos.write(i);
				}
				fis.close();
				privateKeyBytes = baos.toByteArray();
				baos.close();
				kspkcs8 = new PKCS8EncodedKeySpec(privateKeyBytes);
				privateKey = kf.generatePrivate(kspkcs8);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] codifica(String testo){
		char[] plainText;
		byte[] zero = {0};
		plainText = testo.toCharArray();
		
		byte[] byteText = new byte[plainText.length];

		for (int i = 0; i < plainText.length; i++) byteText[i] = (byte) plainText[i];
		
		try {
			c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c.init(Cipher.ENCRYPT_MODE, publicKey);

			byteText = c.doFinal(byteText);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (byteText != null) return byteText;
		return zero;

	}
	
	public String decodifica(byte[] encryptText){
		byte[] byteText = null;
		try {
			c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c.init(Cipher.DECRYPT_MODE, privateKey);
			byteText = c.doFinal(encryptText);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		char[] plainText = new char[byteText.length];

		for (int i = 0; i < byteText.length; i++) plainText[i] = (char) byteText[i];

		return String.valueOf(plainText);
	}
}

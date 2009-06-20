package com.secureandclient;
	import java.security.*;
	import java.security.spec.*;
	import javax.crypto.*;
	import javax.crypto.spec.SecretKeySpec;
	import java.io.*;


public class MagicBox {
	private FileInputStream fis;
	private ByteArrayOutputStream baos;
	private byte[] publicKeyBytes;
	private byte[] privateKeyBytes;
	private byte[] rawKey;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private Key sessionKey;
	private X509EncodedKeySpec ksx509;
	private PKCS8EncodedKeySpec kspkcs8;
	private SecretKeySpec sessionKeySpec;
	private KeyFactory kf;
	private KeyGenerator kg;
	private Cipher c;
	byte[] error = {-1};


	public MagicBox(String pathPub, String pathPri, boolean sessionKey_enabled){
		int i;
		try {
			kf = KeyFactory.getInstance("RSA");
			fis = new FileInputStream(pathPub);
			baos = new ByteArrayOutputStream();
			i = 0;
			while((i = fis.read()) != -1) {
				baos.write(i);
			}
			fis.close();
			publicKeyBytes = baos.toByteArray();
			baos.close();
			ksx509 = new X509EncodedKeySpec(publicKeyBytes);
			publicKey = kf.generatePublic(ksx509);
			if (pathPri != null){
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
				privateKey = kf.generatePrivate(kspkcs8);
			}
			if (sessionKey_enabled){
				kg = KeyGenerator.getInstance("AES");
				kg.init(128);
				sessionKey = kg.generateKey();
				rawKey = sessionKey.getEncoded();
				sessionKeySpec = new SecretKeySpec(rawKey, "AES");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setSessionKey(byte[] rawKey){
		sessionKeySpec = new SecretKeySpec(rawKey, "AES");
	}
	
	public byte[] getSessionKey(){
		return sessionKeySpec.getEncoded();
	}
	
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
	
	public byte[] RSAEncode(String plainText){
		char[] plainChar = plainText.toCharArray();
		byte[] plainByte = new byte[plainChar.length];
		
		for (int i = 0; i < plainChar.length; i++) plainByte[i] = (byte) plainChar[i];
		
		return RSAEncode(plainByte);
	}
	
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
	
	
	public byte[] SessionEncode(String plainText){
		char[] plainChar = plainText.toCharArray();
		byte[] plainByte = new byte[plainChar.length];
		
		for (int i = 0; i < plainChar.length; i++) plainByte[i] = (byte) plainChar[i];
		
		return SessionEncode(plainByte);
		
	}
	
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
	
	public String getStringFromByte(byte[] b){
		char[] c = new char[b.length];
		for(int i = 0; i < b.length; i++) c[i] = (char) b[i]; 
		return String.copyValueOf(c);

	}
	
	public byte[] concat(byte[] A, byte[] B) {
		   byte[] C= new byte[A.length+B.length];
		   System.arraycopy(A, 0, C, 0, A.length);
		   System.arraycopy(B, 0, C, A.length, B.length);
		   return C;
		}
/*
	public byte[] subPad(byte[] padded) {
    	int newlenght = padded.length;
    	while(padded[newlenght - 1] == (byte) 0x00) newlenght--;
    	byte[] noPadded = new byte[newlenght];
    	System.arraycopy(padded, 0, noPadded, 0, noPadded.length);
    	return noPadded;
	}
*/
}

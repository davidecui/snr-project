import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;


public class PairGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KeyPairGenerator keyGen;
		KeyPair key;
		
		String publicKeyFile = "../SecureServer/etc/publicKey.jks";
        String privateKeyFile = "../SecureServer/etc/privateKey.jks";;

        try {
        	
            FileOutputStream fosu = new FileOutputStream(publicKeyFile);
            FileOutputStream fosr = new FileOutputStream(privateKeyFile);

            keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			key = keyGen.generateKeyPair();

			byte[] publicBytes = key.getPublic().getEncoded();
	        byte[] privateBytes = key.getPrivate().getEncoded();

	        fosu.write(publicBytes);
	        fosr.write(privateBytes);

	        fosu.close();
	        fosr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

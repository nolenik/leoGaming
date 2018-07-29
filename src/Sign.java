import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;




import org.bouncycastle.util.encoders.Base64;

 
public class Sign {

   
	
	public static String sign(PrivateKey privateKey, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
	    Signature sign = Signature.getInstance("SHA1withRSA");
	    sign.initSign(privateKey);
	    sign.update(message.getBytes("UTF-8"));
	    return new String(Base64.encode(sign.sign()), "UTF-8");
	}


	public static boolean verify(PublicKey publicKey, String message, String signature) throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
	    Signature sign = Signature.getInstance("SHA1withRSA");
	    sign.initVerify(publicKey);
	    sign.update(message.getBytes("UTF-8"));
	    return sign.verify(Base64.decode(signature.getBytes("UTF-8")));
	}
	public static String getKey(String filename) throws IOException {
	    String strKeyPEM = "";
	    BufferedReader br = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = br.readLine()) != null) {
	        strKeyPEM += line + "\n";
	    }
	    br.close();
	    return strKeyPEM;
	}
	public static RSAPrivateKey getPrivateKey(String filename) throws IOException, GeneralSecurityException {
	    String privateKeyPEM = getKey(filename);
	    privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
	    privateKeyPEM = privateKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");
	    byte[] encoded = Base64.decode(privateKeyPEM);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
	    RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
	    return privKey;
	}


	public static PublicKey getPublicKey(String filename) throws IOException, GeneralSecurityException {
	    String publicKeyPEM = getKey(filename);
	    publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
	    publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
	    byte[] encoded = Base64.decode(publicKeyPEM);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    PublicKey pubKey = (PublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
	    return pubKey;
	}
	public static PrivateKey generatePrivateKey(String filename) throws InvalidKeySpecException, FileNotFoundException, IOException, NoSuchAlgorithmException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PemFile pemFile = new PemFile(filename);
		byte[] content = pemFile.getPemObject().getContent();
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
		return factory.generatePrivate(privKeySpec);
	}
	
	public static PublicKey generatePublicKey(String filename) throws InvalidKeySpecException, FileNotFoundException, IOException, NoSuchAlgorithmException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PemFile pemFile = new PemFile(filename);
		byte[] content = pemFile.getPemObject().getContent();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		return factory.generatePublic(pubKeySpec);
	}
}

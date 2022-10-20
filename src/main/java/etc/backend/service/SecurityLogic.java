package etc.backend.service;

import org.apache.commons.codec.binary.Base64;
import org.jboss.logging.Logger;

import javax.crypto.Cipher;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@ApplicationScoped
public class SecurityLogic {
  /*ENCRIPTA EL TEXTO Y LA CLAVE*/
  public String desencrypt(String textToDesencrypt) {
    String textDesencrypted = "";
    try {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      //RSA_PKCS1_OAEP_PADDING
      Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
      //llave privada encriptada en base64
      File privateKeyFile = new File("keyb64.priv");
      byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
      //se decofica primero el base 64
      byte[] data = Base64.decodeBase64(privateKeyBytes);
      KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
      //se genera la clave para desencriptar
      EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(data);
      PrivateKey privateKey = privateKeyFactory.generatePrivate(privateKeySpec);
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      //como se guarda en base64 la encriptacion, primero se desencripta y el resultado se pasa por el RSA
      byte[] plainText = cipher.doFinal(Base64.decodeBase64(textToDesencrypt));
      //texto desencriptado
      textDesencrypted = new String(plainText);
    } catch (Exception ex) {
      Logger.getLogger(SecurityLogic.class.getName()).log(Logger.Level.ERROR, null, ex);
    }
    return textDesencrypted;
  }

  /*DESENCRIPTA EL TEXTO Y LA CLAVE*/
  public String encrypt(String textToEncrypt) {
    String textEncrypted = "";
    try {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      //RSA_PKCS1_OAEP_PADDING
      Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
      //llave publica encriptad en base64
      File publicKeyFile = new File("keyb64.pub");
      byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
      //se decofica primero el base 64
      byte[] data = Base64.decodeBase64(publicKeyBytes);
      KeyFactory publicKeyFactory = KeyFactory.getInstance("RSA");
      //se genera la clave para encriptar
      EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(data);
      PublicKey publicKey = publicKeyFactory.generatePublic(publicKeySpec);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      //como se guarda en base64 la encriptacion, primero se  pasa por el RSA  el resultado se encripta en base64
      byte[] cipherText = cipher.doFinal(textToEncrypt.getBytes());
      //texto encriptado
      textEncrypted = Base64.encodeBase64String(cipherText);
      System.out.println("cipher: " + textEncrypted);
    } catch (Exception ex) {
      Logger.getLogger(SecurityLogic.class.getName()).log(Logger.Level.ERROR, null, ex);
    }
    return textEncrypted;
  }
}

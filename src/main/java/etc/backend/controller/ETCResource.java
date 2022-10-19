package etc.backend.controller;

import etc.backend.dto.login.Login;
import etc.backend.dto.response.LoginResponse;
import etc.backend.service.LoginLogic;

import javax.crypto.Cipher;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Path("/users")
public class ETCResource {
  @Inject
  LoginLogic loginLogic;

  @POST
  @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(Login login) {
    LoginResponse loginResponse = new LoginResponse();
    int code;
    try {
      loginResponse = loginLogic.validLogin(login);
      code = 200;
    } catch (Exception ex) {
      code = 401;
    }
    return Response.status(code).entity(loginResponse).build();
  }

  @POST
  @Path("/register")
  @Produces(MediaType.APPLICATION_JSON)
  public Response register(Login login) {
    LoginResponse loginResponse = new LoginResponse();
    int code;
    try {
      loginResponse = loginLogic.saveNewUser(login);
      code = 200;
    } catch (Exception ex) {
      code = 401;
    }
    return Response.status(code).entity(loginResponse).build();
  }

  @POST
  @Path("/test")
  @Produces(MediaType.APPLICATION_JSON)
  public Response test(Login login) {
    LoginResponse loginResponse = new LoginResponse();
    int code;
    try {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      byte[] input = "abc".getBytes();
      Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");//OAEPWithSHA1AndMGF1Padding//PKCS1Padding
     /* KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
      generator.initialize(2048);
      KeyPair pair = generator.generateKeyPair();
      Key pubKey = pair.getPublic();
      System.out.println("plain : " + pubKey.toString());
      Key privKey = pair.getPrivate();
      System.out.println("plain : " + privKey.toString());
      try (FileOutputStream outPrivate = new FileOutputStream("key.priv")) {
        outPrivate.write(privKey.getEncoded());
      }
      try (FileOutputStream outPublic = new FileOutputStream("key.pub")) {
        outPublic.write(pubKey.getEncoded());
      }*/
      /*File publicKeyFile = new File("public.key");
      byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
      SecretKeySpec secretkeySpec = new SecretKeySpec(publicKeyBytes, "RSA");
*/
      /*KeyFactory keyFactory = KeyFactory.getInstance("RSA","BC");
      EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
      keyFactory.generatePublic(publicKeySpec);*/
      File publicKeyFile = new File("key.pub");
      byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
      KeyFactory publicKeyFactory = KeyFactory.getInstance("RSA");
      EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
      PublicKey publicKey = publicKeyFactory.generatePublic(publicKeySpec);
      /////////
      ///////
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] cipherText = cipher.doFinal(input);
      System.out.println("cipher: " + new String(cipherText));
      File privateKeyFile = new File("key.priv");
      byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
      KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
      EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
      PrivateKey privateKey = privateKeyFactory.generatePrivate(privateKeySpec);
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] plainText = cipher.doFinal(cipherText);
      System.out.println("plain : " + new String(plainText));


     /* File publicKeyFile2 = new File("private.key");
      byte[] publicKeyBytes2 = Files.readAllBytes(publicKeyFile.toPath());
      SecretKeySpec secretkeySpec2 = new SecretKeySpec(publicKeyBytes, "RSA");
      cipher.init(Cipher.DECRYPT_MODE, secretkeySpec2);
      byte[] plainText = cipher.doFinal(cipherText);
      System.out.println("plain : " + new String(plainText));*/
      code = 200;
    } catch (Exception ex) {
      code = 401;
    }
    return Response.status(code).entity(loginResponse).build();
  }
}
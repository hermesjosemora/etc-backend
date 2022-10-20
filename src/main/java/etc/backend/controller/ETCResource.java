package etc.backend.controller;

import etc.backend.dto.login.Login;
import etc.backend.dto.response.LoginResponse;
import etc.backend.dto.text.Text;
import etc.backend.service.LoginLogic;
import etc.backend.service.SecurityLogic;
import org.apache.commons.codec.binary.Base64;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

@Path("/users")
public class ETCResource {
  @Inject
  LoginLogic loginLogic;
  @Inject
  SecurityLogic securityLogic;

  /*DESENCRIPTA UN TEXTO ENVIADO*/
  @POST
  @Path("/decrypt")
  @Produces(MediaType.APPLICATION_JSON)
  public Response decrypt(Text text) {
    LoginResponse loginResponse = new LoginResponse();
    int code;
    try {
      loginResponse.setMessage("SUCCESS");
      loginResponse.setResult(true);
      loginResponse.setText(this.securityLogic.desencrypt(text.getText()));
      code = 200;
    } catch (Exception ex) {
      code = 401;
    }
    return Response.status(code).entity(loginResponse).build();
  }

  /*ENCRIPTA UN TEXTO ENVIADO Y SE LO ASOCIA A UN USUARIO*/
  @POST
  @Path("/encryp_text")
  @Produces(MediaType.APPLICATION_JSON)
  public Response encrypt(Text text, @QueryParam("idUser") Integer idUser) {
    LoginResponse loginResponse = new LoginResponse();
    int code;
    try {
      loginResponse = this.loginLogic.insertText(text, idUser);
      code = 200;
    } catch (Exception ex) {
      code = 401;
    }
    return Response.status(code).entity(loginResponse).build();
  }

  /*ENCRIPTA UN TEXTO Y DEVUELVE EL RESULTADO EN LA RESPUESTA*/
  @POST
  @Path("/encrypt")
  @Produces(MediaType.APPLICATION_JSON)
  public Response encrypt(Text text) {
    LoginResponse loginResponse = new LoginResponse();
    int code;
    try {
      loginResponse.setMessage("SUCCESS");
      loginResponse.setResult(true);
      loginResponse.setText(this.securityLogic.encrypt(text.getText()));
      code = 200;
    } catch (Exception ex) {
      code = 401;
    }
    return Response.status(code).entity(loginResponse).build();
  }

  /*PARA USO SOLO LA PRIMERA VEZ
   * GENERA LA CLAVE PUBLICA Y PRIVADA A UTILIZAR Y LAS GUARDA ENCRIPTADAS EN BASE64*/
  @POST
  @Path("/generateKeys")
  @Produces(MediaType.APPLICATION_JSON)
  public Response generateKeys(Login login) {
    LoginResponse loginResponse = new LoginResponse();
    int code;
    try {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
      generator.initialize(2048);
      KeyPair pair = generator.generateKeyPair();
      Key pubKey = pair.getPublic();
      System.out.println("plain : " + pubKey.toString());
      Key privKey = pair.getPrivate();
      System.out.println("plain : " + privKey.toString());
      //ARCHIVO DE LLAVE PRIVADA SIN BASE64
      try (FileOutputStream outPrivate = new FileOutputStream("key.priv")) {
        outPrivate.write(privKey.getEncoded());
      }
      //ARCHIVO DE LLAVE PRIVADA CON BASE64
      try (FileOutputStream outPrivate = new FileOutputStream("keyb64.priv")) {
        outPrivate.write(Base64.encodeBase64String(privKey.getEncoded()).getBytes());
      }
      //ARCHIVO DE LLAVE PUBLICA SIN BASE64
      try (FileOutputStream outPublic = new FileOutputStream("key.pub")) {
        outPublic.write(pubKey.getEncoded());
      }
      //ARCHIVO DE LLAVE PUBLICA CON BASE64
      try (FileOutputStream outPublic = new FileOutputStream("keyb64.pub")) {
        outPublic.write(Base64.encodeBase64String(pubKey.getEncoded()).getBytes());
      }
      code = 200;
    } catch (Exception ex) {
      code = 401;
    }
    return Response.status(code).entity(loginResponse).build();
  }

  /*LOGUEO DE USUARIOS*/
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

  /*REGISTRO DE USUARIOS*/
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

  /*SE OBTIENE LA LISTA DE TEXTOS GUARDADOS DE LOS USUARIOS DESENCRIPTADOS*/
  @GET
  @Path("/text_list")
  @Produces(MediaType.APPLICATION_JSON)
  public Response textList(@QueryParam("idUser") Integer idUser) {
    LoginResponse loginResponse = new LoginResponse();
    int code;
    try {
      loginResponse = this.loginLogic.getTextForUsers(idUser);
      code = 200;
    } catch (Exception ex) {
      code = 401;
    }
    return Response.status(code).entity(loginResponse).build();
  }
}
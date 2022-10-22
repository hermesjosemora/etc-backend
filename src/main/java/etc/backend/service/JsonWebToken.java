/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etc.backend.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author edward
 */
@Singleton
public class JsonWebToken {
  @ConfigProperty(name = "jwt.token")
  private String key;
  private Date expirationDate;

  public JsonWebToken() {
  }

  public JsonObject decodeToken(String pToken) {
    JsonObject data = new JsonObject();
    Jws jwtClaims = null;
    try {
      jwtClaims = Jwts.parser().setSigningKey(this.key).parseClaimsJws(pToken);
      data = JsonParser.parseString(jwtClaims.getBody().toString()).getAsJsonObject().get("sub").getAsJsonObject();
    } catch (ExpiredJwtException e) {
      // Payload
    }
    return data;
  }

  public String generateToken(JsonObject json) {
    String compactJws = "";
    try {
      Date date1 = new Date();
      // convert date to calendar
      Calendar c = Calendar.getInstance();
      c.setTime(date1);
      // manipulate date
      c.add(Calendar.YEAR, 1);
      Date expirationTime1 = c.getTime();// new Date(t1 + 365 * 24 * 3600 * 1000);
      this.expirationDate = expirationTime1;
      compactJws = Jwts.builder()
          .setSubject(json.toString())
          .compressWith(CompressionCodecs.DEFLATE)
          .signWith(SignatureAlgorithm.HS256, key)
          .setExpiration(expirationTime1)
          .compact();
    } catch (Exception ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    }
    return compactJws;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  public boolean validToken(String pToken) {
    boolean valid;
    try {
      Jwts.parser().setSigningKey(this.key).parseClaimsJws(pToken);
      valid = true;
    } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException | NullPointerException e) {
      valid = false;
    }
    return valid;
  }
}

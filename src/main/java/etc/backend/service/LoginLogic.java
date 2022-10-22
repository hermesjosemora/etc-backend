package etc.backend.service;

import com.google.gson.JsonObject;
import etc.backend.domain.TextForUsers;
import etc.backend.domain.Users;
import etc.backend.dto.login.Login;
import etc.backend.dto.response.LoginResponse;
import etc.backend.dto.text.Text;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LoginLogic {
  @Inject
  EntityManager em;
  @Inject
  SecurityLogic securityLogic;
  @Inject
  JsonWebToken jwt;

  private Users castData(Login login) {
    return new Users(login.getFullname(), login.getMail(), login.getUsername(), this.securityLogic.encrypt(login.getPassword()));
  }

  public String create(Users user) {
    if (this.findByUsername(user.getUsername()).isPresent()) {
      return "EXISTUSERNAME";
    }
    em.persist(user);
    return "OK";
  }

  public Optional<Users> findByUsername(String username) {
    TypedQuery<Users> query = em.createNamedQuery("User.findByUsername", Users.class);
    query.setParameter("username", username);
    return query.getResultStream().findFirst();
  }

  public Optional<Users> findByUsernameAndPassword(String username, String password) {
    TypedQuery<Users> query = em.createNamedQuery("User.findByUsernameAndPassword", Users.class);
    query.setParameter("username", username);
    query.setParameter("pass", password);
    return query.getResultStream().findFirst();
  }

  public Users get(int userId) {
    return em.find(Users.class, userId);
  }

  @Transactional
  public LoginResponse getTextForUsers(Integer idUser) {
    LoginResponse loginResponse = new LoginResponse();
    try {
      //obtengo la lista de textos
      List<TextForUsers> list = this.listTextForUsers(idUser);
      List<Text> textList = new ArrayList<>();
      //recorro los datos para desencriptar los textos
      for (TextForUsers textForUsers : list) {
        Text text = new Text();
        text.setText(this.securityLogic.desencrypt(textForUsers.getText()));
        text.setId(textForUsers.getId());
        textList.add(text);
      }
      //devuelvo la lsita de los textos a mostrar
      loginResponse.setTextForUsersList(textList);
      loginResponse.setResult(true);
      loginResponse.setMessage("SUCCESS");
    } catch (Exception ex) {
      Logger.getLogger(LoginLogic.class.getName()).log(Logger.Level.ERROR, null, ex);
      loginResponse.setResult(false);
      loginResponse.setMessage("ERROR AL REGISTRAR USUARIO");
    }
    return loginResponse;
  }

  /*ENCRIPTA E INGRESA EL TEXTO EN LA BBDDD ASOCIADO AL USURIO*/
  @Transactional
  public LoginResponse insertText(Text text, Integer idUser) {
    LoginResponse loginResponse = new LoginResponse();
    try {
      //encripta el texto y lo guarda en la bbdd
      TextForUsers textForUsers = new TextForUsers(idUser, this.securityLogic.encrypt(text.getText()));
      em.persist(textForUsers);
      loginResponse.setResult(true);
      loginResponse.setMessage("SUCCESS");
    } catch (Exception ex) {
      Logger.getLogger(LoginLogic.class.getName()).log(Logger.Level.ERROR, null, ex);
      loginResponse.setResult(false);
      loginResponse.setMessage("ERROR");
    }
    return loginResponse;
  }

  public List<Users> list() {
    CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
    CriteriaQuery<Users> query = criteriaBuilder.createQuery(Users.class);
    query.from(Users.class);
    return em.createQuery(query).getResultList();
  }

  public List<TextForUsers> listTextForUsers(Integer fkIdUser) {
    TypedQuery<TextForUsers> query = em.createNamedQuery("TextForUsers.findByIdUser", TextForUsers.class);
    query.setParameter("fkIdUsers", fkIdUser);
    return query.getResultStream().toList();
  }

  /*Se realiza el registro del nuevo usuario
   * See valida que no existe el username previamente registrado
   * Si no existís se crea el usuario con los datos enviados*/
  @Transactional
  public LoginResponse saveNewUser(Login login) {
    LoginResponse loginResponse = new LoginResponse();
    try {
      //se llena la entidad
      Users users = this.castData(login);
      //se manda a crear
      String result = this.create(users);
      //si no exitía ese usuario devuelve OK sino ya existia
      if (result.equals("OK")) {
        loginResponse.setResult(true);
        loginResponse.setMessage("SUCCESS");
      } else {
        //usuario ya existía
        loginResponse.setResult(false);
        loginResponse.setMessage("El nombre de usuario esta reservado, intentar con otro");
      }
    } catch (Exception ex) {
      Logger.getLogger(LoginLogic.class.getName()).log(Logger.Level.ERROR, null, ex);
      loginResponse.setResult(false);
      loginResponse.setMessage("Error al registrar usuario");
    }
    return loginResponse;
  }

  /*Se valida el logueo, se busca por el username, sino exiete se indica que el usuario no existe
   * Si el usuario existe, se desencripta la clave de la bbdd y se compra con la digitada por el usuario
   * Si son iguales se devuelve success, sino se devuelve el error de clave incorrecta*/
  @Transactional
  public LoginResponse validLogin(Login login) {
    LoginResponse loginResponse = new LoginResponse();
    try {
      Optional<Users> list = this.findByUsername(login.getUsername());
      if (list.isPresent()) {
        //si existe valido la clave
        Users user = list.get();
        if (this.securityLogic.desencrypt(user.getPass()).equals(login.getPassword())) {
          //si son iguales respondo correctamente
          JsonObject data = new JsonObject();
          data.addProperty("id_user", user.getId());
          data.addProperty("username", user.getUsername());
          loginResponse.setToken(jwt.generateToken(data));
          loginResponse.setResult(true);
          loginResponse.setMessage("SUCCESS");
        } else {
          //clave incorrecta
          loginResponse.setResult(false);
          loginResponse.setMessage("Clave inciorrecta");
        }
      } else {
        //no existe este usuario
        loginResponse.setResult(false);
        loginResponse.setMessage("El usuario no existe");
      }
    } catch (Exception ex) {
      Logger.getLogger(LoginLogic.class.getName()).log(Logger.Level.ERROR, null, ex);
      loginResponse.setResult(false);
      loginResponse.setMessage("ERROR");
    }
    return loginResponse;
  }
}

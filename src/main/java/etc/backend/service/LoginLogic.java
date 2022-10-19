package etc.backend.service;

import etc.backend.domain.Users;
import etc.backend.dto.login.Login;
import etc.backend.dto.response.LoginResponse;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LoginLogic {
  @Inject
  EntityManager em;

  private Users castData(Login login) {
    return new Users(login.getFullName(), login.getMail(), login.getUserName(), login.getPassword());
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

  public List<Users> list() {
    CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
    CriteriaQuery<Users> query = criteriaBuilder.createQuery(Users.class);
    query.from(Users.class);
    return em.createQuery(query).getResultList();
  }

  @Transactional
  public LoginResponse saveNewUser(Login login) {
    LoginResponse loginResponse = new LoginResponse();
    try {
      Users users = this.castData(login);
      String result = this.create(users);
      if (result.equals("OK")) {
        loginResponse.setResult(true);
        loginResponse.setMessage("SUCCESS");
      } else {
        loginResponse.setResult(false);
        loginResponse.setMessage("EXISTE OTRO USUARIO REGISTRADO");
      }
    } catch (Exception ex) {
      Logger.getLogger(LoginLogic.class.getName()).log(Logger.Level.ERROR, null, ex);
      loginResponse.setResult(false);
      loginResponse.setMessage("ERROR AL REGISTRAR USUARIO");
    }
    return loginResponse;
  }

  @Transactional
  public LoginResponse validLogin(Login login) {
    LoginResponse loginResponse = new LoginResponse();
    try {
      Optional<Users> list = this.findByUsernameAndPassword(login.getUserName(), login.getPassword());
      if (list.isPresent()) {
        loginResponse.setResult(true);
        loginResponse.setMessage("SUCCESS");
      } else {
        loginResponse.setResult(false);
        loginResponse.setMessage("DATOS INCORRECTOS");
      }
    } catch (Exception ex) {
      Logger.getLogger(LoginLogic.class.getName()).log(Logger.Level.ERROR, null, ex);
      loginResponse.setResult(false);
      loginResponse.setMessage("ERROR");
    }
    return loginResponse;
  }
}

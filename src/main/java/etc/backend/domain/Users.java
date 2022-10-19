package etc.backend.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
@NamedQuery(name = "User.findByUsernameAndPassword", query = "SELECT usr FROM Users usr WHERE usr.username = :username" +
    " AND usr.pass = :pass ")
@NamedQuery(name = "User.findByUsername", query = "SELECT usr FROM Users usr WHERE usr.username = :username")
public class Users {
  @Id
  @GeneratedValue
  private Integer id;
  private String fullname;
  private String mail;
  private String username;
  private String pass;

  public Users(String fullName, String mail, String userName, String password) {
    this.fullname = fullName;
    this.mail = mail;
    this.username = userName;
    this.pass = password;
  }

  public Users() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Users users = (Users) o;
    return Objects.equals(id, users.id) && Objects.equals(fullname, users.fullname) && Objects.equals(mail, users.mail) && Objects.equals(username, users.username) && Objects.equals(pass, users.pass);
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fullname, mail, username, pass);
  }
}

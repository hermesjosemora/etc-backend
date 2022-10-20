package etc.backend.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "text_for_users")
@NamedQuery(name = "TextForUsers.findByIdUser", query = "SELECT tfu FROM TextForUsers tfu WHERE tfu.fk_id_users = :fkIdUsers")
public class TextForUsers {
  @Id
  @GeneratedValue
  private Integer id;
  private Integer fk_id_users;
  private String text;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getFk_id_users() {
    return fk_id_users;
  }

  public void setFk_id_users(Integer fk_id_users) {
    this.fk_id_users = fk_id_users;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public TextForUsers(Integer fk_id_users, String text) {
    this.fk_id_users = fk_id_users;
    this.text = text;
  }

  public TextForUsers() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TextForUsers that = (TextForUsers) o;
    return Objects.equals(id, that.id) && Objects.equals(fk_id_users, that.fk_id_users) && Objects.equals(text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fk_id_users, text);
  }
}

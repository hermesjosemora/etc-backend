package etc.backend.dto.response;

import etc.backend.domain.TextForUsers;
import etc.backend.dto.text.Text;

import java.util.List;

public class LoginResponse extends BaseResponse {
  private Integer idUser;
  private String text;
  private List<Text> textForUsersList;

  public List<Text> getTextForUsersList() {
    return textForUsersList;
  }

  public void setTextForUsersList(List<Text> textForUsersList) {
    this.textForUsersList = textForUsersList;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Integer getIdUser() {
    return idUser;
  }

  public void setIdUser(Integer idUser) {
    this.idUser = idUser;
  }
}

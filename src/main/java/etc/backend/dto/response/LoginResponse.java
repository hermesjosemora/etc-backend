package etc.backend.dto.response;

import etc.backend.dto.text.Text;

import java.util.List;

public class LoginResponse extends BaseResponse {
  private String token;
  private String text;
  private List<Text> textForUsersList;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<Text> getTextForUsersList() {
    return textForUsersList;
  }

  public void setTextForUsersList(List<Text> textForUsersList) {
    this.textForUsersList = textForUsersList;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}

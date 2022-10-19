package etc.backend.dto.response;

public class ErrorInfo {
  private String code;
  private String message;

  public ErrorInfo(String pCode, String pMessage) {
    code = pCode;
    message = pMessage;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}



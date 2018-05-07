package thu.declan.xi.server.exception;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author declan
 */
public class ApiException extends Exception {

    private int status;
    private String developerMessage;
    private String userMessage;

    private final String DEV_MSG_BUNDLE = "DeveloperMessages";
    private final String USER_MSG_BUNDLE = "UserMessages";

    private String parseMessage(String msg, String bundle) {
        if (null == msg) {
            return null;
        }
        if (msg.startsWith("{") && msg.endsWith("}")) {
            try {
                String msgTemplate = msg.substring(1, msg.length() - 1).trim();
                ResourceBundle resb = ResourceBundle.getBundle(bundle);
                msg = resb.getString(msgTemplate);
            } catch (MissingResourceException e) {
                return msg;
            }
        }
        return msg;
    }

    public ApiException(int status, String developerMessage, String userMessage) {
        this.status = status;
        this.developerMessage = parseMessage(developerMessage, DEV_MSG_BUNDLE);
        this.userMessage = parseMessage(userMessage, USER_MSG_BUNDLE);
    }
    
    public String ResponseException(String userMessage){
    	userMessage = parseMessage(userMessage, USER_MSG_BUNDLE);
    	return userMessage;
    }

    public ApiException() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = parseMessage(developerMessage, DEV_MSG_BUNDLE);
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = parseMessage(userMessage, USER_MSG_BUNDLE);
    }

}


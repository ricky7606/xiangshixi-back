package thu.declan.xi.server.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 *
 * @author declan
 */
public class ErrorMessage {

    private int status;

    private String developerMessage;

    private String userMessage;

    public ErrorMessage(ApiException ex) {
        this.status = ex.getStatus();
        this.developerMessage = ex.getDeveloperMessage();
        this.userMessage = ex.getUserMessage();
    }

    public ErrorMessage(ConstraintViolationException e) {
        this.status = 400;
        StringBuilder msgBuilder = new StringBuilder();
        
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            msgBuilder.append(violation.getMessage()).append(". ");
        }
        this.developerMessage = msgBuilder.toString();
        this.userMessage = "Unkown Error.";
    }

    public ErrorMessage() {
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
        this.developerMessage = developerMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

}

package thu.declan.xi.server.service;

import thu.declan.xi.server.util.email.EmailInfo;


/**
 *
 * @author declan
 */
public interface EmailService {
    
    public boolean sendEmail(EmailInfo emailInfo);
    
    public boolean sendEmail(String title, String body, String mailTo);
    
    public void sendEmailInBackground(EmailInfo emailInfo);
    
    public void sendEmailInBackground(String title, String body, String mailTo);
    
}

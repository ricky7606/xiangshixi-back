package thu.declan.xi.server.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.service.EmailService;
import thu.declan.xi.server.util.email.EmailInfo;
import thu.declan.xi.server.util.email.MailSender;
import thu.declan.xi.server.util.email.SenderInfo;

/**
 *
 * @author declan
 */
@Service("emailService")
public class EmailServiceImpl implements InitializingBean, EmailService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private MailSender sender;
    
    private EmailInfo buildEmailInfo(String title, String body, String mailTo) {
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setBody(body);
        emailInfo.setHtml(true);
        emailInfo.setMailTo(mailTo);
        emailInfo.setSubject(title);
        return emailInfo;
    }
    
    private class SendEmailRunnable implements Runnable {
        
        private final EmailInfo emailInfo;
        
        public SendEmailRunnable(EmailInfo emailInfo) {
            this.emailInfo = emailInfo;
        }

        @Override
        public void run() {
            sendEmail(emailInfo);
        }
    
    }

    public MailSender getSender() {
        return sender;
    }

    public void setSender(MailSender sender) {
        this.sender = sender;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SenderInfo info = new SenderInfo();
        info.setSmtpHost(Constant.EMAIL_SMTPHOST);
		info.setSmtpPort(Constant.EMAIL_SMTPPORT);
        info.setUsername(Constant.EMAIL_USERNAME);
        info.setPassword(Constant.EMAIL_PASSWORD);
		info.setUseSSL(Constant.EMAIL_USESSL);
        info.setSenderName("享实习");
        sender = new MailSender(info);
    }
    
    @Override
    public boolean sendEmail(EmailInfo emailInfo) {
        try {
            sender.send(emailInfo);
        } catch (Exception ex) {
            LOGGER.error("Send mail failed: " + ex.toString());
            return false;
        }
        return true;
    }
    
    @Override
    public boolean sendEmail(String title, String body, String mailTo) {
        EmailInfo emailInfo = buildEmailInfo(title, body, mailTo);
        return sendEmail(emailInfo);
    }
    
    @Override
	@Async
    public void sendEmailInBackground(EmailInfo emailInfo) {
//        SendEmailRunnable sendEmailRunnable = new SendEmailRunnable(emailInfo);
//        new Thread(sendEmailRunnable).start();
		sendEmail(emailInfo);
    }
    
    @Override
	@Async
    public void sendEmailInBackground(String title, String body, String mailTo) {
		sendEmail(title, body, mailTo);
//        EmailInfo emailInfo = buildEmailInfo(title, body, mailTo);
//        sendEmailInBackground(emailInfo);
    }
    
}

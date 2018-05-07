package thu.declan.xi.server.service;

/**
 *
 * @author declan
 */
public interface SMSService {
    
    public boolean sendMsg(String phone, String content);
    
	public void sendMsgInBackground(String phone, String content);
	
    public boolean sendCode(String phone, String code);
    
}

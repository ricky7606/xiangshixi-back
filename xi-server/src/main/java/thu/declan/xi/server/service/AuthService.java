package thu.declan.xi.server.service;

import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Account;

/**
 *
 * @author declan
 */
public interface AuthService {
    
    public Account getAccount();
	
	public Integer getEntityId();
	
	public Account wechatLogin(String openid, Account.Role authType) throws ServiceException;

    public Account login(String phone, String password, Account.Role authType) throws ServiceException;
	
	public Account logout();
    
}

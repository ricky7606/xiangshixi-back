package thu.declan.xi.server.service.impl;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.AccountMapper;
import thu.declan.xi.server.mapper.CompanyMapper;
import thu.declan.xi.server.mapper.NotificationMapper;
import thu.declan.xi.server.mapper.StudentMapper;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Company;
import thu.declan.xi.server.model.Student;
import thu.declan.xi.server.service.AuthService;
import thu.declan.xi.server.util.EncryptionUtils;


/**
 *
 * @author declan
 */
@Service("authService")
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	AccountMapper accountMapper;
	
	@Autowired
	StudentMapper studentMapper;
	
	@Autowired
	CompanyMapper companyMapper;
    
    @Autowired
	NotificationMapper notiMapper;
    
    private HttpSession session() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true); 
        return session;
    }

	@Override
	public Account getAccount() {
		return (Account) session().getAttribute(Constant.SESSION_ACCOUNT);
	}
	
	
	@Override
	public Integer getEntityId() {
		return (Integer) session().getAttribute(Constant.SESSION_ENTITY_ID);
	}
	
	private void setSession(Account account, Account.Role authType) {
		HttpSession sess = session();
		sess.setAttribute(Constant.SESSION_ACCOUNT, account);
		switch (authType) {
			case STUDENT:
				Student stu = studentMapper.selectByAccountId(account.getId());
				sess.setAttribute(Constant.SESSION_ENTITY_ID, stu.getId());
				break;
			case COMPANY:
				Company comp = companyMapper.selectByAccountId(account.getId());
				sess.setAttribute(Constant.SESSION_ENTITY_ID, comp.getId());
				break;
			default:
				sess.removeAttribute(Constant.SESSION_ENTITY_ID);
		}
	}


	@Override
	public Account login(String phone, String password, Account.Role authType) throws ServiceException {
		Account matcher = new Account();
		matcher.setPhone(phone);
		matcher.setRole(authType);
		Account account = accountMapper.selectByIdentity(matcher);
		if (account == null) {
			throw new ServiceException(ServiceException.CODE_NO_SUCH_ELEMENT, "No such account");
		}
		if (!EncryptionUtils.checkPassword(password, account.getPassword())) {
			throw new ServiceException(ServiceException.CODE_WRONG_PASSWORD, "Wrong password");
		}
        account.setUnreadNotis(notiMapper.unreadCnt(account.getId()));
		setSession(account, authType);
		return account;
	}

	@Override
	public Account wechatLogin(String openid, Account.Role authType) throws ServiceException {
		Account matcher = new Account();
		matcher.setUnionId(openid);
		matcher.setRole(authType);
		Account account = accountMapper.selectByIdentity(matcher);
		if (account == null) {
			throw new ServiceException(ServiceException.CODE_NO_SUCH_ELEMENT, "No such account");
		}
		setSession(account, authType);
		return account;
	}

	@Override
	public Account logout() {
		Account acc = getAccount();
		HttpSession sess = session();
		sess.removeAttribute(Constant.SESSION_ACCOUNT);
		sess.removeAttribute(Constant.SESSION_ENTITY_ID);
		return acc;
	}
    
}

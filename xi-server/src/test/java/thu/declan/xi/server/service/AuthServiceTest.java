package thu.declan.xi.server.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.AccountMapper;
import thu.declan.xi.server.model.Account;
import static org.junit.Assert.*;
import thu.declan.xi.server.util.CommonUtils;

/**
 *
 * @author declan
 */
@RunWith(SpringJUnit4ClassRunner.class) // 整合 
@ContextConfiguration(locations = "classpath:spring.xml") // 加载配置
public class AuthServiceTest {

    @Autowired
    private AuthService authService;
	
	@Autowired
    private AccountService accountService;
	
	@Autowired
	private AccountMapper accountMapper;
	
    @Test
    public void testAuthService() {
        System.out.println("-------------testAuthService begin------------");
//		final String phone = CommonUtils.randomString(11);
//		final String pwd = CommonUtils.randomString(8);
//		Account acc = new Account();
//		acc.setPhone(phone);
//		acc.setPassword(pwd);
//		acc.setRole(Account.Role.STUDENT);
//		try {
//			accountService.add(acc);
//		} catch (ServiceException ex) {
//			fail("Add account failed: " + ex.getReason());
//			return;
//		}
//		try {
//			acc = authService.login(phone, pwd, Account.Role.STUDENT);
//			assertEquals("phone not equal", acc.getPhone(), phone);
//		} catch (ServiceException ex) {
//			fail("Login failed: " + ex.getReason());
//		} 
//		accountMapper.delete(acc.getId());
        System.out.println("-------------testAuthService end------------");
    }
}

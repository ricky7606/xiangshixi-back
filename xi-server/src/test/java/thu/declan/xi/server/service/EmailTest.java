package thu.declan.xi.server.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author declan
 */
@RunWith(SpringJUnit4ClassRunner.class) // 整合 
@ContextConfiguration(locations = "classpath:spring.xml") // 加载配置
public class EmailTest {

	@Autowired
	private EmailService emailService;

	@Test
	public void testEmailService() {
		System.out.println("-------------testEmailService begin------------");
		emailService.sendEmailInBackground("test", "testEmail", "chenye94@qq.com");
//		emailService.sendEmail("test", "testEmail", "chenye94@qq.com");
		System.out.println("-------------testEmailService end------------");
	}
}

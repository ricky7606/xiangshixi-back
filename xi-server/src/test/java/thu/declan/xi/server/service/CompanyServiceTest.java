package thu.declan.xi.server.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.CompanyMapper;
import thu.declan.xi.server.model.Company;
import static org.junit.Assert.*;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.util.CommonUtils;

/**
 *
 * @companyor declan
 */
@RunWith(SpringJUnit4ClassRunner.class) // 整合 
@ContextConfiguration(locations = "classpath:spring.xml") // 加载配置
public class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;
	
	@Autowired
	private CompanyMapper companyMapper;
    
    @Autowired
    private AccountService accountService;
	
    @Test
    public void testCompanyService() {
        System.out.println("-------------testCompanyService begin------------");
//		final String phone = CommonUtils.randomString(11);
//        Account acc = new Account();
//        acc.setPhone(phone);
//        acc.setPassword(phone);
//        try {
//            accountService.add(acc);
//        } catch (ServiceException ex) {
//            fail("Add account failed: " + ex.getReason());
//			return;
//        }
//        Company comp = new Company();
//		comp.setAccountId(acc.getId());
//		comp.setAddr("addr");
//		comp.setCode("code");
//		comp.setContact("contact");
//		comp.setContactPhone("contactphone");
//		comp.setEmail("email");
//		comp.setIndustry("industry");
//		comp.setIntro("intro");
//		comp.setName("company");
//		comp.setPhone(phone);
//		comp.setScale("scale");
//		comp.setType("type");
//		try {
//			companyService.add(comp);
//		} catch (ServiceException ex) {
//			fail("Add company failed: " + ex.getReason());
//			return;
//		}
//		companyMapper.delete(comp.getId());
        System.out.println("-------------testCompanyService end------------");
    }
}

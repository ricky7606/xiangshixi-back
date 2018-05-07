package thu.declan.xi.server.resource;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Code;
import thu.declan.xi.server.service.CodeService;
import thu.declan.xi.server.service.EmailService;
import thu.declan.xi.server.service.SMSService;

/**
 *
 * @author declan
 */
@Path("codes")
@PermitAll
public class CodeResource extends BaseResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(CodeResource.class);

	@Autowired
	private CodeService codeService;
    
    @Autowired
	private SMSService smsService;
	
	@Autowired
	private EmailService emailService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Code createCode(@QueryParam("phone") String phone) throws ApiException {
		LOGGER.debug("==================== enter CodeResource createCode ====================");
		try {
			Code code = codeService.addRandom(phone);
            smsService.sendCode(phone, code.getCode());
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_DUPLICATE_ELEMENT) {
				throw new ApiException(403, devMsg, "一分钟内已发送过验证码。");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave CodeResource createCode ====================");
		return new Code(phone);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Code verifyCode(@QueryParam("phone") String phone, @QueryParam("code") String code) throws ApiException {
		LOGGER.debug("==================== enter CodeResource verifyCode ====================");
		try {
            Code c = new Code(phone);
            c.setCode(code);
			c = codeService.verify(c);
            return c;
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_VERIFY_FAILED) {
				throw new ApiException(404, devMsg, "验证失败。");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave CodeResource verifyCode ====================");
		return null;
	}
	
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() throws ApiException {
//		emailService.sendEmailInBackground("testTitle", "testBody", "chenye94@qq.com");
		return "test done";
	}

}


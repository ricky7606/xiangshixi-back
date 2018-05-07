package thu.declan.xi.server.resource;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.ListResponse;

/**
 *
 * @author declan
 */
@Path("admins")
@RolesAllowed({Constant.ROLE_ADMIN})
public class AdminResource extends BaseResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminResource.class);

	@POST
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Account createAdmin(@Valid Account acc) throws ApiException {
		LOGGER.debug("==================== enter AdminResource createAdmin ====================");
		if (currentRole() != Account.Role.ADMIN) {
			Account sel = new Account();
			sel.setRole(Account.Role.ADMIN);
			if (accountService.getCount(sel) > 0) {
				throw new ApiException(403, "Already registered admin", "无权限");
			}
		}
		acc.setRole(Account.Role.ADMIN);
		AccountResource accRes = new AccountResource();
		beanFactory.autowireBean(accRes);
		String pwd = acc.getPassword();
		acc = accRes.createAccount(acc);
//		try {
//			authService.login(acc.getPhone(), pwd, Account.Role.ADMIN);
//		} catch (ServiceException ex) {
//			accRes.deleteAccount(acc.getId());
//			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
//			LOGGER.debug(devMsg);
//			handleServiceException(ex);
//		}
		LOGGER.debug("==================== leave AdminResource createAdmin ====================");
		return acc;
	}

	@POST
	@Path("/login")
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Account login(Account acc) throws ApiException {
		acc.setRole(Account.Role.ADMIN);
		acc = loginAccount(acc);
		return acc;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ListResponse<Account> getAdmins() throws ApiException {
		LOGGER.debug("==================== enter AdminResource getAdmins ====================");
		Account selector = new Account();
		selector.setRole(Account.Role.ADMIN);
		List<Account> admins = null;
		try {
			admins = accountService.getList(selector);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave AdminResource getAdmins ====================");
		return new ListResponse(admins);
	}
	
	@PUT
    @Path("/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    // 修改企业信息
    public Account editAccount(@PathParam("accountId") int accountId, Account admin) throws ApiException {
        LOGGER.debug("==================== enter AdminResource editAccount ====================");
        LOGGER.debug("accountId: " + accountId);
        try {
            admin.setId(accountId);
            accountService.update(admin);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该账号不存在！");
            }
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave AdminResource editAccount ====================");
        return admin;
    }

}

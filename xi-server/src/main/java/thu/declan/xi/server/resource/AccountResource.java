package thu.declan.xi.server.resource;

import java.util.HashMap;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Code;
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.News;
import thu.declan.xi.server.model.Notification;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.model.PasswordReseter;
import thu.declan.xi.server.model.PointLog;
import thu.declan.xi.server.service.CodeService;
import thu.declan.xi.server.service.WechatService;

/**
 *
 * @author declan
 */
@Path("accounts")
@RolesAllowed({Constant.ROLE_ADMIN})
public class AccountResource extends BaseResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountResource.class);
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private WechatService wechatService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Account createAccount(@Valid Account account) throws ApiException {
		LOGGER.debug("==================== enter AccountResource createAccount ====================");
//		if (account.getRole().equals(Role.ADMIN)) {
//			throw new ApiException(403, "access forbidden.", "不可以创建管理员账户");
//		}
		try {
			accountService.add(account);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_DUPLICATE_ELEMENT) {
				throw new ApiException(403, devMsg, "该账号已注册，请直接登录。");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave AccountResource createAccount ====================");
		return account;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ListResponse<Account> getAccounts() throws ApiException {
		LOGGER.debug("==================== enter AccountResource getAccounts ====================");
		Account selector = new Account();
		List<Account> accounts = null;
		try {
			accounts = accountService.getList(selector);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave AccountResource getAccounts ====================");
		return new ListResponse(accounts);
	}

	@GET
	@Path("/{accountId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Account getAccount(@PathParam("accountId") int accountId) throws ApiException {
		LOGGER.debug("==================== enter AccountResource getAccount ====================");
		LOGGER.debug("accountId: " + accountId);
		Account account = null;
		try {
			account = accountService.get(accountId);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该账号不存在！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave AccountResource getAccount ====================");
		return account;
	}
    
    @PUT
    @Path("/{accountId}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_COMPANY, Constant.ROLE_STUDENT})
    public Account editAccount(@PathParam("accountId") int accountId, Account updater) throws ApiException {
		LOGGER.debug("==================== enter AccountResource editAccount ====================");
		LOGGER.debug("accountId: " + accountId);
        if (accountId == 0) {
            accountId = currentAccountId();
        }
        if (!Account.Role.ADMIN.equals(currentRole()) && accountId != currentAccountId()) {
            throw new ApiException(403, "account id not match", "权限不足！");
        }
		try {
            updater.setId(accountId);
			accountService.update(updater);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该账号不存在！");
			} else if (ex.getCode() == ServiceException.CODE_DUPLICATE_ELEMENT) {
                throw new ApiException(403, devMsg, "手机号（账号）重复！");
            }
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave AccountResource editAccount ====================");
		return this.getAccount(accountId);
	}
    
    @GET
    @Path("/{accountId}/pointLogs")
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT, Constant.ROLE_COMPANY})
    public ListResponse<News> getPointLogs(@PathParam("accountId") int accountId,
            @QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
        LOGGER.debug("==================== enter AccountResource getPointLogs ====================");
        if (accountId == 0) {
            accountId = currentAccountId();
        }
        PointLog selector = new PointLog();
        selector.setAccountId(accountId);
        Pagination pagination = new Pagination(pageSize, pageIndex);
        List<PointLog> pls = null;
        try {
             pls = plogService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave AccountResource getPointLogs ====================");
        return new ListResponse(pls, pagination);
    }
	
	@PUT
    @Path("/{accountId}/wechat")
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT, Constant.ROLE_COMPANY})
    public Account bindWechat(@PathParam("accountId") int accountId,
			@QueryParam("openid") String openid,
			@QueryParam("unionid") String unionid) throws ApiException {
        LOGGER.debug("==================== enter AccountResource bindWechat ====================");
        if (accountId == 0) {
            accountId = currentAccountId();
        }
        Account updater = new Account();
		updater.setUnionId(unionid);
		updater.setOpenId(openid);
		if (accountService.getCount(updater) > 0) {
			throw new ApiException(403, "Duplicated wechat", "该账号已绑定其他微信号，请在PC端登录账号解除已绑定微信号后重新绑定，谢谢！");
		}
		updater.setId(accountId);
		Account acc = null;
        try {
			accountService.update(updater);
			acc = accountService.get(accountId);
			final String account = acc.getPhone();
			wechatService.sendTemplateMessage(Notification.WX_TPL_ID_BIND, openid, (String)null, 
					new HashMap<String, String>() {{
						put("first", "你已成功绑定 享实习 账号");
						put("keyword1", account);
						put("keyword2", "你可以直接使用该微信登录享实习");
					}});
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave AccountResource bindWechat ====================");
        return acc;
    }

	
	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT, Constant.ROLE_COMPANY})
	public Account logout() throws ApiException {
		return authService.logout();
	}
	
	@PUT
	@PermitAll
	@Path("/resetPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Account resetPassword(@Valid PasswordReseter reset) throws ApiException {
		Account sel = new Account();
		sel.setPhone(reset.getPhone());
		sel.setRole(reset.getRole());
		if (reset.getRole() == null) {
			sel.setRole(Account.Role.STUDENT);
		}
		try {
			Account acc = accountService.getByMatcher(sel);
			Code c = new Code();
			c.setPhone(reset.getPhone());
			c.setCode(reset.getCode());
			codeService.verify(c);
			Account updater = new Account();
			updater.setId(acc.getId());
			updater.setPassword(reset.getNewPassword());
			accountService.update(updater);
			return accountService.get(acc.getId());
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该账号不存在！");
			} else if (ex.getCode() == ServiceException.CODE_VERIFY_FAILED) {
                throw new ApiException(403, devMsg, "验证码错误！");
            }
			handleServiceException(ex);
		}
		return null;
	}
	
    
    public void deleteAccount(int accountId) {
        accountService.delete(accountId);
    }

}


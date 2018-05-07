package thu.declan.xi.server.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Account.Role;
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.Notification;
import thu.declan.xi.server.model.Pagination;

/**
 *
 * @author declan
 */
@Path("notifications")
@RolesAllowed({Constant.ROLE_STUDENT, Constant.ROLE_COMPANY, Constant.ROLE_ADMIN})
public class NotificationResource extends BaseResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationResource.class);
    
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN})
	public Notification sendNotification(Notification noti) throws ApiException {
		notiService.addNoti(noti.getAccountId(), Notification.NType.BACKEND, 0, noti.getMsg());
		return noti;
	}
	
	@POST
	@Path("/broadcast")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN})
	public Notification sendNotifications(Notification noti, @QueryParam("role") List<Role> roles) throws ApiException {
		if (roles == null) {
			roles = new ArrayList<>();
			roles.add(Role.COMPANY);
			roles.add(Role.STUDENT);
		}
		Account sel = new Account();
		sel.setQueryRoles(roles);
		List<Account> accounts = null;
		try {
			accounts = accountService.getList(sel);
			for (Account acc : accounts) {
			notiService.addNoti(acc.getId(), Notification.NType.BACKEND, 0, noti.getMsg());
		}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		return noti;
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Notification> getNotificationList(
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
        LOGGER.debug("==================== enter NotificationResource getNotifications ====================");
        Notification selector = new Notification();
		if (currentRole() == Account.Role.ADMIN) {
			selector.setAccountId(0);
		} else {
			selector.setAccountId(currentAccountId());
		}
        Pagination pagination = new Pagination(pageSize, pageIndex);
        List<Notification> noties = null;
        try {
             noties = notiService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave NotificationResource getNotifications ====================");
        return new ListResponse(noties, pagination);
    }
    
    @GET
    @Path("/{notiId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Notification getNotification(@PathParam("notiId") int notiId) throws ApiException {
        LOGGER.debug("==================== enter NotificationResource getNotification ====================");
        LOGGER.debug("notiId: " + notiId);
        Notification noti = null;
        try {
			noti = notiService.get(notiId);
			if (!noti.isRead()) {
				noti.setRead(true);
				notiService.update(noti);
			}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该通知不存在！");
            }
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave NotificationResource getNotification ====================");
        return noti;
    }
    
}

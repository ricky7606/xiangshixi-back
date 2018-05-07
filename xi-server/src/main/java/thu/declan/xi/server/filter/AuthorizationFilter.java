package thu.declan.xi.server.filter;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ErrorMessage;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Notification;
import thu.declan.xi.server.model.PointLog;
import thu.declan.xi.server.model.PointLog.PType;
import thu.declan.xi.server.service.AuthService;
import thu.declan.xi.server.service.NotificationService;
import thu.declan.xi.server.service.PointLogService;

/**
 *
 * @author declan
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private PointLogService plogService;
	
	@Autowired
	private NotificationService notiService;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        // Let OPTIONS request go. for CORS request.
        // Maybe we should delete this before release.
        if (requestContext.getMethod().equals("OPTIONS")) {
            return;
        }

        Method method = resourceInfo.getResourceMethod();
        Class cls = resourceInfo.getResourceClass();

        boolean permitAll = method.isAnnotationPresent(PermitAll.class);
        boolean denyAll = method.isAnnotationPresent(DenyAll.class);
        Set<String> rolesSet = null;

        if (!denyAll && !permitAll) {
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
            } else {
                denyAll = cls.isAnnotationPresent(DenyAll.class);
                permitAll = cls.isAnnotationPresent(PermitAll.class);
                if (!permitAll && !denyAll && cls.isAnnotationPresent(RolesAllowed.class)) {
                    RolesAllowed rolesAnnotation = (RolesAllowed) cls.getAnnotation(RolesAllowed.class);
                    rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
                }
            }
        }

        if (permitAll) {
            return;
        }

        if (denyAll || rolesSet == null) {
            final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ErrorMessage(new ApiException(403, "Access Forbidden", "Access Forbidden")))
                    .build();
            requestContext.abortWith(ACCESS_FORBIDDEN);
            return;
        }

        final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorMessage(new ApiException(401, "Access Denied", "请登录")))
                .build();

        Account account = authService.getAccount();
        if (account == null) {
            LOGGER.debug("account null");
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }

        Account.Role type = account.getRole();
        if (type == null) {
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }
        if (!rolesSet.contains(type.toString())) {
            requestContext.abortWith(ACCESS_DENIED);
        }

        if (type != Account.Role.ADMIN) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date());
            try {
                long refid = sdf.parse(date).getTime() / 1000;
				PointLog pl = null;
                switch (type) {
                    case COMPANY:
						pl = new PointLog(account.getId(), PType.LOGIN, (int) refid);
                        plogService.addPoint(pl, true);
                    default:
						pl = new PointLog(account.getId(), PType.LOGIN, (int) refid);
                        plogService.addPoint(pl, false);
                }
				notiService.addNoti(pl.getAccountId(), Notification.NType.POINT, pl.getId(), false, Notification.TPL_POINT, pl.getValue());
            } catch (ParseException | ServiceException ex) {
            }
        }

    }

}

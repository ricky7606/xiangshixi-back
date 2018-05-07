package thu.declan.xi.server.resource;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.PointLog;
import thu.declan.xi.server.model.Pagination;

/**
 *
 * @author declan
 */
@Path("pointLogs")
@RolesAllowed({Constant.ROLE_STUDENT, Constant.ROLE_COMPANY})
public class PointLogResource extends BaseResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PointLogResource.class);
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<PointLog> getPointLogList(
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
        LOGGER.debug("==================== enter PointLogResource getPointLoges ====================");
        PointLog selector = new PointLog();
        Pagination pagination = new Pagination(pageSize, pageIndex);
        List<PointLog> ples = null;
        try {
             ples = plogService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PointLogResource getPointLoges ====================");
        return new ListResponse(ples, pagination);
    }
    
    @GET
    @Path("/{plogId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PointLog getPointLog(@PathParam("plogId") int plogId) throws ApiException {
        LOGGER.debug("==================== enter PointLogResource getPointLog ====================");
        LOGGER.debug("plogId: " + plogId);
        PointLog pl = null;
        try {
			pl = plogService.get(plogId);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该通知不存在！");
            }
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PointLogResource getPointLog ====================");
        return pl;
    }
    
}

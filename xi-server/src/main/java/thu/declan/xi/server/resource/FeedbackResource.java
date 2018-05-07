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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.Feedback;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.service.FeedbackService;

/**
 *
 * @author declan
 */
@Path("feedbacks")
@RolesAllowed({Constant.ROLE_ADMIN})
public class FeedbackResource extends BaseResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackResource.class);

    @Autowired
    private FeedbackService fbService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_COMPANY, Constant.ROLE_STUDENT})
    public Feedback createFeedback(@Valid Feedback feedback) throws ApiException {
        LOGGER.debug("==================== enter FeedbackResource createFeedback ====================");
        feedback.setAccountId(currentAccountId());
        try {
            fbService.add(feedback);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave FeedbackResource createFeedback ====================");
        return feedback;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Feedback> getFeedbackList(@QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("state") Feedback.FState state) throws ApiException {
        LOGGER.debug("==================== enter FeedbackResource getFeedbackes ====================");
        Feedback selector = new Feedback();
        selector.setState(state);
        Pagination pagination = new Pagination(pageSize, pageIndex);
        List<Feedback> feedbackes = null;
        try {
            feedbackes = fbService.getList(selector, pagination);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave FeedbackResource getFeedbackes ====================");
        return new ListResponse(feedbackes, pagination);
    }

    @GET
    @PermitAll
    @Path("/{feedbackId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Feedback getFeedback(@PathParam("feedbackId") int feedbackId) throws ApiException {
        LOGGER.debug("==================== enter FeedbackResource getFeedback ====================");
        LOGGER.debug("feedbackId: " + feedbackId);
        Feedback feedback = null;
        try {
            feedback = fbService.get(feedbackId);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该资讯不存在！");
            }
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave FeedbackResource getFeedback ====================");
        return feedback;
    }

    @PUT
    @Path("/{feedbackId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Feedback editFeedback(@PathParam("feedbackId") int feedbackId, Feedback feedback) throws ApiException {
        LOGGER.debug("==================== enter FeedbackResource editFeedback ====================");
        LOGGER.debug("feedbackId: " + feedbackId);
        try {
            feedback.setId(feedbackId);
            fbService.update(feedback);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该反馈不存在！");
            }
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave FeedbackResource editFeedback ====================");
        return feedback;
    }

}

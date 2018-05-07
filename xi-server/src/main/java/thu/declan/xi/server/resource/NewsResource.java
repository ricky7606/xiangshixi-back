package thu.declan.xi.server.resource;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import thu.declan.xi.server.model.News;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.service.NewsService;

/**
 *
 * @author declan
 */
@Path("news")
@RolesAllowed({Constant.ROLE_ADMIN})
public class NewsResource extends BaseResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsResource.class);
    
    @Autowired
    private NewsService newsService;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public News createNews(@Valid News news) throws ApiException {
        LOGGER.debug("==================== enter NewsResource createNews ====================");
        try {
			newsService.add(news);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave NewsResource createNews ====================");
        return news;
    }
    
    @GET
	@PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<News> getNewsList(@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize,
			@QueryParam("published") Boolean published) throws ApiException {
        LOGGER.debug("==================== enter NewsResource getNewses ====================");
        News selector = new News();
		selector.setPublished(published);
        Pagination pagination = new Pagination(pageSize, pageIndex);
        List<News> newses = null;
        try {
             newses = newsService.getList(selector, pagination);
			 if (currentAccountId() != null) {
				 for (News n : newses) {
					newsService.setStarred(currentAccountId(), n);					 
				 }
			}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave NewsResource getNewses ====================");
        return new ListResponse(newses, pagination);
    }
    
    @GET
	@PermitAll
    @Path("/{newsId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public News getNews(@PathParam("newsId") int newsId) throws ApiException {
        LOGGER.debug("==================== enter NewsResource getNews ====================");
        LOGGER.debug("newsId: " + newsId);
        News news = null;
        try {
			newsService.incView(newsId);
			news = newsService.get(newsId);
			if (currentAccountId() != null) {
				newsService.setStarred(currentAccountId(), news);
			}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该资讯不存在！");
            }
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave NewsResource getNews ====================");
        return news;
    }
    
    @PUT
    @Path("/{newsId}")
    @Produces(MediaType.APPLICATION_JSON)
    public News editNews(@PathParam("newsId") int newsId, News news) throws ApiException {
        LOGGER.debug("==================== enter NewsResource editNews ====================");
        LOGGER.debug("newsId: " + newsId);
        try {
            news.setId(newsId);
            newsService.update(news);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该资讯不存在！");
            }
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave NewsResource editNews ====================");
        return news;
    }
	
	@POST
    @Path("/{newsId}/star")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT, Constant.ROLE_COMPANY})
    public News starNews(@PathParam("newsId") int newsId) throws ApiException {
        LOGGER.debug("==================== enter NewsResource starNews ====================");
        LOGGER.debug("newsId: " + newsId);
        News news = null;
        try {
			news = newsService.get(newsId);
			newsService.incStar(currentAccountId(), newsId);
			news.setStarred(true);
			news.setStar(news.getStar()+1);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_UK_CONSTRAINT) {
                throw new ApiException(403, devMsg, "已经点赞！");
            }
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave NewsResource starNews ====================");
        return news;
    }
	
	@DELETE
    @Path("/{newsId}/star")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT, Constant.ROLE_COMPANY})
    public News unstarNews(@PathParam("newsId") int newsId) throws ApiException {
        LOGGER.debug("==================== enter NewsResource unstarNews ====================");
        LOGGER.debug("newsId: " + newsId);
        News news = null;
        try {
			news = newsService.get(newsId);
			newsService.decStar(currentAccountId(), newsId);
			news.setStarred(false);
			news.setStar(news.getStar()-1);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_UK_CONSTRAINT) {
                throw new ApiException(403, devMsg, "已经取消点赞！");
            }
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave NewsResource unstarNews ====================");
        return news;
    }
    
//    @DELETE
//    @Path("/{newsId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public News deleteNews(@PathParam("newsId") int newsId) throws ApiException {
//        LOGGER.debug("==================== enter NewsResource deleteNews ====================");
//        LOGGER.debug("newsId: " + newsId);
//        News news = getNews(newsId);
//		newsService.delete(newsId);
//		LOGGER.debug("==================== leave NewsResource deleteNews ====================");
//        return news;
//    }	
}

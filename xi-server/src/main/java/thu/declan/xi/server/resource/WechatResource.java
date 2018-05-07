package thu.declan.xi.server.resource;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Map;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.service.WechatService;
import thu.declan.xi.server.util.HttpRequest;

/**
 *
 * @author declan
 */
@Path("wechat")
@PermitAll
public class WechatResource extends BaseResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatResource.class);
	
	@Autowired
	private WechatService wechatService;
    
    @GET
    @Path("/accessToken")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAccessToken(@QueryParam("code") String code, @QueryParam("mobile") Boolean mobile) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        String appid, appsecret;
        if (mobile != null && mobile) {
            appid = Constant.WECHAT_APPID;
            appsecret = Constant.WECHAT_SECRET;
        } else {
            appid = Constant.WECHAT_OPEN_APPID;
            appsecret = Constant.WECHAT_OPEN_SECRET;
        }
        String params = String.format("appid=%s&secret=%s&code=%s&grant_type=authorization_code", 
                appid, appsecret, code);
        String ret = HttpRequest.sendGet(url, params);
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode obj = null;
//        try {
//            obj = mapper.readTree(ret);
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(WechatResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return ret;
    }
	
	@GET
	@Path("jssdkSign")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> getJSSDKSign(@QueryParam("url") String url) throws ApiException {
		if (url == null) {
			throw new ApiException(400, "api should be set", "未设置url");
		}
		try {
			url = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error("Unsupported Encoding in getJSSDKSign");
		}
		try {
			return wechatService.getJSSDKSign(url);
		} catch (ServiceException ex) {
			handleServiceException(ex);
		}
		return null;
	}
    
    @GET
    @Path("/redirect/{type}/{id}")
    public Response redirect(@PathParam("type") String type, @PathParam("id") String id,
            @Context UriInfo ui,
			@Context HttpServletRequest request) throws URISyntaxException {
        URI uri = ui.getBaseUri();
		String url = String.format("%s://%s/wechat/redirect.html?type=%s&id=%s", request.getScheme(), uri.getHost(), type, id);
        return Response.temporaryRedirect(new URI(url)).build();
    }
	    
}

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
import thu.declan.xi.server.mapper.MetaMapper;
import thu.declan.xi.server.model.Meta;

/**
 *
 * @author declan
 */
@Path("meta")
@PermitAll
public class MetaResource extends BaseResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetaResource.class);

	@Autowired
	private MetaMapper metaMapper;
    
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Meta createMeta(Meta meta) throws ApiException {
		LOGGER.debug("==================== enter MetaResource createMeta ====================");
		
			if (metaMapper.get(meta.getKey()) == null) {
				metaMapper.insert(meta.getKey(), meta.getValue());
			} else {
				metaMapper.update(meta.getKey(), meta.getValue());
			}
		
		LOGGER.debug("==================== leave MetaResource createMeta ====================");
		return meta;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Meta verifyMeta(@QueryParam("key") String key) throws ApiException {
		LOGGER.debug("==================== enter MetaResource verifyMeta ====================");
		Meta meta = new Meta();
		meta.setKey(key);
		meta.setValue(metaMapper.get(key));
		LOGGER.debug("==================== leave MetaResource verifyMeta ====================");
		return meta;
	}

}


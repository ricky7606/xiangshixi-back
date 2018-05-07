package thu.declan.xi.server.filter;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * Maybe we should delete this before release.
 *
 * @author declan
 */
@Provider
public class CORSResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext crc, ContainerResponseContext crc1) throws IOException {
        MultivaluedMap<String, Object> headers = crc1.getHeaders();
        headers.add("Access-Control-Allow-Origin", "http://localhost:8080");
        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia, Authorization, X-File-Name");
		headers.add("Access-Control-Allow-Credentials", "true");
    }

}

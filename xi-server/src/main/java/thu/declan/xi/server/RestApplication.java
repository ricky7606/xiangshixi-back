package thu.declan.xi.server;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApplication extends ResourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestApplication.class);

    public RestApplication() {

        LOGGER.info("-------------RestApplication begin------------");

        packages("thu.declan.xi.server");

        register(JacksonFeature.class);
        register(MultiPartFeature.class);

        LOGGER.info("-------------RestApplication end------------");
    }
}

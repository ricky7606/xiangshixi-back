package thu.declan.xi.server;

import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author declan
 */
@Configuration
@EnableCaching
public class CacheConfig {

   public final static String CACHE_ACCESS_TOKEN = "cacheOne";
   public final static String CACHE_JSAPI_TICKET = "cacheTwo";

   @Bean
   public Cache cacheOne() {
      return new GuavaCache(CACHE_ACCESS_TOKEN, CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build());
   }

   @Bean
   public Cache cacheTwo() {
      return new GuavaCache(CACHE_JSAPI_TICKET, CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build());
   }
   
}

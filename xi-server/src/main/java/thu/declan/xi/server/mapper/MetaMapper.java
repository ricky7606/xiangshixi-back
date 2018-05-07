package thu.declan.xi.server.mapper;

import org.apache.ibatis.annotations.Param;

/**
 *
 * @author declan
 */
public interface MetaMapper {
	
	public void insert(@Param("key") String key, @Param("value") String value);
	
	public void update(@Param("key") String key, @Param("value") String value);
	
	public String get(String key);
	
}

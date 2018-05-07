package thu.declan.xi.server.mapper;

import org.apache.ibatis.annotations.Param;
import thu.declan.xi.server.model.News;

/**
 *
 * @author declan
 */
public interface NewsMapper extends BaseMapper<News> {
	
	void incViewCnt(int id);
	
	void incStarCnt(int id);
	
	void decStarCnt(int id);
	
	void insertStar(@Param("accountId") int accountId, @Param("id") int id);
	
	void deleteStar(@Param("accountId") int accountId, @Param("id") int id);
	
	Integer selectStar(@Param("accountId") int accountId, @Param("id") int id);
	
}

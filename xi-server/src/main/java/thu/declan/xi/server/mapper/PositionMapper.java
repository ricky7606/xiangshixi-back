package thu.declan.xi.server.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import thu.declan.xi.server.model.Position;

/**
 *
 * @author declan
 */
public interface PositionMapper extends BaseMapper<Position> {
    
	Position selectByCompanyId(int companyId);
    
    void collect(@Param(value="stuId") int stuId, @Param(value="posId") int posId);
	void uncollect(@Param(value="stuId") int stuId, @Param(value="posId") int posId);
	Integer selectCollected(@Param(value="stuId") int stuId, @Param(value="posId") int posId);
	
    List<Position> selectCollectedList(@Param(value="stuId") int stuId);
    List<Position> selectCollectedList(@Param(value="stuId") int stuId, RowBounds bounds);
    
    Integer selectCollectedCount(@Param(value="stuId") int stuId);
	
	
    
}

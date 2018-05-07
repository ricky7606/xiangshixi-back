package thu.declan.xi.server.mapper;

import org.apache.ibatis.annotations.Param;
import thu.declan.xi.server.model.AvgRate;
import thu.declan.xi.server.model.Rate;
import thu.declan.xi.server.model.Resume;

/**
 *
 * @author declan
 */
public interface RateMapper extends BaseMapper<Rate> {
    
	public AvgRate selectAvgRate(Rate sel);
	
	public Rate selectResumeRate(@Param("resume") Resume resume, @Param("direction") Rate.Direction direction);
	
}

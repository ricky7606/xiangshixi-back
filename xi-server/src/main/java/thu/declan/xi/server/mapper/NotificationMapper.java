package thu.declan.xi.server.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import thu.declan.xi.server.model.Notification;

/**
 *
 * @author declan
 */
public interface NotificationMapper extends BaseMapper<Notification> {
	
    public void setRead(@Param("notis") List<Notification> notis);
    
    public Integer unreadCnt(int accountId);
	
}

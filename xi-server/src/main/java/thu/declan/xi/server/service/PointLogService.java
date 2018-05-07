package thu.declan.xi.server.service;

import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.PointLog;

/**
 *
 * @author declan
 */
public interface PointLogService extends BaseTableService<PointLog> {
    
    public void addPoint(PointLog plog, boolean isCompany) throws ServiceException;
            
}

package thu.declan.xi.server.service;

import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Code;

/**
 *
 * @author declan
 */
public interface CodeService extends BaseTableService<Code> {
    
    Code addRandom(String phone) throws ServiceException;
    
    Code verify(Code code) throws ServiceException;
    
}

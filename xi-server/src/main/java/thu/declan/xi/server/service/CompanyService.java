package thu.declan.xi.server.service;

import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Company;

/**
 *
 * @author declan
 */
public interface CompanyService extends BaseTableService<Company> {
	
	public Company getByAccountId(int accountId) throws ServiceException;
    
    public void refreshAvgRate(Integer id);
	
}

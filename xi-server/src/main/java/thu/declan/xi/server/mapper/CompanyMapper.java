package thu.declan.xi.server.mapper;

import thu.declan.xi.server.model.Company;

/**
 *
 * @author declan
 */
public interface CompanyMapper extends BaseMapper<Company> {
    
	Company selectByAccountId(int accountId);
	
}

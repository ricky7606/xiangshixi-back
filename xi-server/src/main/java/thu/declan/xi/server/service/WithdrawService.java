package thu.declan.xi.server.service;

import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Withdraw;

/**
 *
 * @author declan
 */
public interface WithdrawService extends BaseTableService<Withdraw> {
	
	public void payWithdraw(int id) throws ServiceException;
}

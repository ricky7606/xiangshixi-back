package thu.declan.xi.server.service;

import thu.declan.xi.server.model.Salary;
import thu.declan.xi.server.exception.ServiceException;

/**
 *
 * @author declan
 */
public interface SalaryService extends BaseTableService<Salary> {

	public void paySalary(int id) throws ServiceException;

}

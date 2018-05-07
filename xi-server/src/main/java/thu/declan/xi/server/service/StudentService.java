package thu.declan.xi.server.service;

import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Student;

/**
 *
 * @author declan
 */
public interface StudentService extends BaseTableService<Student> {
	
	public Student getByAccountId(int accountId) throws ServiceException;

    public void refreshAvgRate(Integer id);
    
}

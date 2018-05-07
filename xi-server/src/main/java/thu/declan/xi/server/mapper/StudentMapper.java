package thu.declan.xi.server.mapper;

import thu.declan.xi.server.model.Student;

/**
 *
 * @author declan
 */
public interface StudentMapper extends BaseMapper<Student> {
    
	Student selectByAccountId(int accountId);
	
}

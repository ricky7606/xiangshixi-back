package thu.declan.xi.server.mapper;

import java.util.List;
import thu.declan.xi.server.model.Resume;

/**
 *
 * @author declan
 */
public interface ResumeMapper extends BaseMapper<Resume> {
    
	List<Resume> selectByPositionId(int companyId);
	
	List<Resume> selectByStudentId(int studentId);
	
}

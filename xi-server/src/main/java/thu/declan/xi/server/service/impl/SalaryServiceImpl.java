package thu.declan.xi.server.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.AccountMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.mapper.SalaryMapper;
import thu.declan.xi.server.mapper.ResumeMapper;
import thu.declan.xi.server.mapper.StudentMapper;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.model.Salary;
import thu.declan.xi.server.model.Student;
import thu.declan.xi.server.service.SalaryService;

/**
 *
 * @author declan
 */
@Service("salaryService")
public class SalaryServiceImpl extends BaseTableServiceImpl<Salary> implements SalaryService {

	@Autowired
	private SalaryMapper salaryMapper;

	@Autowired
	ResumeMapper resumeMapper;

	@Autowired
	StudentMapper studentMapper;

	@Autowired
	AccountMapper accountMapper;

	@Override
	protected BaseMapper getMapper() {
		return salaryMapper;
	}

	@Override
	protected void preAdd(Salary salary) throws ServiceException {
		Resume r = resumeMapper.selectOne(salary.getResumeId());
		if (r == null) {
			throw new ServiceException(ServiceException.CODE_NO_SUCH_ELEMENT, "No such resume");
		}
		salary.setCompanyId(r.getCompanyId());
		salary.setStuId(r.getStuId());
		salary.setState(Salary.SState.NEW_GENERATED);
	}

	@Override
	protected void postGetList(List<Salary> salaries) {
		for (Salary s : salaries) {
			this.postGet(s);
		}
	}
    
    @Override
    protected void preUpdate(Salary s) {
        s.setUpdateTime(new Date());
    }
	
	@Override
	protected void postUpdate(Salary s) throws ServiceException {
		if (s.getState() == Salary.SState.CONFIRMED || s.getState() == Salary.SState.PAID) {
			this.paySalary(s.getId());
		}
	}

	@Override
	protected void postGet(Salary salary) {
		salary.setResume(resumeMapper.selectOne(salary.getResumeId()));
	}

	@Override
	public void paySalary(int id) throws ServiceException {
		Salary s = get(id);
		Student stu = studentMapper.selectOne(s.getStuId());
		if (stu == null) {
			throw new ServiceException(ServiceException.CODE_NO_SUCH_ELEMENT, "No such student");
		}
		accountMapper.addBalance(stu.getAccountId(), s.getStuValue());
		s.setPayTime(new Date());
		s.setState(Salary.SState.PAID);
		salaryMapper.update(s);
	}

}

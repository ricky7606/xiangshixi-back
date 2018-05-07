package thu.declan.xi.server.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.ResumeMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.mapper.PositionMapper;
import thu.declan.xi.server.mapper.RateMapper;
import thu.declan.xi.server.model.Position;
import thu.declan.xi.server.model.Rate;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.model.Resume.RState;
import thu.declan.xi.server.service.ResumeService;

/**
 *
 * @author declan
 */
@Service("resumeService")
public class ResumeServiceImpl extends BaseTableServiceImpl<Resume> implements ResumeService {

	@Autowired
	ResumeMapper resumeMapper;

	@Autowired
	PositionMapper positionMapper;

	@Autowired
	RateMapper rateMapper;

	@Override
	protected BaseMapper<Resume> getMapper() {
		return resumeMapper;
	}

	@Override
	public void preAdd(Resume resume) throws ServiceException {
		Position pos = positionMapper.selectOne(resume.getPositionId());
		if (pos == null) {
			throw new ServiceException(ServiceException.CODE_NO_SUCH_ELEMENT, "No such position");
		}
		resume.setSalary(pos.getSalary());
		resume.setStuSalary(pos.getStuSalary());
		resume.setUnit(pos.getUnit());
	}

	@Override
	public void preUpdate(Resume update) {

	}

	@Override
	protected void postGetList(List<Resume> resumes) {

	}

	@Override
	protected void postGet(Resume resume) {
		if (resume.getState() == RState.ENDED || resume.getState() == RState.COMMENTED) {
			resume.setRate1(rateMapper.selectResumeRate(resume, Rate.Direction.STU_TO_COMP));
			if (resume.getState() == RState.COMMENTED) {
				resume.setRate2(rateMapper.selectResumeRate(resume, Rate.Direction.COMP_TO_STU));
			}
		}
	}

}

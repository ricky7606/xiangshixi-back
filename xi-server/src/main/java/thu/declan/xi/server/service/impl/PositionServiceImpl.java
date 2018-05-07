package thu.declan.xi.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.PositionMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.mapper.CompanyMapper;
import thu.declan.xi.server.mapper.ResumeMapper;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.model.Position;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.model.Resume.RState;
import thu.declan.xi.server.service.PositionService;

/**
 *
 * @author declan
 */
@Service("positionService")
public class PositionServiceImpl extends BaseTableServiceImpl<Position> implements PositionService {

	@Autowired
	PositionMapper positionMapper;
	
	@Autowired
	ResumeMapper resumeMapper;
	
	@Autowired
	CompanyMapper companyMapper;
	
	@Override
	protected void postGetList(List<Position> positions) {
		Resume sel1 = new Resume();
		sel1.setState(RState.WORKING);
		Resume sel2 = new Resume();
		sel2.setQueryStates(new ArrayList<RState>(){{
			add(RState.NEW);
			add(RState.CONFIRMED);
			add(RState.OFFERED);
			add(RState.WAIT_COMP_CONFIRM);
			add(RState.WAIT_STU_CONFIRM);
		}});
		for (Position pos : positions) {
			sel1.setPositionId(pos.getId());
			pos.setInternCnt(resumeMapper.selectCount(sel1));
			sel2.setPositionId(pos.getId());
			pos.setCandidateCnt(resumeMapper.selectCount(sel2));
		}
	}

	@Override
	protected BaseMapper<Position> getMapper() {
		return positionMapper;
	}
	
	@Override
	public void preAdd(Position position) throws ServiceException {
        if (companyMapper.selectOne(position.getCompanyId()) == null) {
			throw new ServiceException(ServiceException.CODE_FK_CONSTRAINT, "No such company");
		}
		position.setStuSalary((int)(position.getSalary() * (1 - Constant.SERVICE_FEE_RATE)));
	}
	
	@Override
	public void preUpdate(Position update) {
		if (update.getSalary() != null) {
			update.setStuSalary((int)(update.getSalary() * (1 - Constant.SERVICE_FEE_RATE)));
		}
	}

    @Override
    public List<Position> getCollectedList(int stuId, Pagination pagination) throws ServiceException {
        int limit = pagination.getPageSize();
		int offset = (pagination.getPageIndex() - 1) * limit;
		List<Position> objects = positionMapper.selectCollectedList(stuId, new RowBounds(offset, limit));
		int count = positionMapper.selectCollectedCount(stuId);
		pagination.setRowCnt(count);
		pagination.setPageCnt((count - 1) / limit + 1);
		postGetList(objects);
        return objects;
    }

    @Override
    public int getCollectedCount(int stuId) {
        return positionMapper.selectCollectedCount(stuId);
    }

	@Override
	public void collect(int stuId, int id) throws ServiceException {
		if (positionMapper.selectCollected(stuId, id) > 0) {
			throw new ServiceException(ServiceException.CODE_UK_CONSTRAINT, "Already collected");
		}
		positionMapper.collect(stuId, id);
	}

	@Override
	public void uncollect(int stuId, int id) throws ServiceException {
//		if (positionMapper.selectCollected(stuId, id) == 0) {
//			throw new ServiceException(ServiceException.CODE_UK_CONSTRAINT, "Already uncollected");
//		}
		positionMapper.uncollect(stuId, id);
	}

	@Override
	public void setCollected(int stuId, Position pos) {
		pos.setCollected(positionMapper.selectCollected(stuId, pos.getId()) > 0);
	}
	
}

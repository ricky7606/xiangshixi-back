package thu.declan.xi.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.RateMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.model.Rate;
import thu.declan.xi.server.service.RateService;

/**
 *
 * @author declan
 */
@Service("rateService")
public class RateServiceImpl extends BaseTableServiceImpl<Rate> implements RateService {

	@Autowired
	RateMapper rateMapper;
	
	@Override
	protected BaseMapper<Rate> getMapper() {
		return rateMapper;
	}
	
	@Override
	public void preAdd(Rate rate) throws ServiceException {
        Rate sel = new Rate();
		sel.setResumeId(rate.getResumeId());
		sel.setDirection(rate.getDirection());
		if (rateMapper.selectCount(sel) > 0) {
			throw new ServiceException(ServiceException.CODE_UK_CONSTRAINT, "Already rated");
		}
	}
	
	@Override
	public void preUpdate(Rate update) {
		
	}

	
}

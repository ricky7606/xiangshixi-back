package thu.declan.xi.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.AccountMapper;
import thu.declan.xi.server.mapper.PointLogMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.model.PointLog;
import thu.declan.xi.server.service.NotificationService;
import thu.declan.xi.server.service.PointLogService;

/**
 *
 * @author declan
 */
@Service("plogService")
public class PointLogServiceImpl extends BaseTableServiceImpl<PointLog> implements PointLogService {

	@Autowired
	PointLogMapper pointLogMapper;
    
    @Autowired
    AccountMapper accountMapper;
	
	@Autowired
	NotificationService notiService;

	@Override
	protected BaseMapper<PointLog> getMapper() {
		return pointLogMapper;
	}
    
    @Override
	public void preAdd(PointLog pointLog) throws ServiceException {
        PointLog sel = new PointLog(pointLog.getAccountId(), pointLog.getType(), pointLog.getRefId());
        if (pointLogMapper.selectCount(sel) > 0) {
            throw new ServiceException(ServiceException.CODE_DUPLICATE_ELEMENT, "Already added");
        }
        accountMapper.addPoint(pointLog.getAccountId(), pointLog.getValue());
	}
	
	@Override
	public void postAdd(PointLog pointLog) {
		
	}

    @Override
    public void addPoint(PointLog plog, boolean isCompany) throws ServiceException {
        int value = pointValue(plog.getType(), isCompany);
        if (value == 0) {
            return;
        }
        plog.setValue(value);
        add(plog);
    }
    
    private int pointValue(PointLog.PType type, boolean isCompany) {
        if (isCompany) {
            switch (type) {
                case REGISTER:
                case POSITION:
                    return 20;
                case RESUME:
                    return 10;
                case EMPLOY:
                case COMMENT:
                    return 50;
                default:
                    return 0;
            }
        } else {
            switch (type) {
                case REGISTER:
                case LOGIN:
                case PROFILE:
                case COMMENT:
                case STAR5:
                    return 10;
                case EMPLOY:
                case RECOMMEND:
                    return 20;
                default:
                    return 0;
            }
        }
    }
	
}

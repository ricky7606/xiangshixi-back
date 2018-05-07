package thu.declan.xi.server.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.mapper.AccountMapper;
import thu.declan.xi.server.mapper.FeedbackMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.model.Feedback;
import thu.declan.xi.server.service.FeedbackService;

/**
 *
 * @author declan
 */
@Service("fbService")
public class FeedbackServiceImpl extends BaseTableServiceImpl<Feedback> implements FeedbackService {

	@Autowired
	private FeedbackMapper feedbackMapper;
    
    @Autowired
    private AccountMapper accountMapper;

	@Override
	protected BaseMapper getMapper() {
		return feedbackMapper;
	}

	@Override
	protected void postGet(Feedback fb) {
		fb.setAccount(accountMapper.selectOne(fb.getAccountId()));
	}

}

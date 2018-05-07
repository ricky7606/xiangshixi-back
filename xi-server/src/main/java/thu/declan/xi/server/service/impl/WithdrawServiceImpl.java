package thu.declan.xi.server.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.AccountMapper;
import thu.declan.xi.server.mapper.WithdrawMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.mapper.StudentMapper;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Student;
import thu.declan.xi.server.model.Withdraw;
import thu.declan.xi.server.service.WechatService;
import thu.declan.xi.server.service.WithdrawService;

/**
 *
 * @author declan
 */
@Service("withdrawService")
public class WithdrawServiceImpl extends BaseTableServiceImpl<Withdraw> implements WithdrawService {

    @Autowired
    private WithdrawMapper withdrawMapper;

    @Autowired
    private AccountMapper accountMapper;
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private WechatService wechatService;

    @Override
    protected BaseMapper getMapper() {
        return withdrawMapper;
    }

    @Override
    protected void preAdd(Withdraw withdraw) throws ServiceException {
        int accId = withdraw.getAccountId();
		Withdraw sel = new Withdraw();
		sel.setAccountId(accId);
		sel.setState(Withdraw.WState.NEW);
		if (withdrawMapper.selectCount(sel) > 0) {
			throw new ServiceException(ServiceException.CODE_DUPLICATE_ELEMENT, "Already has one withdraw in process");
		}
        Account acc = accountMapper.selectOne(accId);
        double value = withdraw.getValue();
        if (acc.getBalance() < value) {
            throw new ServiceException(ServiceException.CODE_VERIFY_FAILED, "Score not enough");
        }
        accountMapper.addBalance(accId, -value);
    }
    
    @Override
    protected void postUpdate(Withdraw withdraw) throws ServiceException {
        if (withdraw.getState() == Withdraw.WState.REFUSED) {
            Withdraw origin = withdrawMapper.selectOne(withdraw.getId());
            int accId = origin.getAccountId();
            accountMapper.addBalance(accId, origin.getValue());
        } else if (withdraw.getState() == Withdraw.WState.PASSED || withdraw.getState() == Withdraw.WState.PAID) {
			payWithdraw(withdraw.getId());
		}
    }

    @Override
    protected void postGet(Withdraw withdraw) {
        withdraw.setAccount(accountMapper.selectOne(withdraw.getAccountId()));
		withdraw.setStudent(studentMapper.selectByAccountId(withdraw.getAccountId()));
    }

	@Override
	public void payWithdraw(int id) throws ServiceException {
		Withdraw withdraw = get(id);
		Account acc = accountMapper.selectOne(withdraw.getAccountId());
		if (acc == null || acc.getOpenId() == null || acc.getRole() != Account.Role.STUDENT) {
			throw new ServiceException(ServiceException.CODE_VERIFY_FAILED, "Account error");
		}
		Student stu = studentMapper.selectByAccountId(acc.getId());
		if (stu == null) {
			throw new ServiceException(ServiceException.CODE_VERIFY_FAILED, "Student error");
		}
		wechatService.transfer(acc.getOpenId(), stu.getName(), withdraw.getId(), withdraw.getValue());
		withdraw.setState(Withdraw.WState.PAID);
		withdrawMapper.update(withdraw);
	}

}

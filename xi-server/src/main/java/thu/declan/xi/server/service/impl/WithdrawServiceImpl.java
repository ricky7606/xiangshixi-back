package thu.declan.xi.server.service.impl;


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
    
//    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTableServiceImpl.class);

    @Override
    protected void preAdd(Withdraw withdraw) throws ServiceException {
        int accId = withdraw.getAccountId();
		Withdraw sel = new Withdraw();
		sel.setAccountId(accId);
		sel.setState(Withdraw.WState.NEW);
		if (withdrawMapper.selectCount(sel) > 0) { // 查询待处理订单
			throw new ServiceException(ServiceException.CODE_DUPLICATE_ELEMENT, "Already has one withdraw in process");
		}
        Account acc = accountMapper.selectOne(accId); // 获取申请用户
        double value = withdraw.getValue(); // 获取提现金额
        if (acc.getBalance() < value) { // 判断余额是否充足
            throw new ServiceException(ServiceException.CODE_VERIFY_FAILED, "Score not enough");
        }
        accountMapper.addBalance(accId, -value); // 扣除金额
    }
    
    @Override
    protected void postUpdate(Withdraw withdraw) throws ServiceException {
//[id=1, accountId=null, account=null, student=null, value=null, channel=null, state=PASSED, confirmed=null, createTime=null, payTime=null]
        if (withdraw.getState() == Withdraw.WState.REFUSED) { // 拒绝则将金额添加
            Withdraw origin = withdrawMapper.selectOne(withdraw.getId());
            int accId = origin.getAccountId();
            accountMapper.addBalance(accId, origin.getValue());
        } else if (withdraw.getState() == Withdraw.WState.PASSED || withdraw.getState() == Withdraw.WState.PAID) {
			payWithdraw(withdraw.getId());
		}
    }

    @Override
    protected void postGet(Withdraw withdraw) {
        withdraw.setAccount(accountMapper.selectOne(withdraw.getAccountId())); //设置用户信息
		withdraw.setStudent(studentMapper.selectByAccountId(withdraw.getAccountId()));//设置学生信息
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

	@Override
	public void modifyWithdraw(Withdraw wit) throws ServiceException {
		Integer accountId = wit.getAccountId();
		Withdraw sel = new Withdraw();
		Boolean flag = true;
		sel.setAccountId(accountId);
		sel.setState(Withdraw.WState.NEW);
		if (withdrawMapper.selectCount(sel) > 0) { // 查询待处理订单
			flag = false;
			wit.setState(Withdraw.WState.AGAINREFUSED);//设置二次审核失败
			withdrawMapper.update(wit);
			throw new ServiceException(ServiceException.CODE_DUPLICATE_ELEMENT, "Already has one withdraw in process");
		}
		Account acc = accountMapper.selectOne(accountId);
		Double value = wit.getValue();// 提现金额
		if (acc.getBalance() < value) { // 查询余额是否充足
			flag = false;
			wit.setState(Withdraw.WState.AGAINREFUSED);
			withdrawMapper.update(wit); // 修改为二审失败
			throw new ServiceException(ServiceException.CODE_VERIFY_FAILED, "Score not enough");
		}
		if (value < 1) {
			flag = false;
			wit.setState(Withdraw.WState.AGAINREFUSED);
			withdrawMapper.update(wit);
			throw new ServiceException(ServiceException.CODE_EXTERNAL_ERROR, "Unqualified amount");
		}
		if (flag) {
			payWithdraw(wit.getId()); // 调用支付
			accountMapper.addBalance(accountId, -value); // 减去金额
		}
	}

}

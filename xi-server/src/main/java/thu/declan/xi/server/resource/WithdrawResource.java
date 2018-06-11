package thu.declan.xi.server.resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Withdraw;
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.Notification;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.model.Withdraw.WState;
import thu.declan.xi.server.service.WechatService;
import thu.declan.xi.server.service.WithdrawService;

/**
 *
 * @author declan
 */
@Path("withdraws")
@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT})
public class WithdrawResource extends BaseResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawResource.class);

	@Autowired
	private WithdrawService withdrawService;

	@Autowired
	private WechatService wechatService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)// 创建提现申请
	public Withdraw createWithdraw(@Valid Withdraw withdraw) throws ApiException {
		LOGGER.debug("==================== enter WithdrawResource createWithdraw ====================");
		if (currentRole() == Account.Role.STUDENT) {
			withdraw.setAccountId(currentAccountId());
		}
		withdraw.setState(Withdraw.WState.NEW); // 设置状态
		try {
			withdrawService.add(withdraw); // 添加订单
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_VERIFY_FAILED) {
				throw new ApiException(403, devMsg, "可提现余额不足");
			} else if (ex.getCode() == ServiceException.CODE_DUPLICATE_ELEMENT) {
				throw new ApiException(403, devMsg, "已经有正在处理中的提现请求");
			}
			handleServiceException(ex);
		}
		Map<String, String> data = new HashMap<>();
		data.put("first", "您好!您已成功申请了一笔提现");
		data.put("keyword1", String.format("%.2f", withdraw.getValue()));
		data.put("keyword2", (new SimpleDateFormat("YYYY-MM-dd HH:mm")).format(new Date()));
		data.put("remark", "请等待审核通过");
		String openid = currentAccount().getOpenId();
		if (openid != null) {
			try { // 发送微信通知
				wechatService.sendTemplateMessage(Notification.WX_TPL_ID_WITHDRAW, openid, (String) null, data);
			} catch (ServiceException ex) {
			}
		}
		LOGGER.debug("==================== leave WithdrawResource createWithdraw ====================");
		return getWithdraw(withdraw.getId()); // 刷新 并等待审核
	}

	@PUT
	@Path("/{withdrawId}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN}) // PASSED 通过   REFUSED 拒绝
	public Withdraw editWithdraw(@PathParam("withdrawId") int withdrawId, Withdraw updater) throws ApiException {
		LOGGER.debug("==================== enter WithdrawResource editWithdraw ====================");
		LOGGER.debug("withdrawId :" + withdrawId);
		Withdraw oldWd = getWithdraw(withdrawId);//查询上一次申请信息
		//设置不可二次修改 
//		if (oldWd.getState() != Withdraw.WState.NEW) {
//			throw new ApiException(403, "Withdraw state not new", "不能修改此提现申请");
//		}
		updater.setValue(null);
        Withdraw withdraw = null;
		try {
			updater.setId(withdrawId);
//[id=1, accountId=null, account=null, student=null, value=null, channel=null, state=PASSED, confirmed=null, createTime=null, payTime=null]
			if(oldWd.getState() == WState.REFUSED){ // 之前被拒绝的
				againEditWithdraw(oldWd);
			}else{
				// 初审通过
				withdrawService.update(updater); // 通过并支付
			}
            withdraw = withdrawService.get(withdrawId);
            Account acc = withdraw.getAccount(); // 获取用户对象
			if (updater.getState() == WState.PASSED || updater.getState() == WState.PAID) { // 通过申请
				Notification noti = notiService.addNoti(acc.getId(), Notification.NType.WITHDRAW, withdrawId, 
						Notification.TPL_WITHDRAW);
				String openid = acc.getOpenId(); // 获取用户微信id
				Map<String, String> data = new HashMap<>();
				data.put("first", "您好!您的提现已到账");
				data.put("keyword1", String.format("%.2f", withdraw.getValue()));
				data.put("keyword2", (new SimpleDateFormat("YYYY-MM-dd HH:mm")).format(withdraw.getCreateTime()));
				data.put("remark", "感谢您的使用");
				if (openid != null) {
					try { // 微信发送提示消息
						wechatService.sendTemplateMessage(Notification.WX_TPL_ID_WITHDRAW_SUC, openid, noti, data);
					} catch (ServiceException ex) {
					}
				}
			} else if (updater.getState() == WState.REFUSED) { // 拒绝申请  提示用户
				Notification noti = notiService.addNoti(acc.getId(), Notification.NType.WITHDRAW, withdrawId, 
						Notification.TPL_WITHDRAW_FAIL);
				String openid = acc.getOpenId();
				Map<String, String> data = new HashMap<>();
				data.put("first", "您好!您的提现申请失败了");
				data.put("keyword1", String.format("%.2f", withdraw.getValue()));
				data.put("keyword2", (new SimpleDateFormat("YYYY-MM-dd HH:mm")).format(withdraw.getCreateTime()));
				data.put("keyword3", "审核未通过");
				data.put("remark", "请登陆查看具体原因");
				if (openid != null) {
					try { // 微信发送提示消息
						wechatService.sendTemplateMessage(Notification.WX_TPL_ID_WITHDRAW_FAIL, openid, noti, data);
					} catch (ServiceException ex) {
					}
				}
			}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg); // 控制台输入错误信息
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave WithdrawResource editWithdraw ====================");
		return withdraw;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT})
	public ListResponse<Withdraw> getWithdraws(@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize,
			@QueryParam("state") Withdraw.WState state,
			@QueryParam("accountId") Integer accountId) throws ApiException {
		LOGGER.debug("==================== enter WithdrawResource getWithdraws ====================");
		if (currentRole() == Account.Role.STUDENT) {
			accountId = currentAccountId();
		}
		Withdraw selector = new Withdraw();
		selector.setAccountId(accountId);
		selector.setState(state);
		List<Withdraw> withdraws = null;
		Pagination pagination = new Pagination(pageSize, pageIndex);
		try {
			withdraws = withdrawService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave WithdrawResource getWithdraws ====================");
		return new ListResponse(withdraws, pagination);
	}

	@GET
	@Path("/{withdrawId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Withdraw getWithdraw(@PathParam("withdrawId") int withdrawId) throws ApiException {
		LOGGER.debug("==================== enter WithdrawResource getWithdraw ====================");
		LOGGER.debug("withdrawId: " + withdrawId);
		Withdraw withdraw = null;
		try {
			withdraw = withdrawService.get(withdrawId);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该资讯不存在！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave WithdrawResource getWithdraw ====================");
		return withdraw;
	}
	
	@GET
	@Path("/Count/{accountId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int queryExistence(@PathParam("accountId")int accountId) throws ApiException {
		LOGGER.debug("==================== enter WithdrawResource queryExistence ====================");
		LOGGER.debug("accountId: " + accountId);
		Withdraw withdraw = new Withdraw();
		withdraw.setAccountId(accountId);
		withdraw.setState(WState.NEW);
		LOGGER.debug("==================== leave WithdrawResource queryExistence ====================");
		return withdrawService.getCount(withdraw);
	}
	
	/*二审思路
	 *  1. 通过 更改之前订单状态为通过  再次扣除金额   
	 *  (ps: 存在漏洞由于无法限制用户提交次数 将会存在 100 申请-100 初次审核:拒绝  +100  再次申请 -100  此时账户余额为 0  二审通过:-100  则为 -100);
	 *  2. 通过查询若用户有无其他申请记录 无则判断用户余额是否充足  充足:将订单状态设置为通过扣除金额   不足:修改订单为二审失败 将无法对此订单再次操作
	 *     若用户已有其他订单申请 则二审失败 将无法对此订单再次操作  
	 *  (ps: 可行 若此用户又重新发起申请 则将检测到用户存在未处理订单 二审则为失败 若没有其他订单则通过 )
	 */
	private void againEditWithdraw(Withdraw wit) throws ApiException{
		LOGGER.debug("==================== enter WithdrawResource againEditWithdraw ====================");
		Integer withdrawId = wit.getId();
		LOGGER.debug("withdrawId :" + withdrawId);
		wit.setState(Withdraw.WState.PASSED);
		try {
			withdrawService.modifyWithdraw(wit); // 调用二审
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.error(ex.getMessage());
			if (ex.getCode() == ServiceException.CODE_VERIFY_FAILED) { // 108
				throw new ApiException(403, devMsg, "该用户余额已不足，二审无法通过。");
			} else if (ex.getCode() == ServiceException.CODE_DUPLICATE_ELEMENT) { // 103
				throw new ApiException(403, devMsg, "该用户存在其他未处理提现申请，二审无法通过。");
//			} else if (ex.getCode() == ServiceException.CODE_EXTERNAL_ERROR) {
//				throw new ApiException(403, devMsg, "支付失败，单笔最小金额默认为1元，单笔单日限额2W/2W");
			}
			handleServiceException(ex);
		}
		//发送消息通知 二审通过
		Notification noti = notiService.addNoti(wit.getAccountId(), Notification.NType.WITHDRAW, withdrawId, 
				Notification.TPL_WITHDRAW_ADOPT);
		String openid = wit.getAccount().getOpenId(); // 获取用户微信id
		 Map<String, String> data = new HashMap<>();
		data.put("first", "您好!您的提现申请二次审核通过了");
		data.put("keyword1", String.format("%.2f", wit.getValue()));
		data.put("keyword2", (new SimpleDateFormat("YYYY-MM-dd HH:mm")).format(wit.getCreateTime()));
		data.put("remark", "将在5~10分钟内为您划款,请注意后续通知");
		if (openid != null) {
			try { // 微信发送提示消息
				wechatService.sendTemplateMessage(Notification.WX_TPL_ID_WITHDRAW_SUC, openid, noti, data);
			} catch (ServiceException ex) {
			}
		}
		LOGGER.debug("==================== leave WithdrawResource againEditWithdraw ====================");
	}

}

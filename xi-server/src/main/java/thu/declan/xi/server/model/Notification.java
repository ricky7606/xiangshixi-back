package thu.declan.xi.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import thu.declan.xi.server.util.CustomJsonDateSerializer;

/**
 *
 * @author declan
 */
public class Notification {
	
	public final static String TPL_POINT = "恭喜你获得%d个积分";
	public final static String TPL_RESUME_ADD = "简历投递成功，请等待面试通知。";
	public final static String TPL_RESUME_CANCEL = "您的简历与企业相关要求不符，让我们再接再厉吧。";
	public final static String TPL_RESUME_CANCEL2 = "很抱歉，您参加的%s公司面试未通过，让我们继续在享实习寻找机会吧！";
	public final static String TPL_RESUME_CANCEL3 = "学生【%s】拒绝了【%s】职位的录用，继续招聘吧。";
	public final static String TPL_RESUME_INTERVIEW = "你收到来自%s公司的面试邀请，请尽快确认面试时间。";
	public final static String TPL_RESUME_TIME = "您的%s公司面试时间已修改为%s，请准时参加。";
	public final static String TPL_RESUME_TIME2 = "您有新的面试时间修改通知，请及时查看。";
	public final static String TPL_RESUME_TIME3 = "您有新的学生确认了面试时间，请及时查看。";
	public final static String TPL_RESUME_JOIN = "尊敬的【%s】，您应聘的“【%s】”职位已经获得企业的录取通知，请尽快前往查看。";
	public final static String TPL_RESUME_JOIN2 = "恭喜，学生【%s】同意了 【%s】 职位的录用通知，请尽快联系入职吧。";
	public final static String TPL_SALARY_CONFIRM = "您在【%s】工作的%s工资单已生成，请尽快确认。";
	public final static String TPL_SALARY_GET = "您在【%s】工作的%s工资已到账，请登录查看账户余额。";
	public final static String TPL_WITHDRAW = "您的提现申请已通过，请查收。";
	public final static String TPL_WITHDRAW_FAIL = "您的提现申请未通过，提现金额已退回账户余额。";
	public final static String TPL_COMMENT = "实习结束啦，请及时对该公司进行评价。";
	public final static String TPL_RESUME_NEW = "尊敬的【%s】，您发布的“【%s】”职位已有应聘者投递了简历，请尽快前往查看。";
    public final static String TPL_COMPANY_VERIFY_FAIL = "非常抱歉，您的企业账号：【%s】，审核未通过，您需要继续完善企业信息并保存，等待重新审核！";
    public final static String TPL_COMPANY_VERIFY_SUC = "您的企业账号：【%s】，已审核通过，您可以登录享实习网站发布职位啦！";
    
	public final static String TPL_BACK_STUDENT_CREATION = "学生注册通知：您有新的学生用户 【%s】 注册了";
	public final static String TPL_BACK_COMPANY_CREATION = "企业注册通知：您有新的企业用户 【%s】 注册了";
	public final static String TPL_BACK_COMPANY_VERIFY = "企业申请认证通知： 【%s】 申请企业认证请尽快查看。";
	
    public final static String WX_TPL_ID_BIND = "uifypyPxvgSZii0k5sGLcU44xwyM6ZDgDHdv1HnJis0";
	public final static String WX_TPL_ID_INTERVIEW = "iK71frKtJrHcQBUyK9eT1dhVrwLn0ydificjYlRVMkk";
	public final static String WX_TPL_ID_RESUMERET = "7ALGPfxWH8keb-XexxIKAI-In0JDUMdGGGiYbwrlAxg";
	public final static String WX_TPL_ID_WITHDRAW = "XcYl3o7b_4jotYJrC5Z0sO1F2l16f4BPzdQNUHHmSrw";
	public final static String WX_TPL_ID_WITHDRAW_SUC = "XcYl3o7b_4jotYJrC5Z0sO1F2l16f4BPzdQNUHHmSrw";
	public final static String WX_TPL_ID_WITHDRAW_FAIL = "mosxDWmFetZxUmbhmGnCnsOxX393hr-mEepLH-Nr408";
	public final static String WX_TPL_ID_SALARY = "9C8CEri8X_XF9ELVFGUUE--1tGXPzIHRpg6P-uKTlQw";
	public final static String WX_TPL_ID_SALARY_GET = "tt6-9GV33WjFJhQ1DFGvBy1COLmDaGvBJtfffn4pz0k";
	
	
	
    public enum NType {
        POINT, RESUME, SALARY, WITHDRAW, STUDENT, COMPANY, POSITION, RATE, BACKEND;
    }
    
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer accountId;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    private NType type;

    public NType getType() {
        return type;
    }

    public void setType(NType type) {
        this.type = type;
    }

	private Integer refId;

	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private Boolean read;

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    private Date createTime;

	@JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
}

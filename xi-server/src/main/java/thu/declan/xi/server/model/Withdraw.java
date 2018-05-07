package thu.declan.xi.server.model;

import java.util.Date;

/**
 *
 * @author declan
 */
public class Withdraw {
    
    public enum Channel {
        WECHAT;
        
        public static Channel fromString(String str) {
            return Enum.valueOf(Channel.class, str.toUpperCase());
        }   
    }
    
    public enum WState {
        NEW,
        PASSED,
        REFUSED,
        PAID;
        
        public static WState fromString(String str) {
            return Enum.valueOf(WState.class, str.toUpperCase());
        }   
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

    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

	private Student student;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

    private Double value;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    private WState state;

    public WState getState() {
        return state;
    }

    public void setState(WState state) {
        this.state = state;
    }

    private Boolean confirmed;

    public Boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    private Date payTime;

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

}

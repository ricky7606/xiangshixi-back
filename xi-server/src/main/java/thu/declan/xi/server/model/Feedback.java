package thu.declan.xi.server.model;

import java.util.Date;

/**
 *
 * @author declan
 */
public class Feedback {
    
    public enum FState {
        NEW,
        READ,
        DEALT;
        
        public static FState fromString(String str) {
            return Enum.valueOf(FState.class, str.toUpperCase());
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

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private FState state;

    public FState getState() {
        return state;
    }

    public void setState(FState state) {
        this.state = state;
    }

    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
}

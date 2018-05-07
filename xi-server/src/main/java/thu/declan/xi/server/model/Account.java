package thu.declan.xi.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 *
 * @author declan
 */
public class Account {
    
    public enum Role {
        ADMIN,      //管理员
        COMPANY,    //企业
        STUDENT;    //学生//学生
        
        public static Role fromString(String str) {
            return Enum.valueOf(Role.class, str.toUpperCase());
        }
    }
    
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role type) {
        this.role = type;
    }
	
	@JsonIgnore
	private List<Role> queryRoles;

	public List<Role> getQueryRoles() {
		return queryRoles;
	}

	public void setQueryRoles(List<Role> queryRoles) {
		this.queryRoles = queryRoles;
	}	

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String unionId;

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

	private String openId;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Integer point;

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    private Double balance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    private Integer unreadNotis;

    public Integer getUnreadNotis() {
        return unreadNotis;
    }

    public void setUnreadNotis(Integer unreadNotis) {
        this.unreadNotis = unreadNotis;
    }
    
}

package thu.declan.xi.server.model;

/**
 *
 * @author declan
 */
public class Admin {

    public Admin() {
    }

    public Admin(String username) {
        this.username = username;
    }
    
    public enum Role {
        SUPER,      //超级管理员
        FINANCE,    //财务
        CUSTOMER,   //客服
        DELIVERY;   //发货员
        
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

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }    

    private String password2;

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String tel;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
}

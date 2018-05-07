package thu.declan.xi.server.model;

/**
 *
 * @author declan
 */
public class PasswordReseter {

	private String phone;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	private Account.Role role;

	public Account.Role getRole() {
		return role;
	}

	public void setRole(Account.Role role) {
		this.role = role;
	}

	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	private String newPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}	
	
}

package thu.declan.xi.server.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author declan
 */
public class Student {

    public enum Gender {
        MALE,
        FEMALE;
        
        public static Gender fromString(String str) {
            return Enum.valueOf(Gender.class, str.toUpperCase());
        }
		
		public String toChsString() {
			return this == MALE ? "男" : "女";
		}
		
    }
    
    public enum Education {
        COLLEGE,
        BACHELOR,
        MASTER,
        PHD;
        
        public static Education fromString(String str) {
            return Enum.valueOf(Education.class, str.toUpperCase());
        }
		
		public String toChsString() {
			switch (this) {
				case COLLEGE:
					return "大专";
				case BACHELOR:
					return "本科";
				case MASTER:
					return "硕士";
				case PHD:
					return "博士";
			}
			return "";
		}
		
    }
    
    public enum LangLevel {
        NORMAL,
        GOOD,
        SKILLED,
        PROFICIENT;
        
        public static LangLevel fromString(String str) {
            return Enum.valueOf(LangLevel.class, str.toUpperCase());
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

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	private String idNo;

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private Gender gender;

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    private Education education;

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    private String major;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    private String area;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String school;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    
    private LangLevel langLevel;

    public LangLevel getLangLevel() {
        return langLevel;
    }

    public void setLangLevel(LangLevel langLevel) {
        this.langLevel = langLevel;
    }

    private Integer grade;

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    private String stuCard;

    public String getStuCard() {
        return stuCard;
    }

    public void setStuCard(String stuCard) {
        this.stuCard = stuCard;
    }

    private List<String> certs;

    public List<String> getCerts() {
        return certs;
    }

    public void setCerts(List<String> certs) {
        this.certs = certs;
    }

	private String selfEval;

	public String getSelfEval() {
		return selfEval;
	}

	public void setSelfEval(String selfEval) {
		this.selfEval = selfEval;
	}

	private String socialExp;

	public String getSocialExp() {
		return socialExp;
	}

	public void setSocialExp(String socialExp) {
		this.socialExp = socialExp;
	}

	private String workExp;

	public String getWorkExp() {
		return workExp;
	}

	public void setWorkExp(String workExp) {
		this.workExp = workExp;
	}	

    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }    

	private Map<String, String> subscription;

	public Map<String, String> getSubscription() {
		return subscription;
	}

	public void setSubscription(Map<String, String> subscription) {
		this.subscription = subscription;
	}
	
	private Date createTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

    private Boolean frozen;

    public Boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(Boolean frozen) {
        this.frozen = frozen;
    }

    
}

package thu.declan.xi.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import thu.declan.xi.server.util.CustomJsonDateSerializer;

/**
 *
 * @author declan
 */
public class Company extends QueryModel {
    
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

    private String addr;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String scale;

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    private String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

	private String contactPos;

	public String getContactPos() {
		return contactPos;
	}

	public void setContactPos(String contactPos) {
		this.contactPos = contactPos;
	}

    private String contactPhone;

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

	private String avatar;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String industry;

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String intro;

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
	
	private String video;

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}	
	
	private String link;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
    
    private String logo;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    private String cert;

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    private Boolean verified;

    public Boolean isVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    
    private Date createTime;

	@JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
	
	private AvgRate avgRate;

	public AvgRate getAvgRate() {
		return avgRate;
	}

	public void setAvgRate(AvgRate avgRate) {
		this.avgRate = avgRate;
	}

	private Boolean frozen;

	public Boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(Boolean frozen) {
		this.frozen = frozen;
	}

	private Integer priority;

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	private Integer activePosCnt;

	public Integer getActivePosCnt() {
		return activePosCnt;
	}

	public void setActivePosCnt(Integer activePosCnt) {
		this.activePosCnt = activePosCnt;
	}

	private Integer rateCnt;

	public Integer getRateCnt() {
		return rateCnt;
	}

	public void setRateCnt(Integer rateCnt) {
		this.rateCnt = rateCnt;
	}
       
}

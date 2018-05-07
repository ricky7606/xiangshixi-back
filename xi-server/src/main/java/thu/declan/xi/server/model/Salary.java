package thu.declan.xi.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import java.util.List;
import thu.declan.xi.server.util.CustomJsonDateSerializer;

/**
 *
 * @author declan
 */
public class Salary extends QueryModel {
	
	public enum SState {
		NEW_GENERATED,
		WAIT_STU_CONFIRM,
		WAIT_COMP_CONFIRM,
		CONFIRMED,
        PAID;
		
		public static SState fromString(String str) {
            return Enum.valueOf(SState.class, str.toUpperCase());
        }   
	}

	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private Integer resumeId;

	public Integer getResumeId() {
		return resumeId;
	}

	public void setResumeId(Integer resumeId) {
		this.resumeId = resumeId;
	}

	private Resume resume;

	public Resume getResume() {
		return resume;
	}

	public void setResume(Resume resume) {
		this.resume = resume;
	}

	private Integer companyId;

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	private Integer stuId;

	public Integer getStuId() {
		return stuId;
	}

	public void setStuId(Integer stuId) {
		this.stuId = stuId;
	}

	private String stuAccount;

	public String getStuAccount() {
		return stuAccount;
	}

	public void setStuAccount(String stuAccount) {
		this.stuAccount = stuAccount;
	}

	private String month;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	private Double workDays;

	public Double getWorkDays() {
		return workDays;
	}

	public void setWorkDays(Double workDays) {
		this.workDays = workDays;
	}
    
    private Double value;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    private Double stuValue;

    public Double getStuValue() {
        return stuValue;
    }

    private String stuComment;

    public String getStuComment() {
        return stuComment;
    }

    public void setStuComment(String stuComment) {
        this.stuComment = stuComment;
    }

    public void setStuValue(Double stuValue) {
        this.stuValue = stuValue;
    }

	private SState state;

	public SState getState() {
		return state;
	}

	public void setState(SState state) {
		this.state = state;
	}

	private Date createTime;

    @JsonSerialize(using = CustomJsonDateSerializer.class)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

    private Date payTime;

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    private Date updateTime;

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
	
	@JsonIgnore
	private List<SState> queryStates;

	public List<SState> getQueryStates() {
		return queryStates;
	}

	public void setQueryStates(List<SState> queryStates) {
		this.queryStates = queryStates;
	}
    
    public void updateValue(Resume r) {
        double units = this.getWorkDays();
        if (r.getUnit().contains("月")) {
            units = units / 22;
        }
        value = r.getSalary() * units;
        stuValue = r.getStuSalary() * units;
    }
	
	public void updateValue() {
		updateValue(this.resume);
	}
	
}

package thu.declan.xi.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import thu.declan.xi.server.util.CustomJsonDateDeserializer;
import thu.declan.xi.server.util.CustomJsonDateSerializer;

/**
 *
 * @author declan
 */
public class Resume extends QueryModel {
    
    public enum RState {
        NEW,
        WAIT_STU_CONFIRM,
        WAIT_COMP_CONFIRM,
        CONFIRMED,
        OFFERED,
        WORKING,
        ENDED,
        CANCELED,
		COMMENTED;
        
        public static RState fromString(String str) {
            return Enum.valueOf(RState.class, str.toUpperCase());
        }        
    }

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer positionId;

    @NotNull
    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }	

	private Position position;

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@JsonIgnore
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

	private Student student;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
	
    private RState state;

    public RState getState() {
        return state;
    }

    public void setState(RState state) {
        this.state = state;
    }

	private Integer salary;

	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	private Integer stuSalary;

	public Integer getStuSalary() {
		return stuSalary;
	}

	public void setStuSalary(Integer stuSalary) {
		this.stuSalary = stuSalary;
	}

	private String unit;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}	

	private Date interviewTime;

	@JsonSerialize(using = CustomJsonDateSerializer.class)
	public Date getInterviewTime() {
		return interviewTime;
	}

	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	public void setInterviewTime(Date interviewTime) {
		this.interviewTime = interviewTime;
	}

	private String interviewPlace;

	public String getInterviewPlace() {
		return interviewPlace;
	}

	public void setInterviewPlace(String interviewPlace) {
		this.interviewPlace = interviewPlace;
	}

	private String contact;

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	private String contactPhone;

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
	@JsonIgnore
	private List<RState> queryStates;

	public List<RState> getQueryStates() {
		return queryStates;
	}

	public void setQueryStates(List<RState> queryStates) {
		this.queryStates = queryStates;
	}

	private String offerPlace;

	public String getOfferPlace() {
		return offerPlace;
	}

	public void setOfferPlace(String offerPlace) {
		this.offerPlace = offerPlace;
	}

	private Date offerTime;

	@JsonSerialize(using = CustomJsonDateSerializer.class)
	public Date getOfferTime() {
		return offerTime;
	}

	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	public void setOfferTime(Date offerTime) {
		this.offerTime = offerTime;
	}

    private String commentStu;

    public String getCommentStu() {
        return commentStu;
    }

    public void setCommentStu(String commentStu) {
        this.commentStu = commentStu;
    }

    private String commentComp;

    public String getCommentComp() {
        return commentComp;
    }

    public void setCommentComp(String commentComp) {
        this.commentComp = commentComp;
    }

    private Date createTime;

	@JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    private Date endTime;

	@JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
	
	// stu to comp
	private Rate rate1;

	public Rate getRate1() {
		return rate1;
	}

	public void setRate1(Rate rate1) {
		this.rate1 = rate1;
	}
	
	// comp to stu
	private Rate rate2;

	public Rate getRate2() {
		return rate2;
	}

	public void setRate2(Rate rate2) {
		this.rate2 = rate2;
	}

    
}

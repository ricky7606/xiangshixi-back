package thu.declan.xi.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import thu.declan.xi.server.util.CustomJsonDateSerializer;

/**
 *
 * @author declan
 */
public class Rate {

	public enum Direction {
		STU_TO_COMP, COMP_TO_STU;
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

	private Direction direction;

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	private Integer score1;

	public Integer getScore1() {
		return score1;
	}

	public void setScore1(Integer score1) {
		this.score1 = score1;
	}
	
	private Integer score2;

	public Integer getScore2() {
		return score2;
	}

	public void setScore2(Integer score2) {
		this.score2 = score2;
	}
	private Integer score3;

	public Integer getScore3() {
		return score3;
	}

	public void setScore3(Integer score3) {
		this.score3 = score3;
	}
	private Integer score4;

	public Integer getScore4() {
		return score4;
	}

	public void setScore4(Integer score4) {
		this.score4 = score4;
	}
	private Integer score5;

	public Integer getScore5() {
		return score5;
	}

	public void setScore5(Integer score5) {
		this.score5 = score5;
	}

	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

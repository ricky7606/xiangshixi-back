package thu.declan.xi.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import thu.declan.xi.server.util.CustomJsonDateSerializer;

/**
 *
 * @author declan
 */
public class PointLog {

    public PointLog() {
    }

    public enum PType {
        REGISTER, //注册
        PROFILE, //完善资料（简历）
        LOGIN, //登录
        POSITION, //企业添加职位
        RESUME, //企业浏览简历
        EMPLOY, //企业录用学生、学生被录用
        COMMENT, //评价
        STAR5, //学生收到企业五星评价
        RECOMMEND; //学生推荐朋友注册成功
        
        public static PType fromString(String str) {
            return Enum.valueOf(PType.class, str.toUpperCase());
        }
    }

    public PointLog(Integer accountId, PType type, Integer refId) {
        this.accountId = accountId;
        this.type = type;
        this.refId = refId;
    }

    public PointLog(Integer accountId, PType type, Integer value, Integer refId) {
        this.accountId = accountId;
        this.type = type;
        this.value = value;
        this.refId = refId;
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

    private PType type;

    public PType getType() {
        return type;
    }

    public void setType(PType type) {
        this.type = type;
    }

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    private Integer refId;

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
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

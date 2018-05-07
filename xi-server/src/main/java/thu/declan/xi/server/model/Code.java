package thu.declan.xi.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import thu.declan.xi.server.util.CustomJsonDateSerializer;

/**
 *
 * @author declan
 */
public class Code {

    public Code() {
    }

    public Code(String phone) {
        this.phone = phone;
    }

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

package thu.declan.xi.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import thu.declan.xi.server.util.CustomJsonDateSerializer;

/**
 *
 * @author declan
 */
public class News {

	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String author;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	private Integer view;

	public Integer getView() {
		return view;
	}

	public void setView(Integer view) {
		this.view = view;
	}

	private Integer star;

	public Integer getStar() {
		return star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	private Boolean published;

	public Boolean isPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	private Date createTime;

	@JsonSerialize(using = CustomJsonDateSerializer.class)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	private Boolean starred;

	public Boolean isStarred() {
		return starred;
	}

	public void setStarred(Boolean starred) {
		this.starred = starred;
	}
	
}

package thu.declan.xi.server.model;

import java.util.List;

/**
 *
 * @author declan
 * @param <T>
 */
public class ListResponse<T> {

	public ListResponse() {
	}

	public ListResponse(List<T> items) {
		this.items = items;
	}
	
	public ListResponse(List<T> items, Pagination pagination) {
		this.pagination = pagination;
		this.items = items;
	}

    private Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
    
}

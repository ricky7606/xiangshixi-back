package thu.declan.xi.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author declan
 */
public class QueryModel {
    
    public static final String TIME_START = "timeBoundLow";
    public static final String TIME_END = "timeBoundHigh";
	
    public static final String SORT_KEY = "sortKey";
    public static final String SORT_DIR = "sortDir";
    
    public static final String SORT_DIR_ASC = "asc";
    public static final String SORT_DIR_DESC = "desc";
	
	public static final String SEARCH_KEY = "searchKey";
	
	@JsonIgnore
	private Map queryParams = new HashMap();

	public Map getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map queryParams) {
		this.queryParams = queryParams;
	}
	
	public void setQueryParam(String key, Object value) {
		queryParams.put(key, value);
	}
	
}
